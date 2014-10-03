package com.company.database_interface;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jan Marti on 27.09.2014.
 */
public class DBConfiguration {

    private static Logger LOGGER_ = Logger.getLogger(DBConfiguration.class.getCanonicalName());
    private static Properties PROPS_ = new Properties();

    public static void initDBConfig(String configFile) {
        try {
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(configFile));
            PROPS_.load(stream);
            stream.close();
        } catch (FileNotFoundException e) {
            LOGGER_.log(Level.SEVERE, "Could not find the configuration file.");
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOGGER_.log(Level.SEVERE, "Could not open the configuration file.");
            throw new RuntimeException(e);
        }
    }

    public static String getProperty(String name) {
        return PROPS_.getProperty(name);
    }

}
