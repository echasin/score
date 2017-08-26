package com.innvo.repository;

import com.innvo.domain.Scorerecordtype;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Scorerecordtype entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScorerecordtypeRepository extends JpaRepository<Scorerecordtype,Long> {
    
}
