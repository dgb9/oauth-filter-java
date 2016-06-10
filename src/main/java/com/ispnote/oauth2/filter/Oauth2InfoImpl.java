package com.ispnote.oauth2.filter;

/**
 * Created by dgabrove on 06/10/2016.
 */
public class Oauth2InfoImpl implements Oauth2Info {
    public String getClientId() {
        return "de196ddc3e857f27bd02";
    }

    public String getSecret() {
        return "f7b9fabfac08673e80f978bdbbb806147ef518e3";
    }

    public String getCallbackUrl() {
        return "http://localhost:8080/oauth/callback.html";
    }

    public String getAuthUrl() {
        return "https://github.com/login/oauth/authorize";
    }
}
