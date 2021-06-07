package edu.pa.address_corrector.datastore;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import edu.pa.address_corrector.datastore.creator.DataStoreCreator;
import edu.pa.address_corrector.node.Node;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * this class represents the data store.
 * the only field it has is a multimap, which stores String keys and Node values.
 * a Node represents a location, only that location, and is the only node which represents that location.
 * a String is a name of one of all the locations.
 */
@ToString
public class DataStore implements Serializable {

    transient private final static Logger logger = LoggerFactory.getLogger(DataStore.class.getName());
    public static DataStore dataStore = null;
    //private final String dataStorePath = ".\\data_store_resources\\RomaniaDataStore.txt";
    transient private final String dataStorePath = ".\\allCountries\\DataStore.txt";
    @Getter
    private final Multimap<String, Node> mapping = ArrayListMultimap.create();

    //getter for the data store.
    //if the data store hasn't been created yet, attempt to load it.
    //if load fails, create it from text file and save the object
    public static DataStore getDataStore() {
        logger.trace("Getting Datastore...");
        if (dataStore == null) {
            dataStore = new DataStore();
            String load = dataStore.loadDataStore();
            if (!load.equals("Datastore loaded.")) {
                logger.info("Attempting to create datastore.");
                dataStore = DataStoreCreator.getCreator().createDataStore(dataStore.dataStorePath);
                dataStore.saveDataStore();
            }
        }
        return dataStore;
    }

    public void saveDataStore() {
        logger.info("Datastore created. Attempting to save datastore.");
        DataStoreOperations.save(dataStore);
    }

    public String loadDataStore() {
        try {
            logger.info("Attempting to load datastore.");
            //DataStoreOperations.load(dataStore);
            dataStore = DataStoreOperations.load();
            logger.info("Datastore loaded.");
            return "Datastore loaded.";
        } catch (Exception e) {
            logger.error("Error. Failed to load datastore " + e.getMessage());
            return "Error";
        }
    }


}


//                        int numberOfWords = wordsInName.size();
//                        String aux;
//                        for (int it = 2; it <= numberOfWords; ++it) {
//                            for (int j = 0; j <= numberOfWords - it; ++j) {
//                                aux = wordsInName.get(j);
//                                for (int k = j + 1; k < j + it; ++k) {
//                                    aux = aux.concat(" " + wordsInName.get(k));
//                                }
//                                wordsInName.add(aux);
//                            }
//                        }