package com.innvo.web.rest;

import com.innvo.ScoreApp;

import com.innvo.domain.Scorecategory;
import com.innvo.repository.ScorecategoryRepository;
import com.innvo.repository.search.ScorecategorySearchRepository;
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
 * Test class for the ScorecategoryResource REST controller.
 *
 * @see ScorecategoryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScoreApp.class)
public class ScorecategoryResourceIntTest {

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
    private ScorecategoryRepository scorecategoryRepository;

    @Autowired
    private ScorecategorySearchRepository scorecategorySearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restScorecategoryMockMvc;

    private Scorecategory scorecategory;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ScorecategoryResource scorecategoryResource = new ScorecategoryResource(scorecategoryRepository, scorecategorySearchRepository);
        this.restScorecategoryMockMvc = MockMvcBuilders.standaloneSetup(scorecategoryResource)
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
    public static Scorecategory createEntity(EntityManager em) {
        Scorecategory scorecategory = new Scorecategory()
            .name(DEFAULT_NAME)
            .nameshort(DEFAULT_NAMESHORT)
            .description(DEFAULT_DESCRIPTION)
            .lastmodifiedby(DEFAULT_LASTMODIFIEDBY)
            .lastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME)
            .domain(DEFAULT_DOMAIN);
        return scorecategory;
    }

    @Before
    public void initTest() {
        scorecategorySearchRepository.deleteAll();
        scorecategory = createEntity(em);
    }

    @Test
    @Transactional
    public void createScorecategory() throws Exception {
        int databaseSizeBeforeCreate = scorecategoryRepository.findAll().size();

        // Create the Scorecategory
        restScorecategoryMockMvc.perform(post("/api/scorecategories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorecategory)))
            .andExpect(status().isCreated());

        // Validate the Scorecategory in the database
        List<Scorecategory> scorecategoryList = scorecategoryRepository.findAll();
        assertThat(scorecategoryList).hasSize(databaseSizeBeforeCreate + 1);
        Scorecategory testScorecategory = scorecategoryList.get(scorecategoryList.size() - 1);
        assertThat(testScorecategory.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testScorecategory.getNameshort()).isEqualTo(DEFAULT_NAMESHORT);
        assertThat(testScorecategory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testScorecategory.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testScorecategory.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testScorecategory.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Scorecategory in Elasticsearch
        Scorecategory scorecategoryEs = scorecategorySearchRepository.findOne(testScorecategory.getId());
        assertThat(scorecategoryEs).isEqualToComparingFieldByField(testScorecategory);
    }

    @Test
    @Transactional
    public void createScorecategoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = scorecategoryRepository.findAll().size();

        // Create the Scorecategory with an existing ID
        scorecategory.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restScorecategoryMockMvc.perform(post("/api/scorecategories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorecategory)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Scorecategory> scorecategoryList = scorecategoryRepository.findAll();
        assertThat(scorecategoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = scorecategoryRepository.findAll().size();
        // set the field null
        scorecategory.setName(null);

        // Create the Scorecategory, which fails.

        restScorecategoryMockMvc.perform(post("/api/scorecategories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorecategory)))
            .andExpect(status().isBadRequest());

        List<Scorecategory> scorecategoryList = scorecategoryRepository.findAll();
        assertThat(scorecategoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameshortIsRequired() throws Exception {
        int databaseSizeBeforeTest = scorecategoryRepository.findAll().size();
        // set the field null
        scorecategory.setNameshort(null);

        // Create the Scorecategory, which fails.

        restScorecategoryMockMvc.perform(post("/api/scorecategories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorecategory)))
            .andExpect(status().isBadRequest());

        List<Scorecategory> scorecategoryList = scorecategoryRepository.findAll();
        assertThat(scorecategoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = scorecategoryRepository.findAll().size();
        // set the field null
        scorecategory.setLastmodifiedby(null);

        // Create the Scorecategory, which fails.

        restScorecategoryMockMvc.perform(post("/api/scorecategories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorecategory)))
            .andExpect(status().isBadRequest());

        List<Scorecategory> scorecategoryList = scorecategoryRepository.findAll();
        assertThat(scorecategoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = scorecategoryRepository.findAll().size();
        // set the field null
        scorecategory.setLastmodifieddatetime(null);

        // Create the Scorecategory, which fails.

        restScorecategoryMockMvc.perform(post("/api/scorecategories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorecategory)))
            .andExpect(status().isBadRequest());

        List<Scorecategory> scorecategoryList = scorecategoryRepository.findAll();
        assertThat(scorecategoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = scorecategoryRepository.findAll().size();
        // set the field null
        scorecategory.setDomain(null);

        // Create the Scorecategory, which fails.

        restScorecategoryMockMvc.perform(post("/api/scorecategories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorecategory)))
            .andExpect(status().isBadRequest());

        List<Scorecategory> scorecategoryList = scorecategoryRepository.findAll();
        assertThat(scorecategoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllScorecategories() throws Exception {
        // Initialize the database
        scorecategoryRepository.saveAndFlush(scorecategory);

        // Get all the scorecategoryList
        restScorecategoryMockMvc.perform(get("/api/scorecategories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scorecategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(sameInstant(DEFAULT_LASTMODIFIEDDATETIME))))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getScorecategory() throws Exception {
        // Initialize the database
        scorecategoryRepository.saveAndFlush(scorecategory);

        // Get the scorecategory
        restScorecategoryMockMvc.perform(get("/api/scorecategories/{id}", scorecategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(scorecategory.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.nameshort").value(DEFAULT_NAMESHORT.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(sameInstant(DEFAULT_LASTMODIFIEDDATETIME)))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingScorecategory() throws Exception {
        // Get the scorecategory
        restScorecategoryMockMvc.perform(get("/api/scorecategories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateScorecategory() throws Exception {
        // Initialize the database
        scorecategoryRepository.saveAndFlush(scorecategory);
        scorecategorySearchRepository.save(scorecategory);
        int databaseSizeBeforeUpdate = scorecategoryRepository.findAll().size();

        // Update the scorecategory
        Scorecategory updatedScorecategory = scorecategoryRepository.findOne(scorecategory.getId());
        updatedScorecategory
            .name(UPDATED_NAME)
            .nameshort(UPDATED_NAMESHORT)
            .description(UPDATED_DESCRIPTION)
            .lastmodifiedby(UPDATED_LASTMODIFIEDBY)
            .lastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME)
            .domain(UPDATED_DOMAIN);

        restScorecategoryMockMvc.perform(put("/api/scorecategories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedScorecategory)))
            .andExpect(status().isOk());

        // Validate the Scorecategory in the database
        List<Scorecategory> scorecategoryList = scorecategoryRepository.findAll();
        assertThat(scorecategoryList).hasSize(databaseSizeBeforeUpdate);
        Scorecategory testScorecategory = scorecategoryList.get(scorecategoryList.size() - 1);
        assertThat(testScorecategory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testScorecategory.getNameshort()).isEqualTo(UPDATED_NAMESHORT);
        assertThat(testScorecategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testScorecategory.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testScorecategory.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testScorecategory.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Scorecategory in Elasticsearch
        Scorecategory scorecategoryEs = scorecategorySearchRepository.findOne(testScorecategory.getId());
        assertThat(scorecategoryEs).isEqualToComparingFieldByField(testScorecategory);
    }

    @Test
    @Transactional
    public void updateNonExistingScorecategory() throws Exception {
        int databaseSizeBeforeUpdate = scorecategoryRepository.findAll().size();

        // Create the Scorecategory

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restScorecategoryMockMvc.perform(put("/api/scorecategories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorecategory)))
            .andExpect(status().isCreated());

        // Validate the Scorecategory in the database
        List<Scorecategory> scorecategoryList = scorecategoryRepository.findAll();
        assertThat(scorecategoryList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteScorecategory() throws Exception {
        // Initialize the database
        scorecategoryRepository.saveAndFlush(scorecategory);
        scorecategorySearchRepository.save(scorecategory);
        int databaseSizeBeforeDelete = scorecategoryRepository.findAll().size();

        // Get the scorecategory
        restScorecategoryMockMvc.perform(delete("/api/scorecategories/{id}", scorecategory.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean scorecategoryExistsInEs = scorecategorySearchRepository.exists(scorecategory.getId());
        assertThat(scorecategoryExistsInEs).isFalse();

        // Validate the database is empty
        List<Scorecategory> scorecategoryList = scorecategoryRepository.findAll();
        assertThat(scorecategoryList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchScorecategory() throws Exception {
        // Initialize the database
        scorecategoryRepository.saveAndFlush(scorecategory);
        scorecategorySearchRepository.save(scorecategory);

        // Search the scorecategory
        restScorecategoryMockMvc.perform(get("/api/_search/scorecategories?query=id:" + scorecategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scorecategory.getId().intValue())))
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
        TestUtil.equalsVerifier(Scorecategory.class);
        Scorecategory scorecategory1 = new Scorecategory();
        scorecategory1.setId(1L);
        Scorecategory scorecategory2 = new Scorecategory();
        scorecategory2.setId(scorecategory1.getId());
        assertThat(scorecategory1).isEqualTo(scorecategory2);
        scorecategory2.setId(2L);
        assertThat(scorecategory1).isNotEqualTo(scorecategory2);
        scorecategory1.setId(null);
        assertThat(scorecategory1).isNotEqualTo(scorecategory2);
    }
}
