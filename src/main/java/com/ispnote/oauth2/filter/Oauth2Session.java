package com.ispnote.oauth2.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

/**
 * Created by dgb9 on 06/09/2016.
 */
public class Oauth2Session {
    private String login;
    private String id;
    private String name;

    private Set<String> access;
    private String token;
    private String state;

    private Oauth2Session() {
        access = new TreeSet<String>();

        // create a new random uuid state string for the oauth protocol
        // that will remain in the session object for verification
        state = UUID.randomUUID().toString();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<String> getAccess() {
        return access;
    }

    public void setAccess(Collection<String> access) {
        this.access.clear();

        if (access != null) {
            this.access.addAll(access);
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Oauth2Session{");
        sb.append("login='").append(login).append('\'');
        sb.append(", id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", access=").append(access);
        sb.append(", token='").append(token).append('\'');
        sb.append(", state='").append(state).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Oauth2Session getSessionObject(HttpServletRequest req) {
        Oauth2Session res = null;

        HttpSession session = req.getSession();
        Object dt = session.getAttribute(Oauth2Constants.OAUTH2_USER_SESSION);

        if (dt != null) {
            res = (Oauth2Session) dt;
        }
        else {
            res = new Oauth2Session();

            session.setAttribute(Oauth2Constants.OAUTH2_USER_SESSION, res);
        }

        return res;
    }

    public boolean isLoggedIn() {
        String lg = getLogin();

        return lg != null && lg.length() > 0;
    }
}
