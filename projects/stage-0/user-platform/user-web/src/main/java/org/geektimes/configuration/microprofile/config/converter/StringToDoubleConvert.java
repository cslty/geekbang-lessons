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
public class StringToDoubleConvert implements Converter<Double> {

    @Override
    public Double convert(String source) throws IllegalArgumentException, NullPointerException {
        if (source == null) {
            return null;
        }
        String value = source.trim();
        if (value.isEmpty()) {
            return null;
        }
        try {
            return Double.valueOf(value);
        } catch (Exception e) {
            // NumberFormatException -> IllegalArgumentException
            throw new IllegalArgumentException("Invalid double value '" + source + "'");
        }
    }

}