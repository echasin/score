package com.innvo.repository;

import com.innvo.domain.Scorestatus;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Scorestatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScorestatusRepository extends JpaRepository<Scorestatus,Long> {
    
}
