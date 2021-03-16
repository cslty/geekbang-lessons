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
public class StringToIntegerConvert implements Converter<Integer> {

    @Override
    public Integer convert(String source) throws IllegalArgumentException, NullPointerException {
        if (source == null) {
            return null;
        }
        String value = source.trim();
        if (value.isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            // NumberFormatException -> IllegalArgumentException
            throw new IllegalArgumentException("Invalid int value '" + source + "'");
        }
    }

}