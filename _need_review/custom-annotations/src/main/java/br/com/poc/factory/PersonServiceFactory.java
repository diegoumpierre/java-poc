package br.com.poc.factory;

import br.com.poc.proxy.PersonServiceProxy;


public class PersonServiceFactory<T> {

    private final Class<T> type;

    public PersonServiceFactory(Class<T> type) {
        this.type = type;
    }

    public static <T> PersonServiceProxy<T> newInstance() {
        return new PersonServiceProxy<T>();
    }


}
