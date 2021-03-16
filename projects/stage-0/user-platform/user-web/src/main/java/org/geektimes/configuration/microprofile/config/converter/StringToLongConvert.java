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
public class StringToLongConvert implements Converter<Long> {

    @Override
    public Long convert(String source) throws IllegalArgumentException, NullPointerException {
        if (source == null) {
            return null;
        }
        String value = source.trim();
        if (value.isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (Exception e) {
            // NumberFormatException -> IllegalArgumentException
            throw new IllegalArgumentException("Invalid long value '" + source + "'");
        }
    }

}