package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Scorestatus;

import com.innvo.repository.ScorestatusRepository;
import com.innvo.repository.search.ScorestatusSearchRepository;
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
 * REST controller for managing Scorestatus.
 */
@RestController
@RequestMapping("/api")
public class ScorestatusResource {

    private final Logger log = LoggerFactory.getLogger(ScorestatusResource.class);

    private static final String ENTITY_NAME = "scorestatus";

    private final ScorestatusRepository scorestatusRepository;

    private final ScorestatusSearchRepository scorestatusSearchRepository;

    public ScorestatusResource(ScorestatusRepository scorestatusRepository, ScorestatusSearchRepository scorestatusSearchRepository) {
        this.scorestatusRepository = scorestatusRepository;
        this.scorestatusSearchRepository = scorestatusSearchRepository;
    }

    /**
     * POST  /scorestatuses : Create a new scorestatus.
     *
     * @param scorestatus the scorestatus to create
     * @return the ResponseEntity with status 201 (Created) and with body the new scorestatus, or with status 400 (Bad Request) if the scorestatus has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/scorestatuses")
    @Timed
    public ResponseEntity<Scorestatus> createScorestatus(@Valid @RequestBody Scorestatus scorestatus) throws URISyntaxException {
        log.debug("REST request to save Scorestatus : {}", scorestatus);
        if (scorestatus.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new scorestatus cannot already have an ID")).body(null);
        }
        Scorestatus result = scorestatusRepository.save(scorestatus);
        scorestatusSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/scorestatuses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /scorestatuses : Updates an existing scorestatus.
     *
     * @param scorestatus the scorestatus to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated scorestatus,
     * or with status 400 (Bad Request) if the scorestatus is not valid,
     * or with status 500 (Internal Server Error) if the scorestatus couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/scorestatuses")
    @Timed
    public ResponseEntity<Scorestatus> updateScorestatus(@Valid @RequestBody Scorestatus scorestatus) throws URISyntaxException {
        log.debug("REST request to update Scorestatus : {}", scorestatus);
        if (scorestatus.getId() == null) {
            return createScorestatus(scorestatus);
        }
        Scorestatus result = scorestatusRepository.save(scorestatus);
        scorestatusSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, scorestatus.getId().toString()))
            .body(result);
    }

    /**
     * GET  /scorestatuses : get all the scorestatuses.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of scorestatuses in body
     */
    @GetMapping("/scorestatuses")
    @Timed
    public ResponseEntity<List<Scorestatus>> getAllScorestatuses(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Scorestatuses");
        Page<Scorestatus> page = scorestatusRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/scorestatuses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /scorestatuses/:id : get the "id" scorestatus.
     *
     * @param id the id of the scorestatus to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the scorestatus, or with status 404 (Not Found)
     */
    @GetMapping("/scorestatuses/{id}")
    @Timed
    public ResponseEntity<Scorestatus> getScorestatus(@PathVariable Long id) {
        log.debug("REST request to get Scorestatus : {}", id);
        Scorestatus scorestatus = scorestatusRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(scorestatus));
    }

    /**
     * DELETE  /scorestatuses/:id : delete the "id" scorestatus.
     *
     * @param id the id of the scorestatus to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/scorestatuses/{id}")
    @Timed
    public ResponseEntity<Void> deleteScorestatus(@PathVariable Long id) {
        log.debug("REST request to delete Scorestatus : {}", id);
        scorestatusRepository.delete(id);
        scorestatusSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/scorestatuses?query=:query : search for the scorestatus corresponding
     * to the query.
     *
     * @param query the query of the scorestatus search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/scorestatuses")
    @Timed
    public ResponseEntity<List<Scorestatus>> searchScorestatuses(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Scorestatuses for query {}", query);
        Page<Scorestatus> page = scorestatusSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/scorestatuses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
