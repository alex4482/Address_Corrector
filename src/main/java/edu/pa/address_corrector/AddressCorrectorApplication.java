package edu.pa.address_corrector;

import edu.pa.address_corrector.datastore.DataStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AddressCorrectorApplication {

    public static void main(String[] args) {
        DataStore.getDataStore();
        SpringApplication.run(AddressCorrectorApplication.class, args);
    }

}
