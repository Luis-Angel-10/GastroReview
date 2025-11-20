package websiters.gastroreview.repository;

import websiters.gastroreview.model.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {

    Page<Address> findByCityIgnoreCase(String city, Pageable pageable);

    Page<Address> findByCountryIgnoreCase(String country, Pageable pageable);

    Page<Address> findByStreetContainingIgnoreCase(String street, Pageable pageable);

    Page<Address> findByCityIgnoreCaseAndCountryIgnoreCase(String city, String country, Pageable pageable);

    boolean existsByStreetIgnoreCaseAndCityIgnoreCaseAndPostalCode(
            String street, String city, Integer postalCode);
}
