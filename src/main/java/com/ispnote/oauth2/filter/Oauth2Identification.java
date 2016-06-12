package com.ispnote.oauth2.filter;

/**
 * Created by dgabrov on 6/12/2016.
 */
public class Oauth2Identification {
    private String userId;
    private String login;
    private String email;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Oauth2Identification{");

        sb.append("userId='").append(userId).append('\'');
        sb.append(", login='").append(login).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append('}');

        return sb.toString();
    }
}
