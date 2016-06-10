package com.ispnote.oauth2.filter;

/**
 * Created by dgabrove on 06/10/2016.
 */
public interface Oauth2Info {
    public String getClientId();
    public String getSecret();
    public String getCallbackUrl();
    public String getAuthUrl();
}
