package com.ispnote.oauth2.filter;

import org.apache.http.client.utils.URIBuilder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by dgb9 on 06/09/2016.
 */
public class Oauth2Filter implements Filter {
    private Oauth2Info data;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.data = Oauth2InfoFactory.getInstance().getInfo();
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        processFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void processFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        try {
            Oauth2Session session = Oauth2Session.getSessionObject(request);

            if (session.isLoggedIn()) {
                chain.doFilter(request, response);
            }
            else {
                if(isCallbackUrl()){
                    processCallback();
                }
                else {
                    redirectToIdentityProvider(request, response);
                }
            }
        }
        catch(Exception e){
            // todo just log the difference
        }
    }

    private void processCallback() {
        // TODO
    }

    private void redirectToIdentityProvider(HttpServletRequest request, HttpServletResponse response) throws URISyntaxException, IOException {

        URIBuilder builder = new URIBuilder(data.getAuthUrl());

        Oauth2Session sessionObject = Oauth2Session.getSessionObject(request);
        String state = sessionObject.getState();
        String allowSignup = Boolean.toString(data.isAllowSignup());

        builder.addParameter(Oauth2Constants.PARAM_CLIENT_ID, data.getClientId());
        builder.addParameter(Oauth2Constants.REDIRECT_URL, data.getCallbackUrl());
        builder.addParameter(Oauth2Constants.PARAM_SCOPE, data.getScope());
        builder.addParameter(Oauth2Constants.PARAM_STATE, state);

        builder.addParameter(Oauth2Constants.PARAM_ALLOW_SIGNUP, allowSignup);

        // get the url and proceed
        String url = builder.toString();
        response.sendRedirect(url); // send the redirect location header
    }

    private boolean isCallbackUrl() {
        // TODO
        return false;
    }

    public void destroy() {

    }
}
