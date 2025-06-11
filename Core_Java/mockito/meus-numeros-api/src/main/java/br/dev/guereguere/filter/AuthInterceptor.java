package br.dev.guereguere.filter;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class AuthInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorization", "<your token>");

    }

}
