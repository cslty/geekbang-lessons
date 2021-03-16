package org.geektimes.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

/**
 * <p>
 * 系统环境变量配置源
 * <p>
 *
 * @author liuwei
 * @since 2021/3/15
 */
public class ApplicationPropertiesConfigSource implements ConfigSource {

    private static Logger logger = Logger.getLogger(ApplicationPropertiesConfigSource.class.getName());

    private final Map<String, String> properties = new HashMap<>(32);

    private final static String APPLICATION_PROPERTIES_LOCATION = "META-INF/application.properties";

    public ApplicationPropertiesConfigSource() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL propertiesFileURL = classLoader.getResource(APPLICATION_PROPERTIES_LOCATION);
        if (propertiesFileURL != null) {
            Properties properties = new Properties();
            try (InputStream in = propertiesFileURL.openStream()){
                properties.load(in);
                properties.forEach((k, v) -> {
                    this.properties.put(k.toString(), v.toString());
                });
            } catch (IOException ignored) {

            }
        }
    }

    @Override
    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

    @Override
    public String getValue(String s) {
        return properties.get(s);
    }

    @Override
    public String getName() {
        return "Application Properties";
    }
}