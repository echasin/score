package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Scorecategory;

import com.innvo.repository.ScorecategoryRepository;
import com.innvo.repository.search.ScorecategorySearchRepository;
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
 * REST controller for managing Scorecategory.
 */
@RestController
@RequestMapping("/api")
public class ScorecategoryResource {

    private final Logger log = LoggerFactory.getLogger(ScorecategoryResource.class);

    private static final String ENTITY_NAME = "scorecategory";

    private final ScorecategoryRepository scorecategoryRepository;

    private final ScorecategorySearchRepository scorecategorySearchRepository;

    public ScorecategoryResource(ScorecategoryRepository scorecategoryRepository, ScorecategorySearchRepository scorecategorySearchRepository) {
        this.scorecategoryRepository = scorecategoryRepository;
        this.scorecategorySearchRepository = scorecategorySearchRepository;
    }

    /**
     * POST  /scorecategories : Create a new scorecategory.
     *
     * @param scorecategory the scorecategory to create
     * @return the ResponseEntity with status 201 (Created) and with body the new scorecategory, or with status 400 (Bad Request) if the scorecategory has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/scorecategories")
    @Timed
    public ResponseEntity<Scorecategory> createScorecategory(@Valid @RequestBody Scorecategory scorecategory) throws URISyntaxException {
        log.debug("REST request to save Scorecategory : {}", scorecategory);
        if (scorecategory.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new scorecategory cannot already have an ID")).body(null);
        }
        Scorecategory result = scorecategoryRepository.save(scorecategory);
        scorecategorySearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/scorecategories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /scorecategories : Updates an existing scorecategory.
     *
     * @param scorecategory the scorecategory to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated scorecategory,
     * or with status 400 (Bad Request) if the scorecategory is not valid,
     * or with status 500 (Internal Server Error) if the scorecategory couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/scorecategories")
    @Timed
    public ResponseEntity<Scorecategory> updateScorecategory(@Valid @RequestBody Scorecategory scorecategory) throws URISyntaxException {
        log.debug("REST request to update Scorecategory : {}", scorecategory);
        if (scorecategory.getId() == null) {
            return createScorecategory(scorecategory);
        }
        Scorecategory result = scorecategoryRepository.save(scorecategory);
        scorecategorySearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, scorecategory.getId().toString()))
            .body(result);
    }

    /**
     * GET  /scorecategories : get all the scorecategories.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of scorecategories in body
     */
    @GetMapping("/scorecategories")
    @Timed
    public ResponseEntity<List<Scorecategory>> getAllScorecategories(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Scorecategories");
        Page<Scorecategory> page = scorecategoryRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/scorecategories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /scorecategories/:id : get the "id" scorecategory.
     *
     * @param id the id of the scorecategory to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the scorecategory, or with status 404 (Not Found)
     */
    @GetMapping("/scorecategories/{id}")
    @Timed
    public ResponseEntity<Scorecategory> getScorecategory(@PathVariable Long id) {
        log.debug("REST request to get Scorecategory : {}", id);
        Scorecategory scorecategory = scorecategoryRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(scorecategory));
    }

    /**
     * DELETE  /scorecategories/:id : delete the "id" scorecategory.
     *
     * @param id the id of the scorecategory to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/scorecategories/{id}")
    @Timed
    public ResponseEntity<Void> deleteScorecategory(@PathVariable Long id) {
        log.debug("REST request to delete Scorecategory : {}", id);
        scorecategoryRepository.delete(id);
        scorecategorySearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/scorecategories?query=:query : search for the scorecategory corresponding
     * to the query.
     *
     * @param query the query of the scorecategory search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/scorecategories")
    @Timed
    public ResponseEntity<List<Scorecategory>> searchScorecategories(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Scorecategories for query {}", query);
        Page<Scorecategory> page = scorecategorySearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/scorecategories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
