package org.ashe.security.infra;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class EnvironmentUtils {

    private static final String DEV = "dev";
    private static final String TEST = "test";
    private static final String PROD = "prod";
    private final Environment environment;

    public EnvironmentUtils(Environment environment) {
        this.environment = environment;
    }

    public String getCurrentProfile() {
        String[] activeProfiles = environment.getActiveProfiles();

        if (activeProfiles.length > 0) {
            return activeProfiles[0];
        }

        return null;
    }

    public boolean isDev() {
        String currentProfile = getCurrentProfile();
        return DEV.equals(currentProfile);
    }

    public boolean isTest() {
        String currentProfile = getCurrentProfile();
        return TEST.equals(currentProfile);
    }

    public boolean isProd() {
        String currentProfile = getCurrentProfile();
        return PROD.equals(currentProfile);
    }

}
