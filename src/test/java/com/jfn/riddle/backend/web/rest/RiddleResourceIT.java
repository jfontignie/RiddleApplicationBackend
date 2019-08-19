package com.jfn.riddle.backend.web.rest;

import com.jfn.riddle.backend.RiddleApplicationApp;
import com.jfn.riddle.backend.domain.Riddle;
import com.jfn.riddle.backend.repository.RiddleRepository;
import com.jfn.riddle.backend.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.jfn.riddle.backend.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link RiddleResource} REST controller.
 */
@SpringBootTest(classes = RiddleApplicationApp.class)
public class RiddleResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private RiddleRepository riddleRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restRiddleMockMvc;

    private Riddle riddle;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RiddleResource riddleResource = new RiddleResource(riddleRepository);
        this.restRiddleMockMvc = MockMvcBuilders.standaloneSetup(riddleResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Riddle createEntity(EntityManager em) {
        Riddle riddle = new Riddle()
            .name(DEFAULT_NAME);
        return riddle;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Riddle createUpdatedEntity(EntityManager em) {
        Riddle riddle = new Riddle()
            .name(UPDATED_NAME);
        return riddle;
    }

    @BeforeEach
    public void initTest() {
        riddle = createEntity(em);
    }

    @Test
    @Transactional
    public void createRiddle() throws Exception {
        int databaseSizeBeforeCreate = riddleRepository.findAll().size();

        // Create the Riddle
        restRiddleMockMvc.perform(post("/api/riddles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(riddle)))
            .andExpect(status().isCreated());

        // Validate the Riddle in the database
        List<Riddle> riddleList = riddleRepository.findAll();
        assertThat(riddleList).hasSize(databaseSizeBeforeCreate + 1);
        Riddle testRiddle = riddleList.get(riddleList.size() - 1);
        assertThat(testRiddle.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createRiddleWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = riddleRepository.findAll().size();

        // Create the Riddle with an existing ID
        riddle.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRiddleMockMvc.perform(post("/api/riddles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(riddle)))
            .andExpect(status().isBadRequest());

        // Validate the Riddle in the database
        List<Riddle> riddleList = riddleRepository.findAll();
        assertThat(riddleList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = riddleRepository.findAll().size();
        // set the field null
        riddle.setName(null);

        // Create the Riddle, which fails.

        restRiddleMockMvc.perform(post("/api/riddles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(riddle)))
            .andExpect(status().isBadRequest());

        List<Riddle> riddleList = riddleRepository.findAll();
        assertThat(riddleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRiddles() throws Exception {
        // Initialize the database
        riddleRepository.saveAndFlush(riddle);

        // Get all the riddleList
        restRiddleMockMvc.perform(get("/api/riddles?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(riddle.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
    
    @Test
    @Transactional
    public void getRiddle() throws Exception {
        // Initialize the database
        riddleRepository.saveAndFlush(riddle);

        // Get the riddle
        restRiddleMockMvc.perform(get("/api/riddles/{id}", riddle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(riddle.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRiddle() throws Exception {
        // Get the riddle
        restRiddleMockMvc.perform(get("/api/riddles/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRiddle() throws Exception {
        // Initialize the database
        riddleRepository.saveAndFlush(riddle);

        int databaseSizeBeforeUpdate = riddleRepository.findAll().size();

        // Update the riddle
        Riddle updatedRiddle = riddleRepository.findById(riddle.getId()).get();
        // Disconnect from session so that the updates on updatedRiddle are not directly saved in db
        em.detach(updatedRiddle);
        updatedRiddle
            .name(UPDATED_NAME);

        restRiddleMockMvc.perform(put("/api/riddles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedRiddle)))
            .andExpect(status().isOk());

        // Validate the Riddle in the database
        List<Riddle> riddleList = riddleRepository.findAll();
        assertThat(riddleList).hasSize(databaseSizeBeforeUpdate);
        Riddle testRiddle = riddleList.get(riddleList.size() - 1);
        assertThat(testRiddle.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingRiddle() throws Exception {
        int databaseSizeBeforeUpdate = riddleRepository.findAll().size();

        // Create the Riddle

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRiddleMockMvc.perform(put("/api/riddles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(riddle)))
            .andExpect(status().isBadRequest());

        // Validate the Riddle in the database
        List<Riddle> riddleList = riddleRepository.findAll();
        assertThat(riddleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteRiddle() throws Exception {
        // Initialize the database
        riddleRepository.saveAndFlush(riddle);

        int databaseSizeBeforeDelete = riddleRepository.findAll().size();

        // Delete the riddle
        restRiddleMockMvc.perform(delete("/api/riddles/{id}", riddle.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Riddle> riddleList = riddleRepository.findAll();
        assertThat(riddleList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Riddle.class);
        Riddle riddle1 = new Riddle();
        riddle1.setId(1L);
        Riddle riddle2 = new Riddle();
        riddle2.setId(riddle1.getId());
        assertThat(riddle1).isEqualTo(riddle2);
        riddle2.setId(2L);
        assertThat(riddle1).isNotEqualTo(riddle2);
        riddle1.setId(null);
        assertThat(riddle1).isNotEqualTo(riddle2);
    }
}
