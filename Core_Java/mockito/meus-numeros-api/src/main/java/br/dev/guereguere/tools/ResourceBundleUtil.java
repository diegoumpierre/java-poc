package br.dev.guereguere.tools;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Component
@Slf4j
public class ResourceBundleUtil {


    public static String getMessageValue(String resourceBundleKey) {
        try{

            ResourceBundle bundle = null;

            Locale locale = LocaleContextHolder.getLocale();
            locale.getLanguage();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            bundle = ResourceBundle.getBundle("messages", locale, loader);

            return bundle.getString(resourceBundleKey);

        }catch (MissingResourceException error){

            log.error(resourceBundleKey +" - VAR_MESSAGE_BUNDLE (\"messages\") NOT DEFINED!");

            return null;
        }
    }



}