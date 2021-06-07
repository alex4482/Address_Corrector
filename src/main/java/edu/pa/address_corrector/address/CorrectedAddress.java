package edu.pa.address_corrector.address;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;


/**
 * this class is used to represent an object from the corrected_address table in the database
 * where an entry represents a given address to be corrected and one of it's correct variants.
 * <p>
 * when an address is given to be corrected, first the database is checked to see if the address
 * has been corrected before, and if it has, the result/s are taken from the database, otherwise the
 * address is corrected with the algorithm and then saved in the database.
 * <p>
 * inputAddress - the given address, an Address object that has been simplified but with all
 * the fields saved in one string, separated by tabs '\t'
 * <p>
 * correctedAddress - one of the variants of the given address after it was corrected.
 * an address may have multiple viable options for correction
 */
@Getter
@Entity
@Setter
@NoArgsConstructor
@ToString
@Table(name = "corrected_address", catalog = "pa_project", schema = "public")
public class CorrectedAddress {

    @Column(name = "input_address")
    private String inputAddress;

    @Column(name = "corrected_address")
    private String correctedAddress;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    public CorrectedAddress(String input, String corrected) {
        inputAddress = input;
        correctedAddress = corrected;
    }

}
