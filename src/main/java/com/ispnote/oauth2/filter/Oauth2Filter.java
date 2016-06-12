package com.ispnote.oauth2.filter;

import com.google.gson.Gson;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
                if(isCallbackUrl(request)){
                    processCallback(request, response, chain);
                }
                else {
                    redirectToIdentityProvider(request, response);
                }
            }
        }
        catch(Exception e){
            printError(response, "error while processing filter: " + e.getClass().getName() + ", " + e.getMessage());
        }
    }

    private void printError(HttpServletResponse response, String s) {
        try {
            PrintWriter writer = response.getWriter();

            String pageText = loadTemplatePage();
            pageText = pageText.replaceAll("@@placeholder@@", s);

            writer.write(pageText);
            writer.flush();
            writer.close();
        }
        catch (Exception e) {
            // no implementation
        }
    }

    private String loadTemplatePage() throws IOException {
        Class<? extends Oauth2Filter> cls = this.getClass();
        InputStream resourceStream = cls.getResourceAsStream("pagetemplate.html");

        return getStreamContent(resourceStream, true);
    }

    private void processCallback(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        // get the parameters
        String state = request.getParameter(Oauth2Constants.PARAM_STATE);
        state = state == null ? "" : state.trim();

        String code = request.getParameter(Oauth2Constants.PARAM_CODE);

        // check the base is equal with the stored information
        try {
            Oauth2Session session = Oauth2Session.getSessionObject(request);
            if (state.equals(session.getState())) {
                // call the POST and retrieve the token
                HttpPost post = new HttpPost(data.getTokenUrl());

                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair(Oauth2Constants.PARAM_CLIENT_ID, data.getClientId()));
                list.add(new BasicNameValuePair(Oauth2Constants.PARAM_CLIENT_SECRET, data.getClientSecret()));
                list.add(new BasicNameValuePair(Oauth2Constants.PARAM_STATE, state));
                list.add(new BasicNameValuePair(Oauth2Constants.PARAM_CODE, code));

                post.setEntity(new UrlEncodedFormEntity(list));

                HttpClient client = HttpClients.createDefault();
                CloseableHttpResponse resp = (CloseableHttpResponse) client.execute(post);

                InputStream input = resp.getEntity().getContent();
                String json = getStreamContent(input, true);

                // the json is supposed to contain the token the user id and the user email id
                String token = getAuthorizationToken(json);

                // check the token and get the login, id  and email
                Oauth2Identification ident = checkToken(token);

                // if everthing is ok, fill out the session object with the identification info and proceed
                // with the filtering
                session.setId(ident.getUserId());
                session.setEmail(ident.getEmail());
                session.setLogin(ident.getLogin());

                // and now do the filter that will forward to the authenticated page
                doFilter(request, response, chain);
            }
            else {
                printError(response, "error validating the random state");
            }
        }
        catch (Exception e) {
            printError(response, "error processing the callback post: " + e.getMessage());
        }

        // store the token in the session
        // proceed with the chain filter processing
    }

    private Oauth2Identification checkToken(String token) {
        // TODO
        return null;
    }

    private String getAuthorizationToken(String json) {
        // TODO
        return null;
    }

    private String getStreamContent(InputStream input, boolean close) throws IOException {
        byte[] bytes = new byte[1024];

        StringBuilder buffer = new StringBuilder();
        int count = 0;

        do {
            count = input.read(bytes);
            if (count > 0) {
                String strRead = new String(bytes, 0, count);

                buffer.append(strRead);
            }
        }
        while (count >= 0);

        if (close) {
            input.close();
        }

        return buffer.toString();
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

    private boolean isCallbackUrl(HttpServletRequest request) {
        String url = request.getRequestURL().toString();

        return data.getCallbackUrl().equals(url);
    }

    public void destroy() {
        // no implementation
    }
}
