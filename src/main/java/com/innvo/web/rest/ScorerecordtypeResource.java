package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Scorerecordtype;

import com.innvo.repository.ScorerecordtypeRepository;
import com.innvo.repository.search.ScorerecordtypeSearchRepository;
import com.innvo.web.rest.util.HeaderUtil;
import com.innvo.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Scorerecordtype.
 */
@RestController
@RequestMapping("/api")
public class ScorerecordtypeResource {

    private final Logger log = LoggerFactory.getLogger(ScorerecordtypeResource.class);

    private static final String ENTITY_NAME = "scorerecordtype";

    private final ScorerecordtypeRepository scorerecordtypeRepository;

    private final ScorerecordtypeSearchRepository scorerecordtypeSearchRepository;

    public ScorerecordtypeResource(ScorerecordtypeRepository scorerecordtypeRepository, ScorerecordtypeSearchRepository scorerecordtypeSearchRepository) {
        this.scorerecordtypeRepository = scorerecordtypeRepository;
        this.scorerecordtypeSearchRepository = scorerecordtypeSearchRepository;
    }

    /**
     * POST  /scorerecordtypes : Create a new scorerecordtype.
     *
     * @param scorerecordtype the scorerecordtype to create
     * @return the ResponseEntity with status 201 (Created) and with body the new scorerecordtype, or with status 400 (Bad Request) if the scorerecordtype has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/scorerecordtypes")
    @Timed
    public ResponseEntity<Scorerecordtype> createScorerecordtype(@Valid @RequestBody Scorerecordtype scorerecordtype) throws URISyntaxException {
        log.debug("REST request to save Scorerecordtype : {}", scorerecordtype);
        if (scorerecordtype.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new scorerecordtype cannot already have an ID")).body(null);
        }
        Scorerecordtype result = scorerecordtypeRepository.save(scorerecordtype);
        scorerecordtypeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/scorerecordtypes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /scorerecordtypes : Updates an existing scorerecordtype.
     *
     * @param scorerecordtype the scorerecordtype to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated scorerecordtype,
     * or with status 400 (Bad Request) if the scorerecordtype is not valid,
     * or with status 500 (Internal Server Error) if the scorerecordtype couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/scorerecordtypes")
    @Timed
    public ResponseEntity<Scorerecordtype> updateScorerecordtype(@Valid @RequestBody Scorerecordtype scorerecordtype) throws URISyntaxException {
        log.debug("REST request to update Scorerecordtype : {}", scorerecordtype);
        if (scorerecordtype.getId() == null) {
            return createScorerecordtype(scorerecordtype);
        }
        Scorerecordtype result = scorerecordtypeRepository.save(scorerecordtype);
        scorerecordtypeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, scorerecordtype.getId().toString()))
            .body(result);
    }

    /**
     * GET  /scorerecordtypes : get all the scorerecordtypes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of scorerecordtypes in body
     */
    @GetMapping("/scorerecordtypes")
    @Timed
    public ResponseEntity<List<Scorerecordtype>> getAllScorerecordtypes(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Scorerecordtypes");
        Page<Scorerecordtype> page = scorerecordtypeRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/scorerecordtypes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /scorerecordtypes/:id : get the "id" scorerecordtype.
     *
     * @param id the id of the scorerecordtype to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the scorerecordtype, or with status 404 (Not Found)
     */
    @GetMapping("/scorerecordtypes/{id}")
    @Timed
    public ResponseEntity<Scorerecordtype> getScorerecordtype(@PathVariable Long id) {
        log.debug("REST request to get Scorerecordtype : {}", id);
        Scorerecordtype scorerecordtype = scorerecordtypeRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(scorerecordtype));
    }

    /**
     * DELETE  /scorerecordtypes/:id : delete the "id" scorerecordtype.
     *
     * @param id the id of the scorerecordtype to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/scorerecordtypes/{id}")
    @Timed
    public ResponseEntity<Void> deleteScorerecordtype(@PathVariable Long id) {
        log.debug("REST request to delete Scorerecordtype : {}", id);
        scorerecordtypeRepository.delete(id);
        scorerecordtypeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/scorerecordtypes?query=:query : search for the scorerecordtype corresponding
     * to the query.
     *
     * @param query the query of the scorerecordtype search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/scorerecordtypes")
    @Timed
    public ResponseEntity<List<Scorerecordtype>> searchScorerecordtypes(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Scorerecordtypes for query {}", query);
        Page<Scorerecordtype> page = scorerecordtypeSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/scorerecordtypes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
