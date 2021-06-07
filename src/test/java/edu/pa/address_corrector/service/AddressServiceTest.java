package edu.pa.address_corrector.service;

import edu.pa.address_corrector.address.Address;
import edu.pa.address_corrector.datastore.DataStore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AddressServiceTest {

    @Test
    @DisplayName("Ensure method simplifyAddress deletes all unnecessary characters.")
    void simplifyAddress1() {
        Address address = new Address();
        address.setCountry("noOddCharacter?");
        address.setFirstLvlLocation("!<noOdd    Character!<");
        address.setSecondLvlLocation("noOdd\nCharacte>+r");
        address.setThirdLvlLocation("noOddCha,,,,,racter_");
        address.setPostalCode("no`~OddCharacter");
        address.setStreetLine("@#$%^&");

        AddressService addressService = new AddressService();
        address = addressService.simplifyAddress(address);

        Address simplifiedAddress = new Address("noOddCharacter",
                "noOdd Character", "noOdd Characte r",
                "noOddCha racter", "no OddCharacter", "");

        assertEquals(simplifiedAddress, address);
    }

    @Test
    @DisplayName("Ensure method simplifyAddress trims whitespace characters from exterior of string and" +
            " makes any other substring of whitespace character in a single space.")
    void simplifyAddress2() {
        Address address = new Address();
        address.setCountry("\tnoExtraWhitespaces???");
        address.setFirstLvlLocation("   noExtra    Whitespaces\n\n\t");
        address.setSecondLvlLocation("noExtra\n\n\n\t\t\n  White\t\t\n spaces");
        address.setThirdLvlLocation("noExtraWhi,,,,,tespaces_");
        address.setPostalCode("no`~ExtraWhitespaces          ");
        address.setStreetLine("@#$%^&noExtra@#$%^&Whitespaces@#$%^&");

        AddressService addressService = new AddressService();
        address = addressService.simplifyAddress(address);

        Address simplifiedAddress = new Address("noExtraWhitespaces",
                "noExtra Whitespaces", "noExtra White spaces",
                "noExtraWhi tespaces", "no ExtraWhitespaces",
                "noExtra Whitespaces");

        assertEquals(simplifiedAddress, address);
    }

    @Test
    @DisplayName("Ensure the correcting of an address works regardless of uppercase letters or lowercase.")
    void correctAddress1() {

        Address address = new Address();
        address.setCountry("eruMAnia");
        address.setFirstLvlLocation("iASi");
        address.setSecondLvlLocation("munIcIpIuL iAsi");
        address.setThirdLvlLocation("iasi");
        address.setPostalCode("123456234");
        address.setStreetLine("mamma mia");

        AddressService addressService = new AddressService();
        List<Address> addressList = addressService.correctAddress(address, DataStore.getDataStore());

        Address correcterAddress = new Address();
        correcterAddress.setCountry("România");
        correcterAddress.setFirstLvlLocation("Iaşi");
        correcterAddress.setSecondLvlLocation("Municipiul Iaşi");
        correcterAddress.setThirdLvlLocation("Iaşi");
        correcterAddress.setPostalCode("12345");
        correcterAddress.setStreetLine("");

        List<Address> correcterAddressList = new ArrayList<>();
        correcterAddressList.add(correcterAddress);

        assertEquals(correcterAddressList, addressList);

    }

    @Test
    @DisplayName("Ensure the correcting of an address works even if a values from fields are switched around.")
    void correctAddress2() {

        Address address = new Address();
        address.setSecondLvlLocation("erumania");
        address.setFirstLvlLocation("iasi");
        address.setCountry("municipiul iasi");
        address.setStreetLine("iasi");
        address.setThirdLvlLocation("123456234");
        address.setPostalCode("mamma mia");

        AddressService addressService = new AddressService();
        List<Address> addressList = addressService.correctAddress(address, DataStore.getDataStore());

        Address correcterAddress = new Address();
        correcterAddress.setCountry("România");
        correcterAddress.setFirstLvlLocation("Iaşi");
        correcterAddress.setSecondLvlLocation("Municipiul Iaşi");
        correcterAddress.setThirdLvlLocation("Iaşi");
        correcterAddress.setPostalCode("12345");
        correcterAddress.setStreetLine("");

        List<Address> correcterAddressList = new ArrayList<>();
        correcterAddressList.add(correcterAddress);

        assertEquals(correcterAddressList, addressList);

    }

    @Test
    @DisplayName("Ensure that a specific type of location in it's specific field matters more than the " +
            "same type of location but in another field.")
    void correctAddress3() {

        Address address = new Address();
        address.setCountry("ro brasov");
        address.setFirstLvlLocation("iasi");
        address.setSecondLvlLocation("");
        address.setThirdLvlLocation("iasi");
        address.setPostalCode("23457");
        address.setStreetLine("");

        AddressService addressService = new AddressService();
        List<Address> addressList = addressService.correctAddress(address, DataStore.getDataStore());

        Address correcterAddress = new Address();
        correcterAddress.setCountry("România");
        correcterAddress.setFirstLvlLocation("Iaşi");
        correcterAddress.setSecondLvlLocation("Municipiul Iaşi");
        correcterAddress.setThirdLvlLocation("Iaşi");
        correcterAddress.setPostalCode("23457");
        correcterAddress.setStreetLine("");

        List<Address> correcterAddressList = new ArrayList<>();
        correcterAddressList.add(correcterAddress);

        assertEquals(correcterAddressList, addressList);

    }
}