package com.innvo.web.rest;

import com.innvo.ScoreApp;

import com.innvo.domain.Scoretype;
import com.innvo.repository.ScoretypeRepository;
import com.innvo.repository.search.ScoretypeSearchRepository;
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
 * Test class for the ScoretypeResource REST controller.
 *
 * @see ScoretypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScoreApp.class)
public class ScoretypeResourceIntTest {

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
    private ScoretypeRepository scoretypeRepository;

    @Autowired
    private ScoretypeSearchRepository scoretypeSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restScoretypeMockMvc;

    private Scoretype scoretype;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ScoretypeResource scoretypeResource = new ScoretypeResource(scoretypeRepository, scoretypeSearchRepository);
        this.restScoretypeMockMvc = MockMvcBuilders.standaloneSetup(scoretypeResource)
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
    public static Scoretype createEntity(EntityManager em) {
        Scoretype scoretype = new Scoretype()
            .name(DEFAULT_NAME)
            .nameshort(DEFAULT_NAMESHORT)
            .description(DEFAULT_DESCRIPTION)
            .lastmodifiedby(DEFAULT_LASTMODIFIEDBY)
            .lastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME)
            .domain(DEFAULT_DOMAIN);
        return scoretype;
    }

    @Before
    public void initTest() {
        scoretypeSearchRepository.deleteAll();
        scoretype = createEntity(em);
    }

    @Test
    @Transactional
    public void createScoretype() throws Exception {
        int databaseSizeBeforeCreate = scoretypeRepository.findAll().size();

        // Create the Scoretype
        restScoretypeMockMvc.perform(post("/api/scoretypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scoretype)))
            .andExpect(status().isCreated());

        // Validate the Scoretype in the database
        List<Scoretype> scoretypeList = scoretypeRepository.findAll();
        assertThat(scoretypeList).hasSize(databaseSizeBeforeCreate + 1);
        Scoretype testScoretype = scoretypeList.get(scoretypeList.size() - 1);
        assertThat(testScoretype.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testScoretype.getNameshort()).isEqualTo(DEFAULT_NAMESHORT);
        assertThat(testScoretype.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testScoretype.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testScoretype.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testScoretype.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Scoretype in Elasticsearch
        Scoretype scoretypeEs = scoretypeSearchRepository.findOne(testScoretype.getId());
        assertThat(scoretypeEs).isEqualToComparingFieldByField(testScoretype);
    }

    @Test
    @Transactional
    public void createScoretypeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = scoretypeRepository.findAll().size();

        // Create the Scoretype with an existing ID
        scoretype.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restScoretypeMockMvc.perform(post("/api/scoretypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scoretype)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Scoretype> scoretypeList = scoretypeRepository.findAll();
        assertThat(scoretypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = scoretypeRepository.findAll().size();
        // set the field null
        scoretype.setName(null);

        // Create the Scoretype, which fails.

        restScoretypeMockMvc.perform(post("/api/scoretypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scoretype)))
            .andExpect(status().isBadRequest());

        List<Scoretype> scoretypeList = scoretypeRepository.findAll();
        assertThat(scoretypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameshortIsRequired() throws Exception {
        int databaseSizeBeforeTest = scoretypeRepository.findAll().size();
        // set the field null
        scoretype.setNameshort(null);

        // Create the Scoretype, which fails.

        restScoretypeMockMvc.perform(post("/api/scoretypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scoretype)))
            .andExpect(status().isBadRequest());

        List<Scoretype> scoretypeList = scoretypeRepository.findAll();
        assertThat(scoretypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = scoretypeRepository.findAll().size();
        // set the field null
        scoretype.setLastmodifiedby(null);

        // Create the Scoretype, which fails.

        restScoretypeMockMvc.perform(post("/api/scoretypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scoretype)))
            .andExpect(status().isBadRequest());

        List<Scoretype> scoretypeList = scoretypeRepository.findAll();
        assertThat(scoretypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = scoretypeRepository.findAll().size();
        // set the field null
        scoretype.setLastmodifieddatetime(null);

        // Create the Scoretype, which fails.

        restScoretypeMockMvc.perform(post("/api/scoretypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scoretype)))
            .andExpect(status().isBadRequest());

        List<Scoretype> scoretypeList = scoretypeRepository.findAll();
        assertThat(scoretypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = scoretypeRepository.findAll().size();
        // set the field null
        scoretype.setDomain(null);

        // Create the Scoretype, which fails.

        restScoretypeMockMvc.perform(post("/api/scoretypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scoretype)))
            .andExpect(status().isBadRequest());

        List<Scoretype> scoretypeList = scoretypeRepository.findAll();
        assertThat(scoretypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllScoretypes() throws Exception {
        // Initialize the database
        scoretypeRepository.saveAndFlush(scoretype);

        // Get all the scoretypeList
        restScoretypeMockMvc.perform(get("/api/scoretypes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scoretype.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(sameInstant(DEFAULT_LASTMODIFIEDDATETIME))))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getScoretype() throws Exception {
        // Initialize the database
        scoretypeRepository.saveAndFlush(scoretype);

        // Get the scoretype
        restScoretypeMockMvc.perform(get("/api/scoretypes/{id}", scoretype.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(scoretype.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.nameshort").value(DEFAULT_NAMESHORT.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(sameInstant(DEFAULT_LASTMODIFIEDDATETIME)))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingScoretype() throws Exception {
        // Get the scoretype
        restScoretypeMockMvc.perform(get("/api/scoretypes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateScoretype() throws Exception {
        // Initialize the database
        scoretypeRepository.saveAndFlush(scoretype);
        scoretypeSearchRepository.save(scoretype);
        int databaseSizeBeforeUpdate = scoretypeRepository.findAll().size();

        // Update the scoretype
        Scoretype updatedScoretype = scoretypeRepository.findOne(scoretype.getId());
        updatedScoretype
            .name(UPDATED_NAME)
            .nameshort(UPDATED_NAMESHORT)
            .description(UPDATED_DESCRIPTION)
            .lastmodifiedby(UPDATED_LASTMODIFIEDBY)
            .lastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME)
            .domain(UPDATED_DOMAIN);

        restScoretypeMockMvc.perform(put("/api/scoretypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedScoretype)))
            .andExpect(status().isOk());

        // Validate the Scoretype in the database
        List<Scoretype> scoretypeList = scoretypeRepository.findAll();
        assertThat(scoretypeList).hasSize(databaseSizeBeforeUpdate);
        Scoretype testScoretype = scoretypeList.get(scoretypeList.size() - 1);
        assertThat(testScoretype.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testScoretype.getNameshort()).isEqualTo(UPDATED_NAMESHORT);
        assertThat(testScoretype.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testScoretype.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testScoretype.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testScoretype.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Scoretype in Elasticsearch
        Scoretype scoretypeEs = scoretypeSearchRepository.findOne(testScoretype.getId());
        assertThat(scoretypeEs).isEqualToComparingFieldByField(testScoretype);
    }

    @Test
    @Transactional
    public void updateNonExistingScoretype() throws Exception {
        int databaseSizeBeforeUpdate = scoretypeRepository.findAll().size();

        // Create the Scoretype

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restScoretypeMockMvc.perform(put("/api/scoretypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scoretype)))
            .andExpect(status().isCreated());

        // Validate the Scoretype in the database
        List<Scoretype> scoretypeList = scoretypeRepository.findAll();
        assertThat(scoretypeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteScoretype() throws Exception {
        // Initialize the database
        scoretypeRepository.saveAndFlush(scoretype);
        scoretypeSearchRepository.save(scoretype);
        int databaseSizeBeforeDelete = scoretypeRepository.findAll().size();

        // Get the scoretype
        restScoretypeMockMvc.perform(delete("/api/scoretypes/{id}", scoretype.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean scoretypeExistsInEs = scoretypeSearchRepository.exists(scoretype.getId());
        assertThat(scoretypeExistsInEs).isFalse();

        // Validate the database is empty
        List<Scoretype> scoretypeList = scoretypeRepository.findAll();
        assertThat(scoretypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchScoretype() throws Exception {
        // Initialize the database
        scoretypeRepository.saveAndFlush(scoretype);
        scoretypeSearchRepository.save(scoretype);

        // Search the scoretype
        restScoretypeMockMvc.perform(get("/api/_search/scoretypes?query=id:" + scoretype.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scoretype.getId().intValue())))
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
        TestUtil.equalsVerifier(Scoretype.class);
        Scoretype scoretype1 = new Scoretype();
        scoretype1.setId(1L);
        Scoretype scoretype2 = new Scoretype();
        scoretype2.setId(scoretype1.getId());
        assertThat(scoretype1).isEqualTo(scoretype2);
        scoretype2.setId(2L);
        assertThat(scoretype1).isNotEqualTo(scoretype2);
        scoretype1.setId(null);
        assertThat(scoretype1).isNotEqualTo(scoretype2);
    }
}
