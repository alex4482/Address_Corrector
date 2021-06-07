package edu.pa.address_corrector.controller;

import edu.pa.address_corrector.address.Address;
import edu.pa.address_corrector.address.CorrectedAddress;
import edu.pa.address_corrector.datastore.DataStore;
import edu.pa.address_corrector.service.AddressService;
import edu.pa.address_corrector.service.CorrectedAddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


/**
 * this class represents the requests controller for an address
 */
@RestController
@RequestMapping(value = "/api/v1")
public class AddressController {

    private final static Logger logger = LoggerFactory.getLogger(AddressController.class.getName());

    @Autowired
    AddressService addressService;

    @Autowired
    CorrectedAddressService correctedAddressService;

    /**
     * gets an address and returns a list of all correct variants of that address
     * either by correcting it and adding the address and correct options to the databse
     * either by getting the results from the database
     *
     * @param address - the given address
     * @return - the options for the corrected address
     */
    @GetMapping("/correct-address")
    public List<Address> getCorrectedAddress(Address address) {
        logger.info("Received request for correcting address.");
        logger.trace("The given address is: " + address.toString());

        //put the given address in a single string separated with tabs
        String inputAddress = address.getCountry()
                + "\t" + address.getFirstLvlLocation()
                + "\t" + address.getSecondLvlLocation()
                + "\t" + address.getThirdLvlLocation()
                + "\t" + address.getPostalCode()
                + "\t" + address.getStreetLine();

        //get all addresses in the database
        List<CorrectedAddress> inputHistory = correctedAddressService.getRepository().findAll();

        //remove addresses that dont have the input address same as the current given address's inputAddress
        inputHistory.removeIf(input -> !input.getInputAddress().equals(inputAddress));

        //if empty it means that this address has not been given before,
        // is not in the database and must be corrected
        // and the results added to the database
        if (inputHistory.isEmpty()) {
            logger.trace("Address is not in database. Must be corrected using the algorithm.");

            address = addressService.simplifyAddress(address);
            List<Address> correctedAddresses = addressService.correctAddress(address, DataStore.getDataStore());

            for (Address addr : correctedAddresses) {
                correctedAddressService.addToHistory(address, addr);
            }
            return correctedAddresses;
        }
        //otherwise get from the database the results and parse them to create Address objects
        else {
            logger.trace("Address found in database. Algorithm will not be used in this case.");

            List<Address> correctedAddresses = new ArrayList<>();
            for (CorrectedAddress addr : inputHistory) {
                String[] fields = addr.getCorrectedAddress().split("\t");
                correctedAddresses.add(new Address(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5]));
            }

            return correctedAddresses;
        }
    }

}
