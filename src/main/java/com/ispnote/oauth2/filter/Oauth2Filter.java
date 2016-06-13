package com.ispnote.oauth2.filter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
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

                HttpClient client = getHttpClient();

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
                session.setToken(token);

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

    }

    private HttpClient getHttpClient() {
        HttpClient client = null;

        if(data.isProxy()){
            HttpClientBuilder bld = HttpClients.custom();

            HttpHost proxy = new HttpHost(data.getProxyAddress(), data.getProxyPort());
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);

            client = bld.setRoutePlanner(routePlanner).build();
        }
        else {
            client = HttpClients.createDefault();
        }
        return client;
    }

    private Oauth2Identification checkToken(String token) throws URISyntaxException, IOException {
        URIBuilder builder = new URIBuilder(data.getVerifyTokenUrl());
        builder.addParameter("access_token", token);
        String strUrl = builder.toString();

        HttpGet get = new HttpGet(strUrl);

        HttpClient client = getHttpClient();

        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        InputStream stream = entity.getContent();
        String content = getStreamContent(stream, true);

        // content is being parsed as a
        Gson gson = new Gson();
        Oauth2Identification ident = gson.fromJson(content, Oauth2Identification.class);

        return ident;
    }

    private String getAuthorizationToken(String json) {
        Gson gson = new Gson();
        Oauth2JsonToken tokenHolder = gson.fromJson(json, Oauth2JsonToken.class);

        return tokenHolder.getToken();
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
