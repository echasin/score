package com.innvo.repository;

import com.innvo.domain.Scoretype;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Scoretype entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScoretypeRepository extends JpaRepository<Scoretype,Long> {
    
}
