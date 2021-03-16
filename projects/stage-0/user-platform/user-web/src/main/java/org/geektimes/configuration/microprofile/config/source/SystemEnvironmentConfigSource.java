package org.geektimes.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 系统环境变量配置源
 * <p>
 *
 * @author liuwei
 * @since 2021/3/15
 */
public class SystemEnvironmentConfigSource implements ConfigSource {

    private final Map<String, String> env;

    public SystemEnvironmentConfigSource() {
        env = System.getenv();
    }

    @Override
    public Set<String> getPropertyNames() {
        return env.keySet();
    }

    @Override
    public String getValue(String s) {
        return env.get(s);
    }

    @Override
    public String getName() {
        return "System Environment Variables";
    }

}