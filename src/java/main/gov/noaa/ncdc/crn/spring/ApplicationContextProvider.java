package gov.noaa.ncdc.crn.spring;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class ApplicationContextProvider {
    private static final String STANDARD_CONTEXT = "classpath:application-context.xml";

    public enum Contexts {
        UNIT("unittest"), PROD("prod"), PROD_RO("prod-ro"), PROD_RO_MEMCACHE("prod-ro-memcache"),
        // TEST("test-rw"),
        // TEST_RO("test-ro"),
        // DEV_RW("dev-rw"),
        // DEV_RO("dev-ro")
        ;
        private GenericXmlApplicationContext context;
        private final String[] locations;
        private final String profile;

        private Contexts(String profile, String... locations) {
            this.locations = locations;
            this.profile = profile;
        }

        private Contexts(String profile) {
            this(profile, STANDARD_CONTEXT);
        }

        public ConfigurableApplicationContext getApplicationContext() {
            /* lazy loading because this is an expensive task */
            if (context == null) {
                context = createApplicationContext(profile, locations);
            }
            return context;
        }
    }

    public static GenericXmlApplicationContext createApplicationContext(String profile, String... locations) {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        if (profile != null) {
            context.getEnvironment().setActiveProfiles(profile);
        }
        context.load(locations);
        context.refresh();
        return context;
    }

}
