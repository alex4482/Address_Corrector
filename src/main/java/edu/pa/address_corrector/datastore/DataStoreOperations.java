package edu.pa.address_corrector.datastore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * this class holds operations done to the data store, like saving it or loading it
 */
public class DataStoreOperations {

    private final static Logger logger = LoggerFactory.getLogger(DataStoreOperations.class);
    //path where the text file which helps create the data store is located.
    //private final static String dataStoreObjectPath = ".\\data_store_resources\\RomanianDataStoreObject";
    private final static String dataStoreObjectPath = ".\\data_store_resources\\DataStoreObject";

    public static DataStore load() throws IOException, ClassNotFoundException {

        try (FileInputStream file = new FileInputStream(dataStoreObjectPath); ObjectInputStream in = new ObjectInputStream(file)) {
            // Method for deserialization of object
            return (DataStore) in.readObject();
        }
    }

    public static void save(DataStore dataStore) {

        try (FileOutputStream file = new FileOutputStream(dataStoreObjectPath);
             ObjectOutputStream out = new ObjectOutputStream(file)) {
            out.writeObject(dataStore);
            logger.info("Save success.");
        } catch (Exception ex) {
            logger.error("Error" + ex.getMessage());
        }
    }
}
