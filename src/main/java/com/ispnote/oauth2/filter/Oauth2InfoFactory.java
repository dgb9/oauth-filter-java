package com.ispnote.oauth2.filter;

/**
 * Created by dgabrove on 06/10/2016.
 */
public class Oauth2InfoFactory {
    private Oauth2InfoFactory() {

    }

    private static Oauth2InfoFactory instance;

    public static Oauth2InfoFactory getInstance() {
        if (instance == null) {
            instance = new Oauth2InfoFactory();
        }

        return instance;
    }

    public Oauth2Info getInfo() {
        return new Oauth2InfoImpl();
    }

}
