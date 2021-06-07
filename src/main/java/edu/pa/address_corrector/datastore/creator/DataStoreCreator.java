package edu.pa.address_corrector.datastore.creator;

import edu.pa.address_corrector.datastore.DataStore;
import edu.pa.address_corrector.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * the purpose of this class is to create a data store from a txt file that has to be parsed.
 * <p>
 * the format of the file is:
 * <lineStart>countryName,countryName2,countryName3...
 * <lineStart><tab>stateName,stateName2,etc<tab>cityName,cityName2,etc<newline>
 * ...
 * ...
 * <lineStart>otherCountryName,otherCountry2,otherCountry3...
 * etc
 */
public class DataStoreCreator {

    //static instance of Creator class
    private static final Logger logger = LoggerFactory.getLogger(DataStoreCreator.class);
    private static DataStoreCreator INSTANCE;

    public static DataStoreCreator getCreator() {
        if (INSTANCE == null)
            INSTANCE = new DataStoreCreator();
        return INSTANCE;
    }

    /**
     * in this function the file is read and dataset created
     *
     * @param DataFilePath - path to text file
     * @return - the datastore
     */
    public DataStore createDataStore(String DataFilePath) {
        logger.trace("Entering method createDataStore...");

        Set<String> forbiddenWords = new HashSet<>(Arrays.asList("Comuna", "Municipiul", "of", "Feredation", "Oraș", "Oras", "District", "Province"));

        BufferedReader bufferedReader;
        DataStore dataStore = new DataStore();

        List<String> columns;
        String country;
        Node root = null;
        Node newValue;
        Node parent = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(DataFilePath));
            String line = bufferedReader.readLine();

            while (line != null) {//process the line

                //spit the line with tabs
                columns = Arrays.asList(line.split("\t"));

                //check if new country started, create new node for it
                //and add all the alternate names for that country in the multimap dataset with the node
                country = columns.get(0);
                if (!country.isEmpty()) {
                    String[] countryNames = country.split(",");
                    root = new Node(null, countryNames[1]);
                    for (String countryName : countryNames) {
                        dataStore.getMapping().put(countryName.toLowerCase(), root);
                    }
                    parent = root;
                }

                //if line has state and city
                //process every value of line
                for (int i = 1; i < columns.size(); ++i) {

                    String columnValue = columns.get(i);

                    String[] names = columnValue.split(",");
                    String officialName = names[0];

                    //create node for the locations
                    newValue = new Node();
                    newValue.setParent(parent);
                    newValue.setName(officialName);
                    parent = newValue;

                    //split every location name in individual words, and add the hole name to the list
                    for (String name : names) {

                        List<String> wordsInName = new ArrayList<>(Arrays.asList(name.split("[- ]")));
                        //wordsInName.add(name);

                        int numberOfWords = wordsInName.size();
                        String aux;
                        for (int it = 2; it <= numberOfWords; ++it) {
                            for (int j = 0; j <= numberOfWords - it; ++j) {
                                aux = wordsInName.get(j);
                                for (int k = j + 1; k < j + it; ++k) {
                                    aux = aux.concat(" " + wordsInName.get(k));
                                }
                                wordsInName.add(aux);
                            }
                        }

                        //for each word, check if it already exists with the created node above
                        for (String word : wordsInName) {
                            if (forbiddenWords.contains(word))
                                continue;

                            //if node doesnt exist, add it to map
                            if (!dataStore.getMapping().get(word.toLowerCase()).contains(newValue)) {
                                dataStore.getMapping().put(word.toLowerCase(), newValue);
                            }
                        }
                    }
                }
                parent = root;

                // read next line
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return dataStore;
    }
}
