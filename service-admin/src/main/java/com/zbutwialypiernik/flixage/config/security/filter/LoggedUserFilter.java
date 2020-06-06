package com.zbutwialypiernik.flixage.config.security.filter;

import com.zbutwialypiernik.flixage.config.Routes;
import com.zbutwialypiernik.flixage.util.SecurityUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This filter prevents user from going to login page when already logged in,
 * if user is then sends him back to the home page.
 *
 * @see SecurityUtils#isUserLoggedIn()
 */
@Component
public class LoggedUserFilter implements Filter  {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (SecurityUtils.isUserLoggedIn() && httpRequest.getRequestURI().equals(httpRequest.getContextPath() + "/" + Routes.LOGIN)) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/" + Routes.HOME);
        } else {
            chain.doFilter(httpRequest, response);
        }
    }

    @Override
    public void destroy() {

    }

}
