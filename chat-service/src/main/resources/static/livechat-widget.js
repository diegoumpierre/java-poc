/**
 * 101 LiveChat Embeddable Widget
 *
 * Usage:
 * <script
 *   src="https://your-domain/livechat-widget.js"
 *   data-tenant-id="uuid"
 *   data-source="helpdesk"
 *   data-queue="support"
 * ></script>
 */
(function() {
    'use strict';

    if (window.__101LiveChatWidget) return;
    window.__101LiveChatWidget = true;

    // -------------------------------------------------------
    // Configuration from script tag
    // -------------------------------------------------------
    var scriptTag = document.currentScript || (function() {
        var scripts = document.getElementsByTagName('script');
        for (var i = scripts.length - 1; i >= 0; i--) {
            if (scripts[i].src && scripts[i].src.indexOf('livechat-widget.js') !== -1) {
                return scripts[i];
            }
        }
        return null;
    })();

    if (!scriptTag) {
        console.error('LiveChat Widget: Could not find script tag');
        return;
    }

    var TENANT_ID = scriptTag.getAttribute('data-tenant-id');
    var SOURCE_SERVICE = scriptTag.getAttribute('data-source') || '';
    var QUEUE_ID = scriptTag.getAttribute('data-queue') || '';
    var CUSTOM_PRIMARY_COLOR = scriptTag.getAttribute('data-color') || '';
    var CUSTOM_POSITION = scriptTag.getAttribute('data-position') || '';

    if (!TENANT_ID) {
        console.error('LiveChat Widget: data-tenant-id is required');
        return;
    }

    var scriptSrc = scriptTag.src;
    var API_BASE = scriptSrc.replace(/\/livechat-widget\.js.*$/, '');

    // -------------------------------------------------------
    // Constants
    // -------------------------------------------------------
    var SESSION_TOKEN_KEY = 'livechat_session_' + TENANT_ID;
    var VISITOR_ID_KEY = 'livechat_visitor_' + TENANT_ID;
    var POLL_INTERVAL = 3000;

    // -------------------------------------------------------
    // State
    // -------------------------------------------------------
    var state = {
        view: 'closed',
        config: null,
        session: null,
        messages: [],
        unreadCount: 0,
        agentTyping: false,
        wsConnected: false,
        isLoading: false,
        isSending: false,
        ratingValue: 0,
        ratingSubmitted: false
    };

    var stompClient = null;
    var pollTimer = null;
    var typingTimeout = null;
    var container = null;

    // -------------------------------------------------------
    // Utility functions
    // -------------------------------------------------------
    function getOrCreateVisitorId() {
        var id = localStorage.getItem(VISITOR_ID_KEY);
        if (!id) {
            id = 'v_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
            localStorage.setItem(VISITOR_ID_KEY, id);
        }
        return id;
    }

    function formatTime(dateStr) {
        try {
            var d = new Date(dateStr);
            return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
        } catch (e) {
            return '';
        }
    }

    function escapeHtml(str) {
        if (!str) return '';
        var div = document.createElement('div');
        div.appendChild(document.createTextNode(str));
        return div.innerHTML;
    }

    // -------------------------------------------------------
    // API calls
    // -------------------------------------------------------
    function apiCall(method, path, body, extraHeaders) {
        var headers = { 'Content-Type': 'application/json' };
        if (extraHeaders) {
            for (var k in extraHeaders) headers[k] = extraHeaders[k];
        }
        var opts = { method: method, headers: headers };
        if (body) opts.body = JSON.stringify(body);
        return fetch(API_BASE + path, opts).then(function(res) {
            if (!res.ok) throw new Error('HTTP ' + res.status);
            var ct = res.headers.get('content-type') || '';
            if (ct.indexOf('json') !== -1) return res.json();
            return null;
        });
    }

    function loadConfig() {
        var qs = SOURCE_SERVICE ? '?sourceService=' + encodeURIComponent(SOURCE_SERVICE) : '';
        return apiCall('GET', '/api/chat/livechat/widget/config' + qs, null, { 'X-Tenant-Id': TENANT_ID });
    }

    function startChat(visitorName, visitorEmail, initialMessage) {
        return apiCall('POST', '/api/chat/livechat/widget/start', {
            visitorId: getOrCreateVisitorId(),
            visitorName: visitorName,
            visitorEmail: visitorEmail || undefined,
            pageUrl: window.location.href,
            initialMessage: initialMessage || undefined,
            queueId: QUEUE_ID || undefined,
            sourceService: SOURCE_SERVICE || 'DIRECT'
        }, { 'X-Tenant-Id': TENANT_ID });
    }

    function resumeSession(token) {
        return apiCall('GET', '/api/chat/livechat/widget/session/' + encodeURIComponent(token));
    }

    function sendMessage(token, content) {
        return apiCall('POST', '/api/chat/livechat/widget/session/' + encodeURIComponent(token) + '/messages', {
            content: content,
            messageType: 'TEXT'
        });
    }

    function getMessages(token) {
        return apiCall('GET', '/api/chat/livechat/widget/session/' + encodeURIComponent(token) + '/messages');
    }

    function endChat(token) {
        return apiCall('POST', '/api/chat/livechat/widget/session/' + encodeURIComponent(token) + '/end');
    }

    function rateChat(token, rating, feedback) {
        var qs = '?rating=' + rating;
        if (feedback) qs += '&feedback=' + encodeURIComponent(feedback);
        return apiCall('POST', '/api/chat/livechat/widget/session/' + encodeURIComponent(token) + '/rate' + qs);
    }

    // -------------------------------------------------------
    // Polling fallback (when WebSocket unavailable)
    // -------------------------------------------------------
    function startPolling() {
        stopPolling();
        if (!state.session) return;
        pollTimer = setInterval(function() {
            getMessages(state.session.sessionToken).then(function(msgs) {
                if (msgs && msgs.length !== state.messages.length) {
                    var oldLen = state.messages.length;
                    state.messages = msgs;
                    if (msgs.length > oldLen && state.view === 'closed') {
                        state.unreadCount += msgs.length - oldLen;
                    }
                    render();
                }
            }).catch(function() {});
        }, POLL_INTERVAL);
    }

    function stopPolling() {
        if (pollTimer) { clearInterval(pollTimer); pollTimer = null; }
    }

    // -------------------------------------------------------
    // WebSocket (SockJS + STOMP)
    // -------------------------------------------------------
    function connectWebSocket(token) {
        if (typeof SockJS === 'undefined') {
            var s = document.createElement('script');
            s.src = 'https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js';
            s.onload = function() { doConnectWs(token); };
            s.onerror = function() { startPolling(); };
            document.head.appendChild(s);
            return;
        }
        doConnectWs(token);
    }

    function doConnectWs(token) {
        if (typeof StompJs === 'undefined') {
            var s = document.createElement('script');
            s.src = 'https://cdn.jsdelivr.net/npm/@stomp/stompjs@7/bundles/stomp.umd.min.js';
            s.onload = function() { doConnectWs(token); };
            s.onerror = function() { startPolling(); };
            document.head.appendChild(s);
            return;
        }

        try {
            var wsUrl = API_BASE + '/ws/livechat?sessionToken=' + encodeURIComponent(token);
            stompClient = new StompJs.Client({
                webSocketFactory: function() { return new SockJS(wsUrl); },
                reconnectDelay: 5000,
                heartbeatIncoming: 25000,
                heartbeatOutgoing: 25000,
                debug: function() {},
                onConnect: function() {
                    state.wsConnected = true;
                    stopPolling();
                    render();
                    stompClient.subscribe('/topic/livechat.session.' + token, function(message) {
                        try { handleWsMessage(JSON.parse(message.body)); } catch (e) {}
                    });
                },
                onDisconnect: function() { state.wsConnected = false; startPolling(); render(); },
                onStompError: function() { state.wsConnected = false; startPolling(); },
                onWebSocketClose: function() { state.wsConnected = false; startPolling(); }
            });
            stompClient.activate();
        } catch (e) {
            startPolling();
        }
    }

    function disconnectWebSocket() {
        if (stompClient) { try { stompClient.deactivate(); } catch (e) {} stompClient = null; }
        state.wsConnected = false;
    }

    function handleWsMessage(data) {
        if (data.id && data.senderType) {
            var exists = state.messages.some(function(m) { return m.id === data.id; });
            if (!exists) {
                state.messages.push(data);
                if (data.senderType !== 'VISITOR' && state.view === 'closed') state.unreadCount++;
                render();
                scrollToBottom();
            }
            return;
        }

        var type = data.type;
        if (type === 'agent_joined') {
            state.session.status = 'ACTIVE';
            state.session.agentName = data.agentName;
            render();
        } else if (type === 'session_closed') {
            state.session.status = 'CLOSED';
            state.view = 'rating';
            disconnectWebSocket(); stopPolling(); render();
        } else if (type === 'agent_typing') {
            state.agentTyping = true; render();
            if (typingTimeout) clearTimeout(typingTimeout);
            typingTimeout = setTimeout(function() { state.agentTyping = false; render(); }, 4000);
        } else if (type === 'session_abandoned') {
            state.session.status = 'ABANDONED';
            state.view = 'ended';
            disconnectWebSocket(); stopPolling(); render();
        } else if (type === 'agent_changed') {
            state.session.agentName = data.agentName; render();
        }
    }

    // -------------------------------------------------------
    // Actions
    // -------------------------------------------------------
    function handleOpen() {
        if (state.session && (state.session.status === 'WAITING' || state.session.status === 'ACTIVE')) {
            state.view = 'chat'; state.unreadCount = 0;
        } else {
            state.view = 'prechat';
        }
        render();
    }

    function handleStartChat() {
        var nameInput = container.querySelector('#lc-name-input');
        var emailInput = container.querySelector('#lc-email-input');
        var name = nameInput ? nameInput.value.trim() : '';
        var email = emailInput ? emailInput.value.trim() : '';
        if (!name) return;
        if (state.config && state.config.requireEmail && !email) return;

        state.isLoading = true; render();

        startChat(name, email).then(function(session) {
            state.session = session;
            state.messages = [];
            localStorage.setItem(SESSION_TOKEN_KEY, session.sessionToken);
            state.view = 'chat'; state.isLoading = false; render();
            connectWebSocket(session.sessionToken);
            getMessages(session.sessionToken).then(function(msgs) {
                state.messages = msgs || []; render(); scrollToBottom();
            });
        }).catch(function() { state.isLoading = false; render(); });
    }

    function handleSendMessage() {
        var input = container.querySelector('#lc-msg-input');
        if (!input) return;
        var content = input.value.trim();
        if (!content || !state.session || state.isSending) return;
        input.value = '';
        state.isSending = true; render();

        sendMessage(state.session.sessionToken, content).then(function(msg) {
            var exists = state.messages.some(function(m) { return m.id === msg.id; });
            if (!exists) state.messages.push(msg);
            state.isSending = false; render(); scrollToBottom();
            if (input) input.focus();
        }).catch(function() {
            input.value = content; state.isSending = false; render();
        });
    }

    function handleEndChat() {
        if (!state.session) return;
        endChat(state.session.sessionToken).then(function() {
            state.session.status = 'CLOSED'; state.view = 'rating';
            disconnectWebSocket(); stopPolling(); render();
        }).catch(function() {});
    }

    function handleSubmitRating() {
        if (!state.session || state.ratingValue === 0) return;
        var feedbackEl = container.querySelector('#lc-feedback-input');
        var feedback = feedbackEl ? feedbackEl.value.trim() : '';
        rateChat(state.session.sessionToken, state.ratingValue, feedback).then(function() {
            state.ratingSubmitted = true; render();
            setTimeout(function() { cleanup(); state.view = 'ended'; render(); }, 2000);
        }).catch(function() {});
    }

    function handleMinimize() { state.view = 'closed'; render(); }

    function cleanup() {
        localStorage.removeItem(SESSION_TOKEN_KEY);
        disconnectWebSocket(); stopPolling();
        state.session = null; state.messages = []; state.ratingValue = 0;
        state.ratingSubmitted = false; state.unreadCount = 0; state.agentTyping = false;
    }

    function scrollToBottom() {
        setTimeout(function() {
            var msgArea = container.querySelector('.lc-messages');
            if (msgArea) msgArea.scrollTop = msgArea.scrollHeight;
        }, 50);
    }

    // -------------------------------------------------------
    // Styles
    // -------------------------------------------------------
    function getStyles() {
        var pc = (state.config && state.config.primaryColor) || '#4F46E5';
        if (CUSTOM_PRIMARY_COLOR) pc = CUSTOM_PRIMARY_COLOR;

        return '.lc-widget *{box-sizing:border-box;margin:0;padding:0;font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif}' +
        '.lc-widget{position:fixed;z-index:99999}' +
        '.lc-widget-br{bottom:24px;right:24px}.lc-widget-bl{bottom:24px;left:24px}' +
        '.lc-fab{width:60px;height:60px;border-radius:50%;border:none;cursor:pointer;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(0,0,0,.15);transition:transform .2s,box-shadow .2s;position:relative;background:' + pc + ';color:#fff}' +
        '.lc-fab:hover{transform:scale(1.05);box-shadow:0 6px 20px rgba(0,0,0,.2)}' +
        '.lc-fab svg{width:28px;height:28px;fill:currentColor}' +
        '.lc-badge{position:absolute;top:-4px;right:-4px;background:#ef4444;color:#fff;border-radius:50%;width:22px;height:22px;font-size:11px;font-weight:700;display:flex;align-items:center;justify-content:center}' +
        '.lc-window{width:380px;height:560px;background:#fff;border-radius:16px;box-shadow:0 8px 30px rgba(0,0,0,.12);display:flex;flex-direction:column;overflow:hidden;animation:lcSlideUp .3s ease}' +
        '@keyframes lcSlideUp{from{opacity:0;transform:translateY(20px)}to{opacity:1;transform:translateY(0)}}' +
        '.lc-header{padding:16px;display:flex;align-items:center;justify-content:space-between;flex-shrink:0;background:' + pc + ';color:#fff}' +
        '.lc-header-info{display:flex;align-items:center;gap:10px}' +
        '.lc-header-avatar{width:36px;height:36px;border-radius:50%;background:rgba(255,255,255,.2);display:flex;align-items:center;justify-content:center}' +
        '.lc-header-avatar svg{width:20px;height:20px;fill:#fff}' +
        '.lc-header-title{font-size:14px;font-weight:600}.lc-header-subtitle{font-size:11px;opacity:.8}' +
        '.lc-header-actions{display:flex;gap:4px}' +
        '.lc-header-btn{background:none;border:none;color:#fff;cursor:pointer;width:32px;height:32px;border-radius:50%;display:flex;align-items:center;justify-content:center;transition:background .2s}' +
        '.lc-header-btn:hover{background:rgba(255,255,255,.2)}' +
        '.lc-header-btn svg{width:16px;height:16px;fill:currentColor}' +
        '.lc-online-dot{display:inline-block;width:8px;height:8px;border-radius:50%;background:#22c55e;margin-left:6px;vertical-align:middle}' +
        '.lc-prechat{flex:1;padding:24px;display:flex;flex-direction:column;justify-content:center;gap:16px}' +
        '.lc-prechat-header{text-align:center}' +
        '.lc-prechat h3{font-size:18px;color:#111;margin-bottom:4px}.lc-prechat p{font-size:13px;color:#666}' +
        '.lc-form{display:flex;flex-direction:column;gap:12px}' +
        '.lc-input{width:100%;padding:10px 12px;border:1px solid #d1d5db;border-radius:8px;font-size:14px;outline:none;transition:border-color .2s}' +
        '.lc-input:focus{border-color:' + pc + ';box-shadow:0 0 0 3px ' + pc + '22}' +
        '.lc-btn{padding:10px 16px;border:none;border-radius:8px;font-size:14px;font-weight:600;cursor:pointer;transition:opacity .2s,transform .1s}' +
        '.lc-btn:active{transform:scale(.98)}' +
        '.lc-btn-primary{background:' + pc + ';color:#fff}.lc-btn-primary:hover{opacity:.9}.lc-btn-primary:disabled{opacity:.5;cursor:not-allowed}' +
        '.lc-btn-text{background:none;color:#666}.lc-btn-text:hover{color:#333}' +
        '.lc-messages{flex:1;padding:12px;overflow-y:auto;background:#f9fafb}' +
        '.lc-msg{margin-bottom:12px;display:flex}' +
        '.lc-msg-visitor{justify-content:flex-end}.lc-msg-agent{justify-content:flex-start}.lc-msg-system{justify-content:center}' +
        '.lc-msg-bubble{max-width:75%;padding:10px 14px;border-radius:16px;font-size:13px;line-height:1.4;word-break:break-word;white-space:pre-wrap}' +
        '.lc-msg-visitor .lc-msg-bubble{background:' + pc + ';color:#fff;border-bottom-right-radius:4px}' +
        '.lc-msg-agent .lc-msg-bubble{background:#fff;color:#111;border:1px solid #e5e7eb;border-bottom-left-radius:4px}' +
        '.lc-msg-system .lc-msg-bubble{background:transparent;color:#9ca3af;font-size:11px;padding:4px 12px}' +
        '.lc-msg-sender{font-size:11px;font-weight:600;margin-bottom:2px;color:' + pc + '}' +
        '.lc-msg-time{font-size:10px;margin-top:4px;opacity:.6}' +
        '.lc-msg-visitor .lc-msg-time{text-align:right}' +
        '.lc-typing{display:flex;align-items:center;gap:4px;padding:8px 14px;background:#fff;border-radius:16px;border:1px solid #e5e7eb;margin-bottom:12px;max-width:120px}' +
        '.lc-typing-dot{width:6px;height:6px;background:#9ca3af;border-radius:50%;animation:lcTyping 1.4s infinite ease-in-out both}' +
        '.lc-typing-dot:nth-child(2){animation-delay:-.32s}.lc-typing-dot:nth-child(3){animation-delay:-.16s}' +
        '@keyframes lcTyping{0%,80%,100%{transform:scale(.6);opacity:.6}40%{transform:scale(1);opacity:1}}' +
        '.lc-input-area{padding:12px;border-top:1px solid #e5e7eb;background:#fff;display:flex;gap:8px;align-items:flex-end;flex-shrink:0}' +
        '.lc-input-area textarea{flex:1;padding:8px 12px;border:1px solid #d1d5db;border-radius:8px;font-size:13px;resize:none;outline:none;max-height:80px;min-height:36px;line-height:1.4;font-family:inherit}' +
        '.lc-input-area textarea:focus{border-color:' + pc + '}' +
        '.lc-send-btn{width:36px;height:36px;border-radius:50%;border:none;cursor:pointer;display:flex;align-items:center;justify-content:center;background:' + pc + ';color:#fff;transition:opacity .2s;flex-shrink:0}' +
        '.lc-send-btn:hover{opacity:.9}.lc-send-btn:disabled{opacity:.4;cursor:not-allowed}' +
        '.lc-send-btn svg{width:16px;height:16px;fill:currentColor}' +
        '.lc-rating{flex:1;padding:24px;display:flex;flex-direction:column;align-items:center;justify-content:center;gap:16px;text-align:center}' +
        '.lc-rating h3{font-size:18px;color:#111}.lc-rating p{font-size:13px;color:#666}' +
        '.lc-stars{display:flex;gap:8px}' +
        '.lc-star{font-size:32px;cursor:pointer;color:#d1d5db;transition:color .2s,transform .1s;background:none;border:none;padding:0}' +
        '.lc-star:hover,.lc-star.active{color:#fbbf24;transform:scale(1.1)}' +
        '.lc-rating textarea{width:100%;padding:10px;border:1px solid #d1d5db;border-radius:8px;font-size:13px;resize:none;outline:none;max-height:80px;font-family:inherit}' +
        '.lc-rating-btns{display:flex;gap:8px;width:100%}.lc-rating-btns .lc-btn{flex:1}' +
        '.lc-waiting{flex:1;display:flex;flex-direction:column;align-items:center;justify-content:center;color:#9ca3af;gap:12px}' +
        '.lc-spinner{width:32px;height:32px;border:3px solid #e5e7eb;border-top-color:' + pc + ';border-radius:50%;animation:lcSpin .8s linear infinite}' +
        '@keyframes lcSpin{to{transform:rotate(360deg)}}' +
        '@media(max-width:440px){.lc-window{width:calc(100vw - 16px);height:calc(100vh - 80px);border-radius:12px}.lc-widget-br{right:8px;bottom:8px}.lc-widget-bl{left:8px;bottom:8px}}';
    }

    // -------------------------------------------------------
    // SVG Icons
    // -------------------------------------------------------
    var ICONS = {
        chat: '<svg viewBox="0 0 24 24"><path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm0 14H5.17L4 17.17V4h16v12z"/><path d="M7 9h2v2H7zm4 0h2v2h-2zm4 0h2v2h-2z"/></svg>',
        minimize: '<svg viewBox="0 0 24 24"><path d="M19 13H5v-2h14v2z"/></svg>',
        send: '<svg viewBox="0 0 24 24"><path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/></svg>',
        headset: '<svg viewBox="0 0 24 24"><path d="M12 1c-4.97 0-9 4.03-9 9v7c0 1.66 1.34 3 3 3h3v-8H5v-2c0-3.87 3.13-7 7-7s7 3.13 7 7v2h-4v8h3c1.66 0 3-1.34 3-3v-7c0-4.97-4.03-9-9-9z"/></svg>',
        power: '<svg viewBox="0 0 24 24"><path d="M13 3h-2v10h2V3zm4.83 2.17l-1.42 1.42C17.99 7.86 19 9.81 19 12c0 3.87-3.13 7-7 7s-7-3.13-7-7c0-2.19 1.01-4.14 2.58-5.42L6.17 5.17C4.23 6.82 3 9.26 3 12c0 4.97 4.03 9 9 9s9-4.03 9-9c0-2.74-1.23-5.18-3.17-6.83z"/></svg>'
    };

    // -------------------------------------------------------
    // Render
    // -------------------------------------------------------
    function render() {
        if (!container || !state.config) return;
        if (!state.config.enabled) { container.innerHTML = ''; return; }

        var pos = CUSTOM_POSITION || (state.config.position === 'BOTTOM_LEFT' ? 'bl' : 'br');
        var posClass = 'lc-widget-' + pos;
        var html = '';

        if (state.view === 'closed' || state.view === 'ended') {
            html = '<div class="lc-widget ' + posClass + '">' +
                '<button class="lc-fab" onclick="window.__lcOpen()" aria-label="Open chat">' +
                ICONS.chat +
                (state.unreadCount > 0 ? '<span class="lc-badge">' + state.unreadCount + '</span>' : '') +
                '</button></div>';
        } else {
            html = '<div class="lc-widget ' + posClass + '"><div class="lc-window">';

            // Header
            html += '<div class="lc-header"><div class="lc-header-info">' +
                '<div class="lc-header-avatar">' + ICONS.headset + '</div><div>' +
                '<div class="lc-header-title">' + escapeHtml(state.config.headerText || 'Live Chat') +
                (state.wsConnected ? '<span class="lc-online-dot"></span>' : '') + '</div>';

            if (state.session) {
                if (state.session.status === 'ACTIVE' && state.session.agentName)
                    html += '<div class="lc-header-subtitle">' + escapeHtml(state.session.agentName) + '</div>';
                else if (state.session.status === 'WAITING')
                    html += '<div class="lc-header-subtitle">Aguardando atendente...</div>';
            }

            html += '</div></div><div class="lc-header-actions">';
            if (state.session && (state.session.status === 'WAITING' || state.session.status === 'ACTIVE'))
                html += '<button class="lc-header-btn" onclick="window.__lcEndChat()" title="Encerrar">' + ICONS.power + '</button>';
            html += '<button class="lc-header-btn" onclick="window.__lcMinimize()" title="Minimizar">' + ICONS.minimize + '</button>';
            html += '</div></div>';

            // Pre-chat form
            if (state.view === 'prechat') {
                html += '<div class="lc-prechat"><div class="lc-prechat-header">' +
                    '<h3>Bem-vindo!</h3>' +
                    '<p>' + escapeHtml(state.config.welcomeMessage || 'Preencha seus dados para iniciar.') + '</p></div>' +
                    '<div class="lc-form">' +
                    '<input type="text" id="lc-name-input" class="lc-input" placeholder="Seu nome *" />' +
                    '<input type="email" id="lc-email-input" class="lc-input" placeholder="Seu email' +
                    (state.config.requireEmail ? ' *' : ' (opcional)') + '" />' +
                    '<button class="lc-btn lc-btn-primary" onclick="window.__lcStartChat()"' +
                    (state.isLoading ? ' disabled' : '') + '>' +
                    (state.isLoading ? 'Iniciando...' : 'Iniciar Chat') +
                    '</button></div></div>';
            }

            // Chat view
            if (state.view === 'chat') {
                html += '<div class="lc-messages">';
                if (state.messages.length === 0 && state.session && state.session.status === 'WAITING')
                    html += '<div class="lc-waiting"><div class="lc-spinner"></div><p style="font-size:13px">Aguardando um atendente...</p></div>';

                for (var i = 0; i < state.messages.length; i++) {
                    var msg = state.messages[i];
                    var cls = msg.senderType === 'SYSTEM' ? 'lc-msg-system' : msg.senderType === 'VISITOR' ? 'lc-msg-visitor' : 'lc-msg-agent';
                    html += '<div class="lc-msg ' + cls + '"><div class="lc-msg-bubble">';
                    if (msg.senderType === 'AGENT' && msg.senderName)
                        html += '<div class="lc-msg-sender">' + escapeHtml(msg.senderName) + '</div>';
                    html += escapeHtml(msg.content);
                    if (msg.senderType !== 'SYSTEM')
                        html += '<div class="lc-msg-time">' + formatTime(msg.createdAt) + '</div>';
                    html += '</div></div>';
                }

                if (state.agentTyping)
                    html += '<div class="lc-typing"><span class="lc-typing-dot"></span><span class="lc-typing-dot"></span><span class="lc-typing-dot"></span></div>';
                html += '</div>';

                // Input area
                html += '<div class="lc-input-area">' +
                    '<textarea id="lc-msg-input" rows="1" placeholder="' +
                    (state.session && state.session.status === 'WAITING' ? 'Aguardando atendente...' : 'Digite sua mensagem...') +
                    '" onkeydown="window.__lcKeyDown(event)"' + (state.isSending ? ' disabled' : '') + '></textarea>' +
                    '<button class="lc-send-btn" onclick="window.__lcSend()"' + (state.isSending ? ' disabled' : '') + '>' +
                    ICONS.send + '</button></div>';
            }

            // Rating view
            if (state.view === 'rating') {
                html += '<div class="lc-rating">';
                if (state.ratingSubmitted) {
                    html += '<div style="font-size:48px;color:#22c55e">&#10003;</div><h3>Obrigado!</h3><p>Seu feedback foi enviado.</p>';
                } else {
                    html += '<div style="font-size:40px">&#9733;</div><h3>Como foi o atendimento?</h3><p>Sua avaliacao nos ajuda a melhorar.</p>' +
                        '<div class="lc-stars">';
                    for (var s = 1; s <= 5; s++)
                        html += '<button class="lc-star' + (s <= state.ratingValue ? ' active' : '') + '" onclick="window.__lcRate(' + s + ')">&#9733;</button>';
                    html += '</div><textarea id="lc-feedback-input" class="lc-input" rows="3" placeholder="Comentario (opcional)..." style="width:100%"></textarea>' +
                        '<div class="lc-rating-btns">' +
                        '<button class="lc-btn lc-btn-text" onclick="window.__lcSkipRating()">Pular</button>' +
                        '<button class="lc-btn lc-btn-primary" onclick="window.__lcSubmitRating()"' + (state.ratingValue === 0 ? ' disabled' : '') + '>Enviar</button></div>';
                }
                html += '</div>';
            }

            html += '</div></div>';
        }

        container.innerHTML = html;
        if (state.view === 'chat') scrollToBottom();
    }

    // -------------------------------------------------------
    // Global event handlers
    // -------------------------------------------------------
    window.__lcOpen = handleOpen;
    window.__lcMinimize = handleMinimize;
    window.__lcStartChat = handleStartChat;
    window.__lcSend = handleSendMessage;
    window.__lcEndChat = handleEndChat;
    window.__lcRate = function(val) { state.ratingValue = val; render(); };
    window.__lcSubmitRating = handleSubmitRating;
    window.__lcSkipRating = function() { cleanup(); state.view = 'ended'; render(); };
    window.__lcKeyDown = function(e) {
        if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); handleSendMessage(); }
    };

    // -------------------------------------------------------
    // Initialize
    // -------------------------------------------------------
    function init() {
        container = document.createElement('div');
        container.id = 'livechat-widget-root';
        document.body.appendChild(container);

        var style = document.createElement('style');
        style.id = 'livechat-widget-styles';
        document.head.appendChild(style);

        loadConfig().then(function(cfg) {
            state.config = cfg;
            style.textContent = getStyles();

            var storedToken = localStorage.getItem(SESSION_TOKEN_KEY);
            if (storedToken) {
                resumeSession(storedToken).then(function(session) {
                    if (session.status === 'WAITING' || session.status === 'ACTIVE') {
                        state.session = session;
                        getMessages(session.sessionToken).then(function(msgs) {
                            state.messages = msgs || []; render();
                        });
                        connectWebSocket(session.sessionToken);
                    } else {
                        localStorage.removeItem(SESSION_TOKEN_KEY);
                    }
                    render();
                }).catch(function() { localStorage.removeItem(SESSION_TOKEN_KEY); render(); });
            } else {
                render();
            }

            if (cfg.autoOpenDelaySeconds && cfg.autoOpenDelaySeconds > 0) {
                setTimeout(function() {
                    if (state.view === 'closed' && !state.session) handleOpen();
                }, cfg.autoOpenDelaySeconds * 1000);
            }
        }).catch(function() {
            state.config = { enabled: true, primaryColor: '#4F46E5', headerText: 'Chat', welcomeMessage: 'Ola! Como podemos ajudar?', offlineMessage: 'Estamos offline.', position: 'BOTTOM_RIGHT', requireEmail: false };
            style.textContent = getStyles();
            render();
        });
    }

    if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', init);
    else init();
})();
