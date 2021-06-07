package edu.pa.address_corrector.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.pa.address_corrector.address.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Ensure that requests to correct an address work.")
    void getCorrectedAddress() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/v1/correct-address" +
                "?country=erumania&firstLvlLocation=iasi" +
                "&postalCode=51234&secondLvlLocation=municipiul%20iasi" +
                "&streetLine=nihao&thirdLvlLocation=iasi");

        MvcResult result = mockMvc.perform(request).andReturn();

        Address address = new Address();
        address.setCountry("România");
        address.setFirstLvlLocation("Iaşi");
        address.setSecondLvlLocation("Municipiul Iaşi");
        address.setThirdLvlLocation("Iaşi");
        address.setPostalCode("51234");
        address.setStreetLine("");

        List<Address> correctAddress = new ArrayList<>();
        correctAddress.add(address);

        List<Address> requestedAddresses = new ObjectMapper().readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<List<Address>>(){});

        assertEquals(correctAddress, requestedAddresses);
    }
}