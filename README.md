# Simple Java Oauth2 Filter

###### General
This is a simple Oauth2 filter. Many identity providers like github, google etc allow programmers to use
their oauth capabilities to connect to their applications.

How does it work:
1. User accesses the application
2. If user is not logged in, browser redirects to github for login
3. Then github redirects back to the application
4. The Oauth filter then places a call to the github to get the authorization token
5. The authorization token is then verified against github and in exchange minimal user information gets retrieved, like the user name, the user id and the login.
6. A session object gets created
7. Application gets redirected to the protected content (the one you require authentication for)

###### Installation
As mentioned, this is a Java servlet filter. This should be installed on the top of your front controller only. What
does this mean: if you use struts, install this on the top of the struts servlet. If you use Spring MVC, the filter
shall be installed on the top of the dispatcher servlet.

Do not install this filter on the top of everything. You don't want the access of your company logo image
to redirect to the github, there is no point and it does not work.

For our example I created a servlet called AppServlet installed at the url pattern *.html. The servlet
is protected by having the filter installed on the top of it. The servlet will only be invoked by a
github authenticated user.

web.xml file defines both the filter and the servlet and shows the filter installed to filter the servlet traffic.

Then do as follows:
- Configure the application in github (your user / settings / oauth applications / developer / register new application
- choose the right callback url, and note the client id and the application secret
- Either provide an implementation for Oauth2Info or use the provided Oauth2InfoImpl
- Install your application and give it a try. If you are not logged in, you should be redirected to github to be logged in and then back to your application


###### The Oauth2Info parameters

1. clientId: the client id provided by github upon setting up the application
2. clientSecret: the client secret, as well provided by github
3. callbackUrl: url where github shall redirect upon successful login. It must start or be identical with the
callbackUrl provided as part of the application registration on the github side
4. authUrl: where your filter shall redirect your application when the user must authenticate. Some url on github,
the one already provided should be ok
5. tokenUrl: the url where your filter will get the token from github, second step of authentication
6. scope: usually "user" - what kind of access your application required regarding the github data. You just want
to authenticate with github and not access the repositories etc so you should not change it
7. allowSignup: if true, allows user to signup to github in case he / she does not have credentials already
8. verifyTokennUrl: where to got to verify the token on github after getting it. In exchange for the verification,
information like userId, login and full user name are provided

The above are the oauth2 required parameters. There is another situation though. The second step - when
call to retrieve the token is made - is a POST request. This request originates from your server. Sometimes
servers in some corporate environment are not allowed to place this kind of requests however proxy is provided.
Then this filter has the possibility to use proxy for the server originating http requests

9. proxy: true if the server originated http requests shall be performed via proxy
10. proxyAddress: the machine name or url of the proxy
11. proxyPort: the proxy port

If proxy is false, the information at 10 and 11 is ignored.

