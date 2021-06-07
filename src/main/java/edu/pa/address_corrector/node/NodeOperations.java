package edu.pa.address_corrector.node;

import edu.pa.address_corrector.address.Address;
import edu.pa.address_corrector.datastore.DataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * this class holds all operations which include working with Node objects
 */
public class NodeOperations {

    private static final Logger logger = LoggerFactory.getLogger(NodeOperations.class);

    //scores when hitting a location type, used in the algorithm
    private final int scoreCountry = 1;
    private final int scoreState = 4;
    private final int scoreCity = 10;
    private final int scoreSector = 14;
    private final int scoreExactField = 2;

    /**
     * this function is used to find the best Address from
     * given lists of possible names of location types
     *
     * @param datastore           - the datastore used
     * @param possibleCountries   - all possible names for a country found in the Address
     * @param possibleStates      - all possible names for a state found in the Address
     * @param possibleCities      - all possible names for a city found in the Address
     * @param possibleSectors     - all possible names for a sector found in the Address
     * @param possiblePostalCodes - all possible postal codes found in the Address
     * @param possibleStreetLines - all possible street line names found in the Address
     * @return - a list of Addresses, which are the best match, meaning they have the same score
     */
    public List<Address> findBestSolutions(DataStore datastore, List<String>[] possibleCountries,
                                           List<String>[] possibleStates, List<String>[] possibleCities,
                                           List<String>[] possibleSectors, List<String>[] possiblePostalCodes,
                                           List<String> possibleStreetLines) {
        logger.trace("Entered method findBestSolutions...");

        //with all the possible names for a location, find all nodes for that type of location that match those names
        List<Pair<List<String>, List<Node>>> nodesCountry = getPossibleNodes(datastore, possibleCountries, 0);
        List<Pair<List<String>, List<Node>>> nodesState = getPossibleNodes(datastore, possibleStates, 1);
        List<Pair<List<String>, List<Node>>> nodesCity = getPossibleNodes(datastore, possibleCities, 2);
        List<Pair<List<String>, List<Node>>> nodesSector = getPossibleNodes(datastore, possibleSectors, 3);

        List<Address> bestAddresses = new ArrayList<>();
        String bestPostalCode;
        String bestStreetLine;// = possibleStreetLines.get(0);

        bestStreetLine = "";

        //check if there is a possible postal code
        if (possiblePostalCodes[0].isEmpty()) {
            if (possiblePostalCodes[1].isEmpty()) {
                bestPostalCode = "unknown";
            } else {
                bestPostalCode = possiblePostalCodes[1].get(0);
            }
        } else {
            bestPostalCode = possiblePostalCodes[0].get(0);
        }

        //check if there are no possible solutions, return unknown
        if (nodesSector.get(0).getValue().isEmpty() && nodesSector.get(1).getValue().isEmpty()) {
            Address bestAddress = new Address();
            bestAddress.setThirdLvlLocation("none/unknown");
            if (nodesCity.get(0).getValue().isEmpty() && nodesCity.get(1).getValue().isEmpty()) {
                bestAddress.setSecondLvlLocation("unknown");
                if (nodesState.get(0).getValue().isEmpty() && nodesState.get(1).getValue().isEmpty()) {
                    bestAddress.setFirstLvlLocation("unknown");
                    if (nodesCountry.get(0).getValue().isEmpty() && nodesCountry.get(1).getValue().isEmpty()) {
                        bestAddress.setCountry("unknown");
                        bestAddress.setStreetLine(bestStreetLine);
                        bestAddress.setPostalCode(bestPostalCode);
                        bestAddresses.add(bestAddress);

                        logger.info("Address could not be corrected");
                        return bestAddresses;
                    }
                }
            }
        }

        int bestScore = 0, score;

        //find all valid solutions by starting from sectors and
        // checking if their corresponding cities/states/countries are possible locations as well
        // to build up the score, and keep only those with the highest score
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < nodesSector.get(i).getValue().size(); ++j) {
                score = scoreSector;
                if (i == 0)
                    score += scoreExactField;
                Node node = nodesSector.get(i).getValue().get(j);

                if (nodesCity.get(0).getValue().contains(node.getParent())) {
                    score += scoreExactField + scoreCity;
                } else {
                    if (nodesCity.get(1).getValue().contains(node.getParent()))
                        score += scoreCity;
                }

                if (nodesState.get(0).getValue().contains(node.getParent().getParent())) {
                    score += scoreExactField + scoreState;
                } else {
                    if (nodesState.get(1).getValue().contains(node.getParent().getParent()))
                        score += scoreState;
                }

                if (nodesCountry.get(0).getValue().contains(node.getParent().getParent().getParent())) {
                    score += scoreExactField + scoreCountry;
                } else {
                    if (nodesCountry.get(1).getValue().contains(node.getParent().getParent().getParent()))
                        score += scoreCountry;
                }

                if (score >= bestScore) {
                    if (score > bestScore) {
                        bestAddresses = new ArrayList<>();
                        bestScore = score;
                    }

                    Address newAddress = new Address();
                    newAddress.setThirdLvlLocation(node.getName());
                    newAddress.setSecondLvlLocation(node.getParent().getName());
                    newAddress.setFirstLvlLocation(node.getParent().getParent().getName());
                    newAddress.setCountry(node.getParent().getParent().getParent().getName());
                    newAddress.setPostalCode(bestPostalCode);
                    newAddress.setStreetLine(bestStreetLine);

                    if (!bestAddresses.contains(newAddress)) {
                        bestAddresses.add(newAddress);
                    }
                }
            }
        }

        //find all valid solutions by starting from cities and
        // checking if their corresponding states/countries are possible locations as well
        // to build up the score, and keep only those with the highest score
        // in case the sector is unknown
        if (bestScore < 14)
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < nodesCity.get(i).getValue().size(); ++j) {
                    score = scoreCity;
                    if (i == 0)
                        score += scoreExactField;
                    Node node = nodesCity.get(i).getValue().get(j);

                    if (nodesState.get(0).getValue().contains(node.getParent())) {
                        score += scoreExactField + scoreState;
                    } else {
                        if (nodesState.get(1).getValue().contains(node.getParent()))
                            score += scoreState;
                    }

                    if (nodesCountry.get(0).getValue().contains(node.getParent().getParent())) {
                        score += scoreExactField + scoreCountry;
                    } else {
                        if (nodesCountry.get(1).getValue().contains(node.getParent().getParent()))
                            score += scoreCountry;
                    }

                    if (score >= bestScore) {
                        if (score > bestScore) {
                            bestAddresses = new ArrayList<>();
                            bestScore = score;
                        }
                        Address newAddress = new Address();
                        newAddress.setThirdLvlLocation("none/unknown");
                        newAddress.setSecondLvlLocation(node.getName());
                        newAddress.setFirstLvlLocation(node.getParent().getName());
                        newAddress.setCountry(node.getParent().getParent().getName());
                        newAddress.setPostalCode(bestPostalCode);
                        newAddress.setStreetLine(bestStreetLine);

                        if (!bestAddresses.contains(newAddress)) {
                            bestAddresses.add(newAddress);
                        }
                    }
                }
            }

        //find all valid solutions by starting from states and
        // checking if their corresponding countries are possible locations as well
        // to build up the score, and keep only those with the highest score
        // in case the city and sector are unknown
        if (bestScore < 10)
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < nodesState.get(i).getValue().size(); ++j) {
                    score = scoreState;
                    if (i == 0)
                        score += scoreExactField;
                    Node node = nodesState.get(i).getValue().get(j);

                    if (nodesCountry.get(0).getValue().contains(node.getParent())) {
                        score += scoreExactField + scoreCountry;
                    } else {
                        if (nodesCountry.get(1).getValue().contains(node.getParent()))
                            score += scoreCountry;
                    }

                    if (score >= bestScore) {
                        if (score > bestScore) {
                            bestAddresses = new ArrayList<>();
                            bestScore = score;
                        }
                        Address newAddress = new Address();
                        newAddress.setThirdLvlLocation("none/unknown");
                        newAddress.setSecondLvlLocation("unknown");
                        newAddress.setFirstLvlLocation(node.getName());
                        newAddress.setCountry(node.getParent().getName());
                        newAddress.setPostalCode(bestPostalCode);
                        newAddress.setStreetLine(bestStreetLine);

                        if (!bestAddresses.contains(newAddress)) {
                            bestAddresses.add(newAddress);
                        }
                    }
                }
            }

        //find all valid solutions by starting from countries
        // to build up the score, and keep only those with the highest score
        // in case the other fields are unknown
        if (bestScore < 4)
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < nodesCountry.get(i).getValue().size(); ++j) {
                    score = scoreCountry;
                    if (i == 0)
                        score += scoreExactField;
                    Node node = nodesCountry.get(i).getValue().get(j);

                    if (score >= bestScore) {
                        if (score > bestScore) {
                            bestAddresses = new ArrayList<>();
                            bestScore = score;
                        }
                        Address newAddress = new Address();
                        newAddress.setThirdLvlLocation("none/unknown");
                        newAddress.setSecondLvlLocation("unknown");
                        newAddress.setFirstLvlLocation("unknown");
                        newAddress.setCountry(node.getName());
                        newAddress.setPostalCode(bestPostalCode);
                        newAddress.setStreetLine(bestStreetLine);

                        if (!bestAddresses.contains(newAddress)) {
                            bestAddresses.add(newAddress);
                        }
                    }
                }
            }


        return bestAddresses;
    }

    /**
     * this function is used to get all possible nodes for a specified type of location
     * using a given list of location names
     *
     * @param datastore         - the datastore that holds the multimap used to find all possible nodes
     * @param possibleLocations - list with all possible location names that have to be searched in the multimap
     * @param heightOfLocation  - 0 if looking for country nodes
     *                          1 if looking for state nodes
     *                          2 if looking for city nodes
     *                          3 if looking for sector nodes
     * @return - a list of all nodes found in the multimap
     */
    private List<Pair<List<String>, List<Node>>> getPossibleNodes(DataStore datastore, List<String>[] possibleLocations, int heightOfLocation) {

        logger.info("Entering method getPossibleNodes with height of location = " + heightOfLocation);

        List<Node> possibleNodes = new ArrayList<>();
        List<String> possibleNodesNames = new ArrayList<>();

        List<Node> possibleNodesOther = new ArrayList<>();
        List<String> possibleNodesNamesOther = new ArrayList<>();

        for (String location : possibleLocations[0]) {
            for (Node node : datastore.getMapping().get(location.toLowerCase())) {
                possibleNodes.add(node);
                //possibleNodesNames.add(location);
                possibleNodesNames.add(node.getName());
            }
        }

        for (int i = 0; i < possibleNodes.size(); ++i) {
            if (getHeightOfNode(possibleNodes.get(i)) != heightOfLocation) {
                possibleNodes.remove(i);
                possibleNodesNames.remove(i);
                i--;
            }
        }

        for (String location : possibleLocations[1]) {

            for (Node node : datastore.getMapping().get(location.toLowerCase())) {
                possibleNodesOther.add(node);
                //possibleNodesNames.add(location);
                possibleNodesNamesOther.add(node.getName());
            }
        }

        for (int i = 0; i < possibleNodesOther.size(); ++i) {
            if (getHeightOfNode(possibleNodesOther.get(i)) != heightOfLocation) {
                possibleNodesOther.remove(i);
                possibleNodesNamesOther.remove(i);
                i--;
            }
        }

        List<Pair<List<String>, List<Node>>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(possibleNodesNames, possibleNodes));
        pairs.add(new Pair<>(possibleNodesNamesOther, possibleNodesOther));

        return pairs;
    }

    /**
     * this function is used to get the height of a node
     *
     * @return - 0 if parent is null
     * - 1 if parent's parent is null
     * - 2 if parent's parent's parent is null
     * - 3 if parent's parent's parent's parent is null
     */
    private int getHeightOfNode(Node node) {
        int height = 0;
        Node parent = node.getParent();
        while (parent != null) {
            height++;
            node = parent;
            parent = node.getParent();
        }
        return height;
    }
}
