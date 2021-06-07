package edu.pa.address_corrector.service;

import edu.pa.address_corrector.address.Address;
import edu.pa.address_corrector.address.CorrectedAddress;
import edu.pa.address_corrector.repository.CorrectedAddressRepo;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * this class holds functions which work with CorrectAddress and Address objects and adds them to the database
 */
@Getter
@Service
public class CorrectedAddressService {

    private static final Logger logger = LoggerFactory.getLogger(CorrectedAddressService.class);

    @Autowired
    private CorrectedAddressRepo repository;

    /**
     * this function is used to add the input address and a correct variant of that address to the database
     */
    public void addToHistory(Address inputAddress, Address correctAddress) {
        logger.trace("Adding corrected Address to the database.");


        //put every field in the Address obj in a string, separated by tabs
        String inputAddressString = inputAddress.getCountry() + "\t"
                + inputAddress.getFirstLvlLocation() + "\t"
                + inputAddress.getSecondLvlLocation() + "\t"
                + inputAddress.getThirdLvlLocation() + "\t"
                + inputAddress.getPostalCode() + "\t"
                + inputAddress.getStreetLine();

        //put every field in the Address obj in a string, separated by tabs
        String correctAddressString = correctAddress.getCountry() + "\t"
                + correctAddress.getFirstLvlLocation() + "\t"
                + correctAddress.getSecondLvlLocation() + "\t"
                + correctAddress.getThirdLvlLocation() + "\t"
                + correctAddress.getPostalCode() + "\t"
                + correctAddress.getStreetLine();

        //save in the database the a new CorrectedAddress object, which holds the 2 created above strings
        repository.save(new CorrectedAddress(inputAddressString, correctAddressString));
    }
}
