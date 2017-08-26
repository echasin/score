package com.innvo.web.rest;

import com.innvo.ScoreApp;

import com.innvo.domain.Scoreclass;
import com.innvo.repository.ScoreclassRepository;
import com.innvo.repository.search.ScoreclassSearchRepository;
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
 * Test class for the ScoreclassResource REST controller.
 *
 * @see ScoreclassResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScoreApp.class)
public class ScoreclassResourceIntTest {

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
    private ScoreclassRepository scoreclassRepository;

    @Autowired
    private ScoreclassSearchRepository scoreclassSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restScoreclassMockMvc;

    private Scoreclass scoreclass;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ScoreclassResource scoreclassResource = new ScoreclassResource(scoreclassRepository, scoreclassSearchRepository);
        this.restScoreclassMockMvc = MockMvcBuilders.standaloneSetup(scoreclassResource)
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
    public static Scoreclass createEntity(EntityManager em) {
        Scoreclass scoreclass = new Scoreclass()
            .name(DEFAULT_NAME)
            .nameshort(DEFAULT_NAMESHORT)
            .description(DEFAULT_DESCRIPTION)
            .lastmodifiedby(DEFAULT_LASTMODIFIEDBY)
            .lastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME)
            .domain(DEFAULT_DOMAIN);
        return scoreclass;
    }

    @Before
    public void initTest() {
        scoreclassSearchRepository.deleteAll();
        scoreclass = createEntity(em);
    }

    @Test
    @Transactional
    public void createScoreclass() throws Exception {
        int databaseSizeBeforeCreate = scoreclassRepository.findAll().size();

        // Create the Scoreclass
        restScoreclassMockMvc.perform(post("/api/scoreclasses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scoreclass)))
            .andExpect(status().isCreated());

        // Validate the Scoreclass in the database
        List<Scoreclass> scoreclassList = scoreclassRepository.findAll();
        assertThat(scoreclassList).hasSize(databaseSizeBeforeCreate + 1);
        Scoreclass testScoreclass = scoreclassList.get(scoreclassList.size() - 1);
        assertThat(testScoreclass.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testScoreclass.getNameshort()).isEqualTo(DEFAULT_NAMESHORT);
        assertThat(testScoreclass.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testScoreclass.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testScoreclass.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testScoreclass.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Scoreclass in Elasticsearch
        Scoreclass scoreclassEs = scoreclassSearchRepository.findOne(testScoreclass.getId());
        assertThat(scoreclassEs).isEqualToComparingFieldByField(testScoreclass);
    }

    @Test
    @Transactional
    public void createScoreclassWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = scoreclassRepository.findAll().size();

        // Create the Scoreclass with an existing ID
        scoreclass.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restScoreclassMockMvc.perform(post("/api/scoreclasses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scoreclass)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Scoreclass> scoreclassList = scoreclassRepository.findAll();
        assertThat(scoreclassList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = scoreclassRepository.findAll().size();
        // set the field null
        scoreclass.setName(null);

        // Create the Scoreclass, which fails.

        restScoreclassMockMvc.perform(post("/api/scoreclasses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scoreclass)))
            .andExpect(status().isBadRequest());

        List<Scoreclass> scoreclassList = scoreclassRepository.findAll();
        assertThat(scoreclassList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameshortIsRequired() throws Exception {
        int databaseSizeBeforeTest = scoreclassRepository.findAll().size();
        // set the field null
        scoreclass.setNameshort(null);

        // Create the Scoreclass, which fails.

        restScoreclassMockMvc.perform(post("/api/scoreclasses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scoreclass)))
            .andExpect(status().isBadRequest());

        List<Scoreclass> scoreclassList = scoreclassRepository.findAll();
        assertThat(scoreclassList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = scoreclassRepository.findAll().size();
        // set the field null
        scoreclass.setLastmodifiedby(null);

        // Create the Scoreclass, which fails.

        restScoreclassMockMvc.perform(post("/api/scoreclasses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scoreclass)))
            .andExpect(status().isBadRequest());

        List<Scoreclass> scoreclassList = scoreclassRepository.findAll();
        assertThat(scoreclassList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = scoreclassRepository.findAll().size();
        // set the field null
        scoreclass.setLastmodifieddatetime(null);

        // Create the Scoreclass, which fails.

        restScoreclassMockMvc.perform(post("/api/scoreclasses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scoreclass)))
            .andExpect(status().isBadRequest());

        List<Scoreclass> scoreclassList = scoreclassRepository.findAll();
        assertThat(scoreclassList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = scoreclassRepository.findAll().size();
        // set the field null
        scoreclass.setDomain(null);

        // Create the Scoreclass, which fails.

        restScoreclassMockMvc.perform(post("/api/scoreclasses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scoreclass)))
            .andExpect(status().isBadRequest());

        List<Scoreclass> scoreclassList = scoreclassRepository.findAll();
        assertThat(scoreclassList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllScoreclasses() throws Exception {
        // Initialize the database
        scoreclassRepository.saveAndFlush(scoreclass);

        // Get all the scoreclassList
        restScoreclassMockMvc.perform(get("/api/scoreclasses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scoreclass.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(sameInstant(DEFAULT_LASTMODIFIEDDATETIME))))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getScoreclass() throws Exception {
        // Initialize the database
        scoreclassRepository.saveAndFlush(scoreclass);

        // Get the scoreclass
        restScoreclassMockMvc.perform(get("/api/scoreclasses/{id}", scoreclass.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(scoreclass.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.nameshort").value(DEFAULT_NAMESHORT.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(sameInstant(DEFAULT_LASTMODIFIEDDATETIME)))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingScoreclass() throws Exception {
        // Get the scoreclass
        restScoreclassMockMvc.perform(get("/api/scoreclasses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateScoreclass() throws Exception {
        // Initialize the database
        scoreclassRepository.saveAndFlush(scoreclass);
        scoreclassSearchRepository.save(scoreclass);
        int databaseSizeBeforeUpdate = scoreclassRepository.findAll().size();

        // Update the scoreclass
        Scoreclass updatedScoreclass = scoreclassRepository.findOne(scoreclass.getId());
        updatedScoreclass
            .name(UPDATED_NAME)
            .nameshort(UPDATED_NAMESHORT)
            .description(UPDATED_DESCRIPTION)
            .lastmodifiedby(UPDATED_LASTMODIFIEDBY)
            .lastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME)
            .domain(UPDATED_DOMAIN);

        restScoreclassMockMvc.perform(put("/api/scoreclasses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedScoreclass)))
            .andExpect(status().isOk());

        // Validate the Scoreclass in the database
        List<Scoreclass> scoreclassList = scoreclassRepository.findAll();
        assertThat(scoreclassList).hasSize(databaseSizeBeforeUpdate);
        Scoreclass testScoreclass = scoreclassList.get(scoreclassList.size() - 1);
        assertThat(testScoreclass.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testScoreclass.getNameshort()).isEqualTo(UPDATED_NAMESHORT);
        assertThat(testScoreclass.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testScoreclass.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testScoreclass.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testScoreclass.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Scoreclass in Elasticsearch
        Scoreclass scoreclassEs = scoreclassSearchRepository.findOne(testScoreclass.getId());
        assertThat(scoreclassEs).isEqualToComparingFieldByField(testScoreclass);
    }

    @Test
    @Transactional
    public void updateNonExistingScoreclass() throws Exception {
        int databaseSizeBeforeUpdate = scoreclassRepository.findAll().size();

        // Create the Scoreclass

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restScoreclassMockMvc.perform(put("/api/scoreclasses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scoreclass)))
            .andExpect(status().isCreated());

        // Validate the Scoreclass in the database
        List<Scoreclass> scoreclassList = scoreclassRepository.findAll();
        assertThat(scoreclassList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteScoreclass() throws Exception {
        // Initialize the database
        scoreclassRepository.saveAndFlush(scoreclass);
        scoreclassSearchRepository.save(scoreclass);
        int databaseSizeBeforeDelete = scoreclassRepository.findAll().size();

        // Get the scoreclass
        restScoreclassMockMvc.perform(delete("/api/scoreclasses/{id}", scoreclass.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean scoreclassExistsInEs = scoreclassSearchRepository.exists(scoreclass.getId());
        assertThat(scoreclassExistsInEs).isFalse();

        // Validate the database is empty
        List<Scoreclass> scoreclassList = scoreclassRepository.findAll();
        assertThat(scoreclassList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchScoreclass() throws Exception {
        // Initialize the database
        scoreclassRepository.saveAndFlush(scoreclass);
        scoreclassSearchRepository.save(scoreclass);

        // Search the scoreclass
        restScoreclassMockMvc.perform(get("/api/_search/scoreclasses?query=id:" + scoreclass.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scoreclass.getId().intValue())))
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
        TestUtil.equalsVerifier(Scoreclass.class);
        Scoreclass scoreclass1 = new Scoreclass();
        scoreclass1.setId(1L);
        Scoreclass scoreclass2 = new Scoreclass();
        scoreclass2.setId(scoreclass1.getId());
        assertThat(scoreclass1).isEqualTo(scoreclass2);
        scoreclass2.setId(2L);
        assertThat(scoreclass1).isNotEqualTo(scoreclass2);
        scoreclass1.setId(null);
        assertThat(scoreclass1).isNotEqualTo(scoreclass2);
    }
}
