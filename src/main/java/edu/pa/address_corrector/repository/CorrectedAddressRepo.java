package edu.pa.address_corrector.repository;

import edu.pa.address_corrector.address.CorrectedAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CorrectedAddressRepo extends JpaRepository<CorrectedAddress, Long> {
}
