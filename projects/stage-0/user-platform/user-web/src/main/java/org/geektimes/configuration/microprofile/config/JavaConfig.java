package org.geektimes.configuration.microprofile.config;


import org.apache.commons.lang.ClassUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

public class JavaConfig implements Config {

    /**
     * 内部可变的集合，不要直接暴露在外面
     */
    private List<ConfigSource> configSources = new LinkedList<>();

    private final Map<Type, Converter<?>> converters;

    private static Comparator<ConfigSource> configSourceComparator = new Comparator<ConfigSource>() {
        @Override
        public int compare(ConfigSource o1, ConfigSource o2) {
            return Integer.compare(o2.getOrdinal(), o1.getOrdinal());
        }
    };

    public JavaConfig() {
        ClassLoader classLoader = getClass().getClassLoader();
        ServiceLoader<ConfigSource> serviceLoader = ServiceLoader.load(ConfigSource.class, classLoader);
        serviceLoader.forEach(configSources::add);
        // 排序
        configSources.sort(configSourceComparator);
        this.converters = buildConverters(classLoader);
    }

    @Override
    public <T> T getValue(String propertyName, Class<T> propertyType) {
        String propertyValue = getPropertyValue(propertyName);
        // String 转换成目标类型
        Converter<T> converter = getConverterOrNull(propertyType);
        if (converter == null) {
            // 没有转换器支持该类型
            throw new IllegalArgumentException("No Converter supports for " + propertyType.getName());
        }
        return converter.convert(propertyValue);
    }

    @Override
    public ConfigValue getConfigValue(String propertyName) {
        return null;
    }

    protected String getPropertyValue(String propertyName) {
        String propertyValue = null;
        for (ConfigSource configSource : configSources) {
            propertyValue = configSource.getValue(propertyName);
            if (propertyValue != null) {
                break;
            }
        }
        return propertyValue;
    }

    @Override
    public <T> Optional<T> getOptionalValue(String propertyName, Class<T> propertyType) {
        T value = getValue(propertyName, propertyType);
        return Optional.ofNullable(value);
    }

    @Override
    public Iterable<String> getPropertyNames() {
        return null;
    }

    @Override
    public Iterable<ConfigSource> getConfigSources() {
        return Collections.unmodifiableList(configSources);
    }

    @Override
    public <T> Optional<Converter<T>> getConverter(Class<T> forType) {
        return Optional.ofNullable(getConverterOrNull(forType));
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        if (Config.class.isAssignableFrom(type)) {
            return type.cast(this);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    <T> Converter<T> getConverterOrNull(Class<T> asType) {
        final Converter<?> exactConverter = converters.get(asType);
        if (exactConverter != null) {
            return (Converter<T>) exactConverter;
        }
        // 原生类型转包装类型，再获取
        if (asType.isPrimitive()) {
            return (Converter<T>) getConverterOrNull(ClassUtils.primitiveToWrapper(asType));
        }
        // 数组不支持
        if (asType.isArray()) {
            return null;
        }
        return null;
    }

    /**
     * 构建Converter
     */
    private Map<Type, Converter<?>> buildConverters(ClassLoader classLoader) {
        ServiceLoader<Converter> serviceLoader = ServiceLoader.load(Converter.class, classLoader);
        Map<Type, Converter<?>> converters = new HashMap<>();
        for (Converter<?> converter : serviceLoader) {
            Class<? extends Converter> converterClass = converter.getClass();
            // 没有泛型的情况
            Type type = getConverterType(converterClass);
            if (type == null) {
                throw new IllegalStateException(String.format("Can not add converter %s that is not parameterized with a type", converterClass.getName()));
            }
            converters.put(type, converter);
        }
        return converters;
    }

    /**
     * 获取Converter的转换类型
     */
    private Type getConverterType(Class<?> clazz) {
        if (clazz.equals(Object.class)) {
            return null;
        }

        for (Type type : clazz.getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) type;
                if (pt.getRawType().equals(Converter.class)) {
                    Type[] typeArguments = pt.getActualTypeArguments();
                    // 泛型数量不为1
                    if (typeArguments.length != 1) {
                       throw new IllegalStateException(String.format("Converter %s must be parameterized with a single type", clazz.getName()));
                    }
                    return typeArguments[0];
                }
            }
        }

        return getConverterType(clazz.getSuperclass());
    }

}
