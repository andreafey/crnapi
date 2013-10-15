/**
 * 
 */
package gov.noaa.ncdc.crn.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Allows easy access to properties from the properties file.
 * @author diana.kantor
 */
public class PropertyUtils extends PropertyPlaceholderConfigurer {
    private static Map<String, String> propertiesMap;

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props)
            throws BeansException {
        super.processProperties(beanFactory, props);

        propertiesMap = new HashMap<String, String>();
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            propertiesMap.put(keyStr, props.getProperty(keyStr));
        }
    }

    /**
     * Gets a property value by name.
     * @param name The name of the property.
     * @return The value of the property.
     */
    public static String getProperty(String name) {
        return propertiesMap == null ? null : propertiesMap.get(name);
    }

    /**
     * Determines if a property is set to true.
     * @param name The name of the property.
     * @return True if the property is set to "true" (case-insensitive). Otherwise, false.
     */
    public static boolean isPropertyTrue(String name) {
        return Boolean.valueOf(getProperty(name));
    }
}