package edu.pa.address_corrector.service;

import edu.pa.address_corrector.AddressCorrectorApplication;
import edu.pa.address_corrector.address.Address;
import edu.pa.address_corrector.address.CorrectedAddress;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Example.PropertySelector;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = AddressCorrectorApplication.class)
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
class CorrectedAddressServiceTest {

    @Autowired
    private CorrectedAddressService correctedAddressService;

    @Test
    void addToHistory() {

        Address inputAddress = new Address("test","test",
                "test", "test", "test", "test");
        Address correctAddress = new Address("test","test",
                "test", "test", "test", "test");

        correctedAddressService.addToHistory(inputAddress, correctAddress);

        String inputAddressString = "test" + "\t" + "test" + "\t" + "test" + "\t" + "test" + "\t" + "test" + "\t" + "test";
        String correctAddressString = "test" + "\t" + "test" + "\t" + "test" + "\t" + "test" + "\t" + "test" + "\t" + "test";
        
        List<CorrectedAddress> correctedAddressList = correctedAddressService.getRepository().findAll();
        
        boolean ok = false;
        Long testId = null;
        for(CorrectedAddress correctedAddress1 : correctedAddressList){
            if (correctedAddress1.getInputAddress().equals(inputAddressString) &&
                    correctedAddress1.getCorrectedAddress().equals(correctAddressString)) {
                testId = correctedAddress1.getId();
                ok = true;
                break;
            }
        }

        assertTrue(ok);

        correctedAddressService.getRepository().deleteById(testId);
    }
}