package com.innvo.web.rest;

import com.innvo.ScoreApp;

import com.innvo.domain.Scorerecordtype;
import com.innvo.repository.ScorerecordtypeRepository;
import com.innvo.repository.search.ScorerecordtypeSearchRepository;
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
 * Test class for the ScorerecordtypeResource REST controller.
 *
 * @see ScorerecordtypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScoreApp.class)
public class ScorerecordtypeResourceIntTest {

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
    private ScorerecordtypeRepository scorerecordtypeRepository;

    @Autowired
    private ScorerecordtypeSearchRepository scorerecordtypeSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restScorerecordtypeMockMvc;

    private Scorerecordtype scorerecordtype;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ScorerecordtypeResource scorerecordtypeResource = new ScorerecordtypeResource(scorerecordtypeRepository, scorerecordtypeSearchRepository);
        this.restScorerecordtypeMockMvc = MockMvcBuilders.standaloneSetup(scorerecordtypeResource)
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
    public static Scorerecordtype createEntity(EntityManager em) {
        Scorerecordtype scorerecordtype = new Scorerecordtype()
            .name(DEFAULT_NAME)
            .nameshort(DEFAULT_NAMESHORT)
            .description(DEFAULT_DESCRIPTION)
            .lastmodifiedby(DEFAULT_LASTMODIFIEDBY)
            .lastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME)
            .domain(DEFAULT_DOMAIN);
        return scorerecordtype;
    }

    @Before
    public void initTest() {
        scorerecordtypeSearchRepository.deleteAll();
        scorerecordtype = createEntity(em);
    }

    @Test
    @Transactional
    public void createScorerecordtype() throws Exception {
        int databaseSizeBeforeCreate = scorerecordtypeRepository.findAll().size();

        // Create the Scorerecordtype
        restScorerecordtypeMockMvc.perform(post("/api/scorerecordtypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorerecordtype)))
            .andExpect(status().isCreated());

        // Validate the Scorerecordtype in the database
        List<Scorerecordtype> scorerecordtypeList = scorerecordtypeRepository.findAll();
        assertThat(scorerecordtypeList).hasSize(databaseSizeBeforeCreate + 1);
        Scorerecordtype testScorerecordtype = scorerecordtypeList.get(scorerecordtypeList.size() - 1);
        assertThat(testScorerecordtype.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testScorerecordtype.getNameshort()).isEqualTo(DEFAULT_NAMESHORT);
        assertThat(testScorerecordtype.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testScorerecordtype.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testScorerecordtype.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testScorerecordtype.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Scorerecordtype in Elasticsearch
        Scorerecordtype scorerecordtypeEs = scorerecordtypeSearchRepository.findOne(testScorerecordtype.getId());
        assertThat(scorerecordtypeEs).isEqualToComparingFieldByField(testScorerecordtype);
    }

    @Test
    @Transactional
    public void createScorerecordtypeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = scorerecordtypeRepository.findAll().size();

        // Create the Scorerecordtype with an existing ID
        scorerecordtype.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restScorerecordtypeMockMvc.perform(post("/api/scorerecordtypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorerecordtype)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Scorerecordtype> scorerecordtypeList = scorerecordtypeRepository.findAll();
        assertThat(scorerecordtypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = scorerecordtypeRepository.findAll().size();
        // set the field null
        scorerecordtype.setName(null);

        // Create the Scorerecordtype, which fails.

        restScorerecordtypeMockMvc.perform(post("/api/scorerecordtypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorerecordtype)))
            .andExpect(status().isBadRequest());

        List<Scorerecordtype> scorerecordtypeList = scorerecordtypeRepository.findAll();
        assertThat(scorerecordtypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameshortIsRequired() throws Exception {
        int databaseSizeBeforeTest = scorerecordtypeRepository.findAll().size();
        // set the field null
        scorerecordtype.setNameshort(null);

        // Create the Scorerecordtype, which fails.

        restScorerecordtypeMockMvc.perform(post("/api/scorerecordtypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorerecordtype)))
            .andExpect(status().isBadRequest());

        List<Scorerecordtype> scorerecordtypeList = scorerecordtypeRepository.findAll();
        assertThat(scorerecordtypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = scorerecordtypeRepository.findAll().size();
        // set the field null
        scorerecordtype.setLastmodifiedby(null);

        // Create the Scorerecordtype, which fails.

        restScorerecordtypeMockMvc.perform(post("/api/scorerecordtypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorerecordtype)))
            .andExpect(status().isBadRequest());

        List<Scorerecordtype> scorerecordtypeList = scorerecordtypeRepository.findAll();
        assertThat(scorerecordtypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = scorerecordtypeRepository.findAll().size();
        // set the field null
        scorerecordtype.setLastmodifieddatetime(null);

        // Create the Scorerecordtype, which fails.

        restScorerecordtypeMockMvc.perform(post("/api/scorerecordtypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorerecordtype)))
            .andExpect(status().isBadRequest());

        List<Scorerecordtype> scorerecordtypeList = scorerecordtypeRepository.findAll();
        assertThat(scorerecordtypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = scorerecordtypeRepository.findAll().size();
        // set the field null
        scorerecordtype.setDomain(null);

        // Create the Scorerecordtype, which fails.

        restScorerecordtypeMockMvc.perform(post("/api/scorerecordtypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorerecordtype)))
            .andExpect(status().isBadRequest());

        List<Scorerecordtype> scorerecordtypeList = scorerecordtypeRepository.findAll();
        assertThat(scorerecordtypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllScorerecordtypes() throws Exception {
        // Initialize the database
        scorerecordtypeRepository.saveAndFlush(scorerecordtype);

        // Get all the scorerecordtypeList
        restScorerecordtypeMockMvc.perform(get("/api/scorerecordtypes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scorerecordtype.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(sameInstant(DEFAULT_LASTMODIFIEDDATETIME))))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getScorerecordtype() throws Exception {
        // Initialize the database
        scorerecordtypeRepository.saveAndFlush(scorerecordtype);

        // Get the scorerecordtype
        restScorerecordtypeMockMvc.perform(get("/api/scorerecordtypes/{id}", scorerecordtype.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(scorerecordtype.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.nameshort").value(DEFAULT_NAMESHORT.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(sameInstant(DEFAULT_LASTMODIFIEDDATETIME)))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingScorerecordtype() throws Exception {
        // Get the scorerecordtype
        restScorerecordtypeMockMvc.perform(get("/api/scorerecordtypes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateScorerecordtype() throws Exception {
        // Initialize the database
        scorerecordtypeRepository.saveAndFlush(scorerecordtype);
        scorerecordtypeSearchRepository.save(scorerecordtype);
        int databaseSizeBeforeUpdate = scorerecordtypeRepository.findAll().size();

        // Update the scorerecordtype
        Scorerecordtype updatedScorerecordtype = scorerecordtypeRepository.findOne(scorerecordtype.getId());
        updatedScorerecordtype
            .name(UPDATED_NAME)
            .nameshort(UPDATED_NAMESHORT)
            .description(UPDATED_DESCRIPTION)
            .lastmodifiedby(UPDATED_LASTMODIFIEDBY)
            .lastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME)
            .domain(UPDATED_DOMAIN);

        restScorerecordtypeMockMvc.perform(put("/api/scorerecordtypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedScorerecordtype)))
            .andExpect(status().isOk());

        // Validate the Scorerecordtype in the database
        List<Scorerecordtype> scorerecordtypeList = scorerecordtypeRepository.findAll();
        assertThat(scorerecordtypeList).hasSize(databaseSizeBeforeUpdate);
        Scorerecordtype testScorerecordtype = scorerecordtypeList.get(scorerecordtypeList.size() - 1);
        assertThat(testScorerecordtype.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testScorerecordtype.getNameshort()).isEqualTo(UPDATED_NAMESHORT);
        assertThat(testScorerecordtype.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testScorerecordtype.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testScorerecordtype.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testScorerecordtype.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Scorerecordtype in Elasticsearch
        Scorerecordtype scorerecordtypeEs = scorerecordtypeSearchRepository.findOne(testScorerecordtype.getId());
        assertThat(scorerecordtypeEs).isEqualToComparingFieldByField(testScorerecordtype);
    }

    @Test
    @Transactional
    public void updateNonExistingScorerecordtype() throws Exception {
        int databaseSizeBeforeUpdate = scorerecordtypeRepository.findAll().size();

        // Create the Scorerecordtype

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restScorerecordtypeMockMvc.perform(put("/api/scorerecordtypes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scorerecordtype)))
            .andExpect(status().isCreated());

        // Validate the Scorerecordtype in the database
        List<Scorerecordtype> scorerecordtypeList = scorerecordtypeRepository.findAll();
        assertThat(scorerecordtypeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteScorerecordtype() throws Exception {
        // Initialize the database
        scorerecordtypeRepository.saveAndFlush(scorerecordtype);
        scorerecordtypeSearchRepository.save(scorerecordtype);
        int databaseSizeBeforeDelete = scorerecordtypeRepository.findAll().size();

        // Get the scorerecordtype
        restScorerecordtypeMockMvc.perform(delete("/api/scorerecordtypes/{id}", scorerecordtype.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean scorerecordtypeExistsInEs = scorerecordtypeSearchRepository.exists(scorerecordtype.getId());
        assertThat(scorerecordtypeExistsInEs).isFalse();

        // Validate the database is empty
        List<Scorerecordtype> scorerecordtypeList = scorerecordtypeRepository.findAll();
        assertThat(scorerecordtypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchScorerecordtype() throws Exception {
        // Initialize the database
        scorerecordtypeRepository.saveAndFlush(scorerecordtype);
        scorerecordtypeSearchRepository.save(scorerecordtype);

        // Search the scorerecordtype
        restScorerecordtypeMockMvc.perform(get("/api/_search/scorerecordtypes?query=id:" + scorerecordtype.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scorerecordtype.getId().intValue())))
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
        TestUtil.equalsVerifier(Scorerecordtype.class);
        Scorerecordtype scorerecordtype1 = new Scorerecordtype();
        scorerecordtype1.setId(1L);
        Scorerecordtype scorerecordtype2 = new Scorerecordtype();
        scorerecordtype2.setId(scorerecordtype1.getId());
        assertThat(scorerecordtype1).isEqualTo(scorerecordtype2);
        scorerecordtype2.setId(2L);
        assertThat(scorerecordtype1).isNotEqualTo(scorerecordtype2);
        scorerecordtype1.setId(null);
        assertThat(scorerecordtype1).isNotEqualTo(scorerecordtype2);
    }
}
