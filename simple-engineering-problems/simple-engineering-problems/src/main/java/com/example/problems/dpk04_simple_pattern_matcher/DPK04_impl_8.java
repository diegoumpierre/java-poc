package com.example.problems.dpk04_simple_pattern_matcher;

public class DPK04_impl_8 {

    enum LanguageEnum{
        Usa("English"),
        Brazil("Portuguese"),
        Spain("Spanish"),
        Italy("Italian"),
        France("French"),
        Germany("German")
        ;
        private final String language;

        LanguageEnum(String language) {
            this.language = language;
        }
    }

    public static String patternMatcherEnum(String usa) {
        LanguageEnum languageEnum = LanguageEnum.valueOf(usa);
        return languageEnum.language;
    }

    public static String patternMatcher(String usa) {
        String language = null;
        switch (usa){
            case "Usa":language="English";break;
            case "Brazil":language="Portuguese";break;
            case "Spain":language="Spanish";break;
            case "Italy":language="Italian";break;
            case "France":language="French";break;
            case "Germany":language="German";break;
        }
        return language;
    }
}
