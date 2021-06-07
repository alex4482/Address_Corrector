package edu.pa.address_corrector.service;

import edu.pa.address_corrector.address.Address;
import edu.pa.address_corrector.datastore.DataStore;
import edu.pa.address_corrector.node.NodeOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * this class holds most methods which have to do only with Address objects,
 * to get information from Address objects
 */
@Service
public class AddressService {

    private static final Logger logger = LoggerFactory.getLogger(AddressService.class);

    /**
     * this function is used to eliminate characters which should not be in an address like ? or ! etc
     * and make all consecutive whitespace characters into a single space
     * and eliminate spaces from beginning and end
     * for every field in the Address object
     *
     * @return - a new Address object but with each field simplified
     */
    public Address simplifyAddress(Address oldAddress) {

        logger.trace("Entering method simplifyAddress...");

        Address newAddress = new Address();

        String after = oldAddress.getCountry();
        if (after == null)
            after = "";
        after = after.replaceAll("[^\\p{Alnum}'(*)./ ]", " ");
        after = after.trim().replaceAll("\\p{Space}+", " ");
        //after = after.replaceAll("[^\\p{Alnum}'(*)./ ]", "");
        newAddress.setCountry(after);


        after = oldAddress.getFirstLvlLocation();
        if (after == null)
            after = "";
        after = after.replaceAll("[^\\p{Alnum}'(*)./ ]", " ");
        after = after.trim().replaceAll("\\p{Space}+", " ");
        newAddress.setFirstLvlLocation(after);

        after = oldAddress.getSecondLvlLocation();
        if (after == null)
            after = "";
        after = after.replaceAll("[^\\p{Alnum}'(*)./ ]", " ");
        after = after.trim().replaceAll("\\p{Space}+", " ");
        newAddress.setSecondLvlLocation(after);

        after = oldAddress.getThirdLvlLocation();
        if (after == null)
            after = "";
        after = after.replaceAll("[^\\p{Alnum}'(*)./ ]", " ");
        after = after.trim().replaceAll("\\p{Space}+", " ");
        newAddress.setThirdLvlLocation(after);

        after = oldAddress.getPostalCode();
        if (after == null)
            after = "";
        after = after.replaceAll("[^\\p{Alnum}'(*)./ ]", " ");
        after = after.trim().replaceAll("\\p{Space}+", " ");
        newAddress.setPostalCode(after);

        after = oldAddress.getStreetLine();
        if (after == null)
            after = "";
        after = after.replaceAll("[^\\p{Alnum}'(*)./ ]", " ");
        after = after.trim().replaceAll("\\p{Space}+", " ");
        newAddress.setStreetLine(after);

        return newAddress;
    }

    /**
     * this function gets all possible country names from every field in an Address
     *
     * @return - 2 lists of Strings, which are potential names for countries
     * one list contains names found in the country field,
     * the other, names found in all other fields
     * country names must be a String of alphabetic characters, at least 2
     */
    private List<String>[] getPossibleCountries(Address oldAddress) {

        //create 2 arrays, one for the names found in the country field,
        // one for names found in the other fields
        ArrayList<String>[] posible = new ArrayList[2];
        posible[0] = new ArrayList<>();
        posible[1] = new ArrayList<>();

        //create 6 arrays, one for each field in the address
        ArrayList<String>[] namesAux = new ArrayList[6];

        for (int i = 0; i < 6; i++) {
            namesAux[i] = new ArrayList<>();
        }

        //add all words to their respective arrays
        namesAux[0].addAll(Arrays.asList(oldAddress.getCountry().split(" ")));
        namesAux[1].addAll(Arrays.asList(oldAddress.getFirstLvlLocation().split(" ")));
        namesAux[2].addAll(Arrays.asList(oldAddress.getSecondLvlLocation().split(" ")));
        namesAux[3].addAll(Arrays.asList(oldAddress.getThirdLvlLocation().split(" ")));
        namesAux[4].addAll(Arrays.asList(oldAddress.getPostalCode().split(" ")));
        namesAux[5].addAll(Arrays.asList(oldAddress.getStreetLine().split(" ")));

        List<String> posibleNamesOther = new ArrayList<>();
        List<String> posibleNamesCountry = new ArrayList<>();

        //make combinations of 2,3,etc words and add them to their lists for each field
        for (int ii = 0; ii < 6; ++ii) {
            int numberOfWords = namesAux[ii].size();
            String aux = "";
            for (int i = 2; i <= numberOfWords; ++i) {
                for (int j = 0; j <= numberOfWords - i; ++j) {
                    aux = namesAux[ii].get(j);
                    for (int k = j + 1; k < j + i; ++k) {
                        aux = aux.concat(" " + namesAux[ii].get(k));
                    }
                    namesAux[ii].add(aux);
                }
            }
            if (ii == 0) {
                posibleNamesCountry.addAll(namesAux[ii]);
            } else
                posibleNamesOther.addAll(namesAux[ii]);
        }

        //create a pattern with regular expressions
        Pattern patter = Pattern.compile("[\\p{Alnum}]{2,}( [\\p{Alnum}]{2,})*", Pattern.CASE_INSENSITIVE);
        Matcher match;

        //find all matches in every other field except the country field
        for (String posibleName : posibleNamesOther) {
            match = patter.matcher(posibleName);
            while (match.find()) {
                posible[1].add(match.group());
            }
        }

        //find all matches in the country field
        for (String posibleName : posibleNamesCountry) {
            match = patter.matcher(posibleName);
            while (match.find()) {
                posible[0].add(match.group());
            }
        }

        return posible;
    }

    //this functions is used for the same purpose as the getPossibleCountries function,
    //but used for finding names of states.
    //for now, no different methods were applied
    private List<String>[] getPossibleStates(Address oldAddress) {
        ArrayList<String>[] posible = new ArrayList[2];
        posible[0] = new ArrayList<>();
        posible[1] = new ArrayList<>();

        ArrayList<String>[] namesAux = new ArrayList[6];

        for (int i = 0; i < 6; i++) {
            namesAux[i] = new ArrayList<>();
        }

        namesAux[0].addAll(Arrays.asList(oldAddress.getCountry().split(" ")));
        namesAux[1].addAll(Arrays.asList(oldAddress.getFirstLvlLocation().split(" ")));
        namesAux[2].addAll(Arrays.asList(oldAddress.getSecondLvlLocation().split(" ")));
        namesAux[3].addAll(Arrays.asList(oldAddress.getThirdLvlLocation().split(" ")));
        namesAux[4].addAll(Arrays.asList(oldAddress.getPostalCode().split(" ")));
        namesAux[5].addAll(Arrays.asList(oldAddress.getStreetLine().split(" ")));

        List<String> posibleNamesOther = new ArrayList<>();
        List<String> posibleNamesState = new ArrayList<>();

        for (int ii = 0; ii < 6; ++ii) {
            int numberOfWords = namesAux[ii].size();
            String aux = "";
            for (int i = 2; i <= numberOfWords; ++i) {
                for (int j = 0; j <= numberOfWords - i; ++j) {
                    aux = namesAux[ii].get(j);
                    for (int k = j + 1; k < j + i; ++k) {
                        aux = aux.concat(" " + namesAux[ii].get(k));
                    }
                    namesAux[ii].add(aux);
                }
            }
            if (ii == 1) {
                posibleNamesState.addAll(namesAux[ii]);
            } else
                posibleNamesOther.addAll(namesAux[ii]);
        }

        Pattern patter = Pattern.compile("[\\p{Alnum}]{2,}( [\\p{Alnum}]{2,})*", Pattern.CASE_INSENSITIVE);
        Matcher match;

        for (String posibleName : posibleNamesOther) {
            match = patter.matcher(posibleName);
            while (match.find()) {
                posible[1].add(match.group());
            }
        }

        for (String posibleName : posibleNamesState) {
            match = patter.matcher(posibleName);
            while (match.find()) {
                posible[0].add(match.group());
            }
        }

        return posible;
    }

    //this functions is used for the same purpose as the getPossibleCountries function,
    //but used for finding names of cities.
    //for now, no different methods were applied
    private List<String>[] getPossibleCities(Address oldAddress) {
        ArrayList<String>[] posible = new ArrayList[2];
        posible[0] = new ArrayList<>();
        posible[1] = new ArrayList<>();

        ArrayList<String>[] namesAux = new ArrayList[6];

        for (int i = 0; i < 6; i++) {
            namesAux[i] = new ArrayList<>();
        }

        namesAux[0].addAll(Arrays.asList(oldAddress.getCountry().split(" ")));
        namesAux[1].addAll(Arrays.asList(oldAddress.getFirstLvlLocation().split(" ")));
        namesAux[2].addAll(Arrays.asList(oldAddress.getSecondLvlLocation().split(" ")));
        namesAux[3].addAll(Arrays.asList(oldAddress.getThirdLvlLocation().split(" ")));
        namesAux[4].addAll(Arrays.asList(oldAddress.getPostalCode().split(" ")));
        namesAux[5].addAll(Arrays.asList(oldAddress.getStreetLine().split(" ")));

        List<String> posibleNamesOther = new ArrayList<>();
        List<String> posibleNamesCountry = new ArrayList<>();

        for (int ii = 0; ii < 6; ++ii) {
            int numberOfWords = namesAux[ii].size();
            String aux = "";
            for (int i = 2; i <= numberOfWords; ++i) {
                for (int j = 0; j <= numberOfWords - i; ++j) {
                    aux = namesAux[ii].get(j);
                    for (int k = j + 1; k < j + i; ++k) {
                        aux = aux.concat(" " + namesAux[ii].get(k));
                    }
                    namesAux[ii].add(aux);
                }
            }
            if (ii == 2) {
                posibleNamesCountry.addAll(namesAux[ii]);
            } else
                posibleNamesOther.addAll(namesAux[ii]);
        }

        Pattern patter = Pattern.compile("[\\p{Alnum}]{2,}( [\\p{Alnum}]{2,})*", Pattern.CASE_INSENSITIVE);
        Matcher match;

        for (String posibleName : posibleNamesOther) {
            match = patter.matcher(posibleName);
            while (match.find()) {
                posible[1].add(match.group());
            }
        }

        for (String posibleName : posibleNamesCountry) {
            match = patter.matcher(posibleName);
            while (match.find()) {
                posible[0].add(match.group());
            }
        }

        return posible;
    }

    //this functions is used for the same purpose as the getPossibleCountries function,
    //but used for finding names of sectors.
    //for now, no different methods were applied
    private List<String>[] getPossibleSectors(Address oldAddress) {
        ArrayList<String>[] posible = new ArrayList[2];
        posible[0] = new ArrayList<>();
        posible[1] = new ArrayList<>();

        ArrayList<String>[] namesAux = new ArrayList[6];

        for (int i = 0; i < 6; i++) {
            namesAux[i] = new ArrayList<>();
        }

        namesAux[0].addAll(Arrays.asList(oldAddress.getCountry().split(" ")));
        namesAux[1].addAll(Arrays.asList(oldAddress.getFirstLvlLocation().split(" ")));
        namesAux[2].addAll(Arrays.asList(oldAddress.getSecondLvlLocation().split(" ")));
        namesAux[3].addAll(Arrays.asList(oldAddress.getThirdLvlLocation().split(" ")));
        namesAux[4].addAll(Arrays.asList(oldAddress.getPostalCode().split(" ")));
        namesAux[5].addAll(Arrays.asList(oldAddress.getStreetLine().split(" ")));

        List<String> posibleNamesOther = new ArrayList<>();
        List<String> posibleNamesCountry = new ArrayList<>();

        for (int ii = 0; ii < 6; ++ii) {
            int numberOfWords = namesAux[ii].size();
            String aux = "";
            for (int i = 2; i <= numberOfWords; ++i) {
                for (int j = 0; j <= numberOfWords - i; ++j) {
                    aux = namesAux[ii].get(j);
                    for (int k = j + 1; k < j + i; ++k) {
                        aux = aux.concat(" " + namesAux[ii].get(k));
                    }
                    namesAux[ii].add(aux);
                }
            }
            if (ii == 3) {
                posibleNamesCountry.addAll(namesAux[ii]);
            } else
                posibleNamesOther.addAll(namesAux[ii]);
        }

        Pattern patter = Pattern.compile("[\\p{Alnum}]{2,}( [\\p{Alnum}]{2,})*", Pattern.CASE_INSENSITIVE);
        Matcher match;

        for (String posibleName : posibleNamesOther) {
            match = patter.matcher(posibleName);
            while (match.find()) {
                posible[1].add(match.group());
            }
        }

        for (String posibleName : posibleNamesCountry) {
            match = patter.matcher(posibleName);
            while (match.find()) {
                posible[0].add(match.group());
            }
        }

        return posible;
    }

    //this functions is used for the same purpose as the getPossibleCountries function,
    //but used for finding postal codes.
    //for now, this function only differs slightly.
    private List<String>[] getPossiblePostalCodes(Address oldAddress) {

        ArrayList<String>[] posible = new ArrayList[2];
        posible[0] = new ArrayList<>();
        posible[1] = new ArrayList<>();

        ArrayList<String>[] namesAux = new ArrayList[6];

        for (int i = 0; i < 6; i++) {
            namesAux[i] = new ArrayList<>();
        }

        namesAux[0].addAll(Arrays.asList(oldAddress.getCountry().split(" ")));
        namesAux[1].addAll(Arrays.asList(oldAddress.getFirstLvlLocation().split(" ")));
        namesAux[2].addAll(Arrays.asList(oldAddress.getSecondLvlLocation().split(" ")));
        namesAux[3].addAll(Arrays.asList(oldAddress.getThirdLvlLocation().split(" ")));
        namesAux[4].addAll(Arrays.asList(oldAddress.getPostalCode().split(" ")));
        namesAux[5].addAll(Arrays.asList(oldAddress.getStreetLine().split(" ")));

        //the pattern searches for a 5 digit number
        Pattern patter = Pattern.compile("[\\p{Digit}]{5}");
        Matcher match;

        //search for matches straight in every field, and add findings in their respective lists
        for (int i = 0; i < 6; ++i) {
            int k;
            if (i != 4)
                k = 1;
            else
                k = 0;
            for (String posiblePostalCode : namesAux[i]) {
                match = patter.matcher(posiblePostalCode);
                while (match.find()) {
                    posible[k].add(match.group());
                }
            }
        }

        return posible;
    }

    //this functions is used for finding street line names,
    //for now, there are no implemented methods, and the string returned is the street line field in the address
    private List<String> getPossibleStreetLines(Address oldAddress) {
        List<String> posible = new ArrayList<>();

        posible.add(oldAddress.getStreetLine());

        return posible;
    }

    /**
     * this function gets the input address, a datastore for information, and finds all possible names
     * for every field, and calls the findBestSolutions in the NodeOperations class to find
     * all possible nodes with the given names lists and find the best solutions
     */
    public List<Address> correctAddress(Address inputAddress, DataStore dataStore) {
        logger.trace("Entering method correctAddress...");

        inputAddress = simplifyAddress(inputAddress);
        List<String>[] posibleCountries = getPossibleCountries(inputAddress);
        List<String>[] posibleStates = getPossibleStates(inputAddress);
        List<String>[] posibleCities = getPossibleCities(inputAddress);
        List<String>[] posibleSectors = getPossibleSectors(inputAddress);
        List<String>[] posiblePostalCodes = getPossiblePostalCodes(inputAddress);
        List<String> posibleStreetLines = getPossibleStreetLines(inputAddress);

        NodeOperations nodeOperations = new NodeOperations();

        List<Address> correctAddresses = nodeOperations
                .findBestSolutions(dataStore, posibleCountries, posibleStates, posibleCities,
                        posibleSectors, posiblePostalCodes, posibleStreetLines);

        logger.info(correctAddresses.toString());

        return correctAddresses;
    }

}
