package com.ispnote.oauth2.filter;

/**
 * Created by dgabrove on 06/10/2016.
 */
public interface Oauth2Info {
    String getClientId();
    String getClientSecret();
    String getCallbackUrl();
    String getAuthUrl();

    String getTokenUrl();

    String getScope();

    boolean isAllowSignup();

    String getVerifyTokenUrl();

    boolean isProxy();
    String getProxyAddress();
    int getProxyPort();
}
