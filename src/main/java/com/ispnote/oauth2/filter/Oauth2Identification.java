package com.ispnote.oauth2.filter;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dgabrov on 6/12/2016.
 */
public class Oauth2Identification {

    @SerializedName(value="id")
    private String userId;

    @SerializedName(value="login")
    private String login;

    @SerializedName(value="name")
    private String name;

    public Oauth2Identification() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Oauth2Identification{");

        sb.append("userId='").append(userId).append('\'');
        sb.append(", login='").append(login).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append('}');

        return sb.toString();
    }
}
