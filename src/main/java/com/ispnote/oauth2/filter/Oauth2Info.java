package com.ispnote.oauth2.filter;

/**
 * Created by dgabrove on 06/10/2016.
 */
public interface Oauth2Info {
    String getClientId();
    String getSecret();
    String getCallbackUrl();
    String getAuthUrl();
    String getScope();

    boolean isAllowSignup();
}
