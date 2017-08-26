package com.innvo.repository;

import com.innvo.domain.Scorecategory;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Scorecategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScorecategoryRepository extends JpaRepository<Scorecategory,Long> {
    
}
