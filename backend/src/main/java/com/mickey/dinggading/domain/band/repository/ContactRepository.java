package com.mickey.dinggading.domain.band.repository;

import com.mickey.dinggading.domain.band.model.entity.Contact;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    void deleteAllByBand_BandId(Long bandBandId);

    Optional<Contact> findByContactIdAndBand_BandId(Long contactId, Long bandId);

    List<Contact> findAllByBand_BandId(Long bandId);
}