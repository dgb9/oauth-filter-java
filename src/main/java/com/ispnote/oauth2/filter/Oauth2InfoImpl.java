package com.ispnote.oauth2.filter;

/**
 * Created by dgabrove on 06/10/2016.
 */
public class Oauth2InfoImpl implements Oauth2Info {
    public String getClientId() {
        return "de196ddc3e857f27bd02";
    }

    public String getClientSecret() {
        return "f7b9fabfac08673e80f978bdbbb806147ef518e3";
    }

    public String getCallbackUrl() {
        return "http://localhost:8080/oauth/callback.html";
    }

    public String getAuthUrl() {
        return "https://github.com/login/oauth/authorize";
    }

    public String getTokenUrl(){
        return "https://github.com/login/oauth/access_token";
    }

    public String getScope() {
        return "user"; // user means will get the user email address and the user id
    }

    public boolean isAllowSignup() {
        return true; // let's allow people to signup to github
    }

    public String getVerifyTokenUrl() {
        return "https://api.github.com/user";
    }

    public boolean isProxy() {
        return true;
    }

    public String getProxyAddress() {
        return "webproxystatic-on.tsl.telus.com";
    }

    public int getProxyPort() {
        return 8080;
    }
}
