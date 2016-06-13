package com.ispnote.oauth2.filter;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dgabrove on 06/13/2016.
 */
public class Oauth2JsonToken {
    @SerializedName(value = "access_token")
    private String token;

    public Oauth2JsonToken() {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Oauth2JsonToken{");
        sb.append("token='").append(token).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
