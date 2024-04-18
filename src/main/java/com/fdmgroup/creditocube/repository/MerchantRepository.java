package com.fdmgroup.creditocube.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fdmgroup.creditocube.model.Merchant;

/**
 * Repository interface for Merchant entities.
 *
 * @author Wong Mann Joe
 * @version 1.0
 * @since 2023-04-16
 */
public interface MerchantRepository extends JpaRepository<Merchant, String> {

    /**
     * Finds a Merchant by its unique merchant code.
     *
     * @param merchantCode A String identifying the merchant code to search for.
     * @return An Optional containing the found Merchant, or an empty Optional if no Merchant is found.
     */
    Optional<Merchant> findByMerchantCode(String merchantCode);

}
