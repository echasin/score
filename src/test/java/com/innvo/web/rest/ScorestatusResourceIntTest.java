package com.innvo.web.rest;

import com.innvo.ScoreApp;

import com.innvo.domain.Scorestatus;
import com.innvo.repository.ScorestatusRepository;
import com.innvo.repository.search.ScorestatusSearchRepository;
import com.innvo.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static com.innvo.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ScorestatusResource REST controller.
 *
 * @see ScorestatusResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScoreApp.class)
public class ScorestatusResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_NAMESHORT = "AAAAAAAAAA";
    private static final String UPDATED_NAMESHORT = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_LASTMODIFIEDBY = "AAAAAAAAAA";
    private static final String UPDATED_LASTMODIFIEDBY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_LASTMODIFIEDDATETIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LASTMODIFIEDDATETIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_DOMAIN = "AAAAAAAAAA";
    private static final String UPDATED_DOMAIN = "BBBBBBBBBB";

    @Autowired
    private ScorestatusRepository scorestatusRepository;

    @Autowired
    private ScorestatusSearchRepository scorestatusSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restScorestatusMockMvc;

    private Scorestatus scorestatus;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ScorestatusResource scorestatusResource = new ScorestatusResource(scorestatusRepository, scorestatusSearchRepository);
        this.restScorestatusMockMvc = MockMvcBuilders.standaloneSetup(scorestatusResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Scorestatus createEntity(EntityManager em) {
        Scorestatus scorestatus = new Scorestatus()
            .name(DEFAULT_NAME)
            .nameshort(DEFAULT_NAMESHORT)
            .description(DEFAULT_DESCRIPTION)
            .lastmodifiedby(DEFAULT_LASTMODIFIEDBY)
            .lastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME)
            .domain(DEFAULT_DOMAIN);
        return scorestatus;
    }

    @Before
    public void initTest() {
        scorestatusSearchRepository.deleteAll();
        scorestatus = createEntity(em);
    }

    @Test
    @Transactional
    public void createScorestatus() throws Exception {
        int databaseSizeBeforeCreate = scorestatusRepository.findAll().size();

        // Create the Scorestatus
        restScorestatusMockMvc.perform(post("/api/scorestatuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorestatus)))
            .andExpect(status().isCreated());

        // Validate the Scorestatus in the database
        List<Scorestatus> scorestatusList = scorestatusRepository.findAll();
        assertThat(scorestatusList).hasSize(databaseSizeBeforeCreate + 1);
        Scorestatus testScorestatus = scorestatusList.get(scorestatusList.size() - 1);
        assertThat(testScorestatus.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testScorestatus.getNameshort()).isEqualTo(DEFAULT_NAMESHORT);
        assertThat(testScorestatus.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testScorestatus.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testScorestatus.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testScorestatus.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Scorestatus in Elasticsearch
        Scorestatus scorestatusEs = scorestatusSearchRepository.findOne(testScorestatus.getId());
        assertThat(scorestatusEs).isEqualToComparingFieldByField(testScorestatus);
    }

    @Test
    @Transactional
    public void createScorestatusWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = scorestatusRepository.findAll().size();

        // Create the Scorestatus with an existing ID
        scorestatus.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restScorestatusMockMvc.perform(post("/api/scorestatuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorestatus)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Scorestatus> scorestatusList = scorestatusRepository.findAll();
        assertThat(scorestatusList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = scorestatusRepository.findAll().size();
        // set the field null
        scorestatus.setName(null);

        // Create the Scorestatus, which fails.

        restScorestatusMockMvc.perform(post("/api/scorestatuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorestatus)))
            .andExpect(status().isBadRequest());

        List<Scorestatus> scorestatusList = scorestatusRepository.findAll();
        assertThat(scorestatusList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameshortIsRequired() throws Exception {
        int databaseSizeBeforeTest = scorestatusRepository.findAll().size();
        // set the field null
        scorestatus.setNameshort(null);

        // Create the Scorestatus, which fails.

        restScorestatusMockMvc.perform(post("/api/scorestatuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorestatus)))
            .andExpect(status().isBadRequest());

        List<Scorestatus> scorestatusList = scorestatusRepository.findAll();
        assertThat(scorestatusList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = scorestatusRepository.findAll().size();
        // set the field null
        scorestatus.setLastmodifiedby(null);

        // Create the Scorestatus, which fails.

        restScorestatusMockMvc.perform(post("/api/scorestatuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorestatus)))
            .andExpect(status().isBadRequest());

        List<Scorestatus> scorestatusList = scorestatusRepository.findAll();
        assertThat(scorestatusList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = scorestatusRepository.findAll().size();
        // set the field null
        scorestatus.setLastmodifieddatetime(null);

        // Create the Scorestatus, which fails.

        restScorestatusMockMvc.perform(post("/api/scorestatuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorestatus)))
            .andExpect(status().isBadRequest());

        List<Scorestatus> scorestatusList = scorestatusRepository.findAll();
        assertThat(scorestatusList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = scorestatusRepository.findAll().size();
        // set the field null
        scorestatus.setDomain(null);

        // Create the Scorestatus, which fails.

        restScorestatusMockMvc.perform(post("/api/scorestatuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorestatus)))
            .andExpect(status().isBadRequest());

        List<Scorestatus> scorestatusList = scorestatusRepository.findAll();
        assertThat(scorestatusList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllScorestatuses() throws Exception {
        // Initialize the database
        scorestatusRepository.saveAndFlush(scorestatus);

        // Get all the scorestatusList
        restScorestatusMockMvc.perform(get("/api/scorestatuses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scorestatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(sameInstant(DEFAULT_LASTMODIFIEDDATETIME))))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getScorestatus() throws Exception {
        // Initialize the database
        scorestatusRepository.saveAndFlush(scorestatus);

        // Get the scorestatus
        restScorestatusMockMvc.perform(get("/api/scorestatuses/{id}", scorestatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(scorestatus.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.nameshort").value(DEFAULT_NAMESHORT.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(sameInstant(DEFAULT_LASTMODIFIEDDATETIME)))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingScorestatus() throws Exception {
        // Get the scorestatus
        restScorestatusMockMvc.perform(get("/api/scorestatuses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateScorestatus() throws Exception {
        // Initialize the database
        scorestatusRepository.saveAndFlush(scorestatus);
        scorestatusSearchRepository.save(scorestatus);
        int databaseSizeBeforeUpdate = scorestatusRepository.findAll().size();

        // Update the scorestatus
        Scorestatus updatedScorestatus = scorestatusRepository.findOne(scorestatus.getId());
        updatedScorestatus
            .name(UPDATED_NAME)
            .nameshort(UPDATED_NAMESHORT)
            .description(UPDATED_DESCRIPTION)
            .lastmodifiedby(UPDATED_LASTMODIFIEDBY)
            .lastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME)
            .domain(UPDATED_DOMAIN);

        restScorestatusMockMvc.perform(put("/api/scorestatuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedScorestatus)))
            .andExpect(status().isOk());

        // Validate the Scorestatus in the database
        List<Scorestatus> scorestatusList = scorestatusRepository.findAll();
        assertThat(scorestatusList).hasSize(databaseSizeBeforeUpdate);
        Scorestatus testScorestatus = scorestatusList.get(scorestatusList.size() - 1);
        assertThat(testScorestatus.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testScorestatus.getNameshort()).isEqualTo(UPDATED_NAMESHORT);
        assertThat(testScorestatus.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testScorestatus.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testScorestatus.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testScorestatus.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Scorestatus in Elasticsearch
        Scorestatus scorestatusEs = scorestatusSearchRepository.findOne(testScorestatus.getId());
        assertThat(scorestatusEs).isEqualToComparingFieldByField(testScorestatus);
    }

    @Test
    @Transactional
    public void updateNonExistingScorestatus() throws Exception {
        int databaseSizeBeforeUpdate = scorestatusRepository.findAll().size();

        // Create the Scorestatus

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restScorestatusMockMvc.perform(put("/api/scorestatuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorestatus)))
            .andExpect(status().isCreated());

        // Validate the Scorestatus in the database
        List<Scorestatus> scorestatusList = scorestatusRepository.findAll();
        assertThat(scorestatusList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteScorestatus() throws Exception {
        // Initialize the database
        scorestatusRepository.saveAndFlush(scorestatus);
        scorestatusSearchRepository.save(scorestatus);
        int databaseSizeBeforeDelete = scorestatusRepository.findAll().size();

        // Get the scorestatus
        restScorestatusMockMvc.perform(delete("/api/scorestatuses/{id}", scorestatus.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean scorestatusExistsInEs = scorestatusSearchRepository.exists(scorestatus.getId());
        assertThat(scorestatusExistsInEs).isFalse();

        // Validate the database is empty
        List<Scorestatus> scorestatusList = scorestatusRepository.findAll();
        assertThat(scorestatusList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchScorestatus() throws Exception {
        // Initialize the database
        scorestatusRepository.saveAndFlush(scorestatus);
        scorestatusSearchRepository.save(scorestatus);

        // Search the scorestatus
        restScorestatusMockMvc.perform(get("/api/_search/scorestatuses?query=id:" + scorestatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scorestatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(sameInstant(DEFAULT_LASTMODIFIEDDATETIME))))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Scorestatus.class);
        Scorestatus scorestatus1 = new Scorestatus();
        scorestatus1.setId(1L);
        Scorestatus scorestatus2 = new Scorestatus();
        scorestatus2.setId(scorestatus1.getId());
        assertThat(scorestatus1).isEqualTo(scorestatus2);
        scorestatus2.setId(2L);
        assertThat(scorestatus1).isNotEqualTo(scorestatus2);
        scorestatus1.setId(null);
        assertThat(scorestatus1).isNotEqualTo(scorestatus2);
    }
}
