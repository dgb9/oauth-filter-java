package com.ispnote.servlet;

import com.ispnote.oauth2.filter.Oauth2Session;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by dgb9 on 06/09/2016.
 */
public class AppServlet extends HttpServlet{
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        printData(resp.getWriter(), req);
    }

    private void printData(PrintWriter writer, HttpServletRequest req) {
        try {
            print(writer, "<h1>Currently accessing the application</h1>");

            Oauth2Session dt = Oauth2Session.getSessionObject(req);
            print(writer, "<p>");

            // print the items in the session object
            print(writer, "oauth session: " + dt);

            print(writer, "</p>");
        }
        catch (Exception e) {
            // no implementation needed
        }
    }

    public void print(PrintWriter writer, Object message) {
        String msg = "" + message;

        writer.write(msg);
    }
}
