package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Scoreclass;

import com.innvo.repository.ScoreclassRepository;
import com.innvo.repository.search.ScoreclassSearchRepository;
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
 * REST controller for managing Scoreclass.
 */
@RestController
@RequestMapping("/api")
public class ScoreclassResource {

    private final Logger log = LoggerFactory.getLogger(ScoreclassResource.class);

    private static final String ENTITY_NAME = "scoreclass";

    private final ScoreclassRepository scoreclassRepository;

    private final ScoreclassSearchRepository scoreclassSearchRepository;

    public ScoreclassResource(ScoreclassRepository scoreclassRepository, ScoreclassSearchRepository scoreclassSearchRepository) {
        this.scoreclassRepository = scoreclassRepository;
        this.scoreclassSearchRepository = scoreclassSearchRepository;
    }

    /**
     * POST  /scoreclasses : Create a new scoreclass.
     *
     * @param scoreclass the scoreclass to create
     * @return the ResponseEntity with status 201 (Created) and with body the new scoreclass, or with status 400 (Bad Request) if the scoreclass has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/scoreclasses")
    @Timed
    public ResponseEntity<Scoreclass> createScoreclass(@Valid @RequestBody Scoreclass scoreclass) throws URISyntaxException {
        log.debug("REST request to save Scoreclass : {}", scoreclass);
        if (scoreclass.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new scoreclass cannot already have an ID")).body(null);
        }
        Scoreclass result = scoreclassRepository.save(scoreclass);
        scoreclassSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/scoreclasses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /scoreclasses : Updates an existing scoreclass.
     *
     * @param scoreclass the scoreclass to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated scoreclass,
     * or with status 400 (Bad Request) if the scoreclass is not valid,
     * or with status 500 (Internal Server Error) if the scoreclass couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/scoreclasses")
    @Timed
    public ResponseEntity<Scoreclass> updateScoreclass(@Valid @RequestBody Scoreclass scoreclass) throws URISyntaxException {
        log.debug("REST request to update Scoreclass : {}", scoreclass);
        if (scoreclass.getId() == null) {
            return createScoreclass(scoreclass);
        }
        Scoreclass result = scoreclassRepository.save(scoreclass);
        scoreclassSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, scoreclass.getId().toString()))
            .body(result);
    }

    /**
     * GET  /scoreclasses : get all the scoreclasses.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of scoreclasses in body
     */
    @GetMapping("/scoreclasses")
    @Timed
    public ResponseEntity<List<Scoreclass>> getAllScoreclasses(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Scoreclasses");
        Page<Scoreclass> page = scoreclassRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/scoreclasses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /scoreclasses/:id : get the "id" scoreclass.
     *
     * @param id the id of the scoreclass to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the scoreclass, or with status 404 (Not Found)
     */
    @GetMapping("/scoreclasses/{id}")
    @Timed
    public ResponseEntity<Scoreclass> getScoreclass(@PathVariable Long id) {
        log.debug("REST request to get Scoreclass : {}", id);
        Scoreclass scoreclass = scoreclassRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(scoreclass));
    }

    /**
     * DELETE  /scoreclasses/:id : delete the "id" scoreclass.
     *
     * @param id the id of the scoreclass to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/scoreclasses/{id}")
    @Timed
    public ResponseEntity<Void> deleteScoreclass(@PathVariable Long id) {
        log.debug("REST request to delete Scoreclass : {}", id);
        scoreclassRepository.delete(id);
        scoreclassSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/scoreclasses?query=:query : search for the scoreclass corresponding
     * to the query.
     *
     * @param query the query of the scoreclass search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/scoreclasses")
    @Timed
    public ResponseEntity<List<Scoreclass>> searchScoreclasses(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Scoreclasses for query {}", query);
        Page<Scoreclass> page = scoreclassSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/scoreclasses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
