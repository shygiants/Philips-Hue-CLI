package io.github.shygiants.philips_hue.util;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * @auther Sanghoon Yoon (iDBLab, shygiants@gmail.com)
 * @date 2016. 11. 25.
 * @see
 */
public final class Resources {

    private static final Resources singleton = new Resources();

    public static String strings(String key) {
        String str = singleton.strings.getProperty(key);
        if (str == null) throw new NoSuchElementException();
        return str;
    }

    private final Properties strings;

    private Resources() {
        strings = new Properties();
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            strings.load(classLoader.getResourceAsStream("strings.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
