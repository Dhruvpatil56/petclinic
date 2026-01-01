package org.springframework.samples.petclinic;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;

import javax.servlet.Filter;

/**
 * Servlet 3.0+ initializer (replaces web.xml)
 *
 * Profile resolution order:
 * 1. JVM arg   : -Dspring.profiles.active
 * 2. ENV var   : SPRING_PROFILES_ACTIVE (Docker / K8s)
 * 3. Default   : jdbc
 */
public class PetclinicInitializer extends AbstractDispatcherServletInitializer {

    private static final String DEFAULT_PROFILE = "jdbc";

    @Override
    protected WebApplicationContext createRootApplicationContext() {
        XmlWebApplicationContext rootAppContext = new XmlWebApplicationContext();
        rootAppContext.setConfigLocations(
                "classpath:spring/business-config.xml",
                "classpath:spring/tools-config.xml"
        );

        String profile = System.getProperty("spring.profiles.active");

        if (profile == null || profile.isEmpty()) {
            profile = System.getenv("SPRING_PROFILES_ACTIVE");
        }

        if (profile == null || profile.isEmpty()) {
            profile = DEFAULT_PROFILE;
        }

        rootAppContext.getEnvironment().setActiveProfiles(profile);

        return rootAppContext;
    }

    @Override
    protected WebApplicationContext createServletApplicationContext() {
        XmlWebApplicationContext webAppContext = new XmlWebApplicationContext();
        webAppContext.setConfigLocation("classpath:spring/mvc-core-config.xml");
        return webAppContext;
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[]{
                new CharacterEncodingFilter("UTF-8", true)
        };
    }
}

