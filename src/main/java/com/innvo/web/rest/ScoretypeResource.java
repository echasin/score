package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Scoretype;

import com.innvo.repository.ScoretypeRepository;
import com.innvo.repository.search.ScoretypeSearchRepository;
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
 * REST controller for managing Scoretype.
 */
@RestController
@RequestMapping("/api")
public class ScoretypeResource {

    private final Logger log = LoggerFactory.getLogger(ScoretypeResource.class);

    private static final String ENTITY_NAME = "scoretype";

    private final ScoretypeRepository scoretypeRepository;

    private final ScoretypeSearchRepository scoretypeSearchRepository;

    public ScoretypeResource(ScoretypeRepository scoretypeRepository, ScoretypeSearchRepository scoretypeSearchRepository) {
        this.scoretypeRepository = scoretypeRepository;
        this.scoretypeSearchRepository = scoretypeSearchRepository;
    }

    /**
     * POST  /scoretypes : Create a new scoretype.
     *
     * @param scoretype the scoretype to create
     * @return the ResponseEntity with status 201 (Created) and with body the new scoretype, or with status 400 (Bad Request) if the scoretype has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/scoretypes")
    @Timed
    public ResponseEntity<Scoretype> createScoretype(@Valid @RequestBody Scoretype scoretype) throws URISyntaxException {
        log.debug("REST request to save Scoretype : {}", scoretype);
        if (scoretype.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new scoretype cannot already have an ID")).body(null);
        }
        Scoretype result = scoretypeRepository.save(scoretype);
        scoretypeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/scoretypes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /scoretypes : Updates an existing scoretype.
     *
     * @param scoretype the scoretype to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated scoretype,
     * or with status 400 (Bad Request) if the scoretype is not valid,
     * or with status 500 (Internal Server Error) if the scoretype couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/scoretypes")
    @Timed
    public ResponseEntity<Scoretype> updateScoretype(@Valid @RequestBody Scoretype scoretype) throws URISyntaxException {
        log.debug("REST request to update Scoretype : {}", scoretype);
        if (scoretype.getId() == null) {
            return createScoretype(scoretype);
        }
        Scoretype result = scoretypeRepository.save(scoretype);
        scoretypeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, scoretype.getId().toString()))
            .body(result);
    }

    /**
     * GET  /scoretypes : get all the scoretypes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of scoretypes in body
     */
    @GetMapping("/scoretypes")
    @Timed
    public ResponseEntity<List<Scoretype>> getAllScoretypes(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Scoretypes");
        Page<Scoretype> page = scoretypeRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/scoretypes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /scoretypes/:id : get the "id" scoretype.
     *
     * @param id the id of the scoretype to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the scoretype, or with status 404 (Not Found)
     */
    @GetMapping("/scoretypes/{id}")
    @Timed
    public ResponseEntity<Scoretype> getScoretype(@PathVariable Long id) {
        log.debug("REST request to get Scoretype : {}", id);
        Scoretype scoretype = scoretypeRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(scoretype));
    }

    /**
     * DELETE  /scoretypes/:id : delete the "id" scoretype.
     *
     * @param id the id of the scoretype to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/scoretypes/{id}")
    @Timed
    public ResponseEntity<Void> deleteScoretype(@PathVariable Long id) {
        log.debug("REST request to delete Scoretype : {}", id);
        scoretypeRepository.delete(id);
        scoretypeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/scoretypes?query=:query : search for the scoretype corresponding
     * to the query.
     *
     * @param query the query of the scoretype search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/scoretypes")
    @Timed
    public ResponseEntity<List<Scoretype>> searchScoretypes(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Scoretypes for query {}", query);
        Page<Scoretype> page = scoretypeSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/scoretypes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
