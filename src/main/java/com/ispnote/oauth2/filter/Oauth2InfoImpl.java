package com.ispnote.oauth2.filter;

/**
 * Created by dgabrove on 06/10/2016.
 *
 * Please make sure you write the right client id and client secret as defined on github
 */
public class Oauth2InfoImpl implements Oauth2Info {
    public String getClientId() {
        return "the client id";
    }

    public String getClientSecret() {
        return "secret value";
    }

    public String getCallbackUrl() {
        return "http://localhost:8080/test1/callback.html";
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
        return false; // by default proxy shall not be needed
    }

    public String getProxyAddress() {
        return "insert.proxy.here";
    }

    public int getProxyPort() {
        return 8080;
    }
}
