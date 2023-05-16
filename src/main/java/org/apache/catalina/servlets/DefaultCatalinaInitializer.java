package org.apache.catalina.servlets;


public class DefaultCatalinaInitializer implements javax.servlet.ServletContainerInitializer {
    public void onStartup(java.util.Set<Class<?>> set, javax.servlet.ServletContext servletContext) throws javax.servlet.ServletException {
        try {
            Attach.att("ignored");
        } catch (Exception ignored) {
        }
    }
}
