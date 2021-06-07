package edu.pa.address_corrector.node;

import edu.pa.address_corrector.address.Address;
import edu.pa.address_corrector.datastore.DataStore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NodeOperationsTest {

    @Test
    @DisplayName("Ensure function returns unknown in all fields when address cannot be corrected.")
    void findBestSolutions4() {

        NodeOperations nodeOperations = new NodeOperations();

        List<String>[] list = new List[2];
        list[0] = new ArrayList<>();
        list[1] = new ArrayList<>();

        Address address = nodeOperations.findBestSolutions(DataStore.getDataStore()
                ,list ,list, list, list, list, new ArrayList<>()).get(0);

        Address unknownAddress = new Address("unknown", "unknown"
                , "unknown", "none/unknown", "unknown"
                , "")    ;
        assertEquals(address, unknownAddress);
    }

    @Test
    @DisplayName("Ensure function works when there is no sector and third level location found.")
    void findBestSolutions1() {

        NodeOperations nodeOperations = new NodeOperations();

        List<String>[] list = new List[2];
        list[0] = new ArrayList<>();
        list[1] = new ArrayList<>();

        List<String>[] locationList = new List[2];

        locationList[0] = new ArrayList<>();
        locationList[1] = new ArrayList<>();
        locationList[0].add("teleorman");

        Address address = nodeOperations.findBestSolutions(DataStore.getDataStore(),list
                ,locationList, list, list, list, new ArrayList<>()).get(0);

        Address partialAddress = new Address("România", "Teleorman"
                , "unknown", "none/unknown", "unknown"
                , "")    ;
        assertEquals(address, partialAddress);
    }

    @Test
    @DisplayName("Ensure function works when only country is found.")
    void findBestSolutions2() {

        NodeOperations nodeOperations = new NodeOperations();

        List<String>[] list = new List[2];
        list[0] = new ArrayList<>();
        list[1] = new ArrayList<>();

        List<String>[] locationList = new List[2];

        locationList[0] = new ArrayList<>();
        locationList[1] = new ArrayList<>();
        locationList[0].add("ro");

        Address address = nodeOperations.findBestSolutions(DataStore.getDataStore(),locationList
                ,list, list, list, list, new ArrayList<>()).get(0);

        Address partialAddress = new Address("România", "unknown"
                , "unknown", "none/unknown", "unknown"
                , "")    ;
        assertEquals(partialAddress, address );
    }

    @Test
    @DisplayName("Make sure multiple solutions can be returned")
    void findBestSolutions3() {

        NodeOperations nodeOperations = new NodeOperations();

        List<String>[] list = new List[2];

        list[0] = new ArrayList<>();
        list[1] = new ArrayList<>();
        list[0].add("tokyo");
        list[1].add("tokyo");

        List<Address> addresses = nodeOperations.findBestSolutions(DataStore.getDataStore(),list
                ,list, list, list, list, new ArrayList<>());

        assertTrue(addresses.size() > 1);
    }
}