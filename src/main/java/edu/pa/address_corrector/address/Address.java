package edu.pa.address_corrector.address;

import lombok.*;

import java.io.Serializable;


/**
 * this class is used to represent an address.
 * an address should have:
 * country - a country
 * firstLvlLocation - a first level administrative location (like a state/judet)
 * secondLvlLocation - a second level administrative location (like a city/municipality)
 * thirdLvlLocation - a third level administrative location (like a sector/village)
 * postalCode - a 5 digit number
 * streetLine - any string containing spaces, dots and alphanumeric characters
 */

@EqualsAndHashCode
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class Address implements Serializable {
    private String country;
    private String firstLvlLocation;    // state
    private String secondLvlLocation;   // city/municipality
    private String thirdLvlLocation;    // sector/village
    private String postalCode;
    private String streetLine;
}
