package org.geektimes.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * <p>
 *
 * <p>
 *
 * @author liuwei
 * @since 2021/3/16
 */
public class StringConvert implements Converter<String> {

    @Override
    public String convert(String source) throws IllegalArgumentException, NullPointerException {
        if (source == null) {
            return null;
        }
        String value = source.trim();
        if (value.isEmpty()) {
            return null;
        }
        return value;
    }

}