package com.example.problems.dpk04_simple_pattern_matcher;

public class DPK04_impl_7 {
    public static String patternMatcher(String country) {
        String lang = null;
        switch (country){
            case "Usa":lang="English";break;
            case "Brazil":lang="Portuguese";break;
            case "Spain":lang="Spanish";break;
            case "Italy":lang="Italian";break;
            case "France":lang="French";break;
            case "Germany":lang="German";break;
        }
        return lang;
    }


    public static String patternMatcherEnum(String coutry) {
        CountryLanguageEnum countryLanguageEnum = CountryLanguageEnum.valueOf(coutry);
        return countryLanguageEnum.lang;
    }

    enum CountryLanguageEnum{
        Usa("English"),
        Brazil("Portuguese"),
        Spain("Spanish"),
        Italy("Italian"),
        France("French"),
        Germany("German")
        ;
        private final String lang;
        CountryLanguageEnum(String lang){
            this.lang = lang;
        }
    }
}
