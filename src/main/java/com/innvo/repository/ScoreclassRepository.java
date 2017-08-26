package com.innvo.repository;

import com.innvo.domain.Scoreclass;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Scoreclass entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScoreclassRepository extends JpaRepository<Scoreclass,Long> {
    
}
