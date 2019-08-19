package com.jfn.riddle.backend.web.rest;

import com.jfn.riddle.backend.domain.Riddle;
import com.jfn.riddle.backend.repository.RiddleRepository;
import com.jfn.riddle.backend.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.jfn.riddle.backend.domain.Riddle}.
 */
@RestController
@RequestMapping("/api")
public class RiddleResource {

    private final Logger log = LoggerFactory.getLogger(RiddleResource.class);

    private static final String ENTITY_NAME = "riddle";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RiddleRepository riddleRepository;

    public RiddleResource(RiddleRepository riddleRepository) {
        this.riddleRepository = riddleRepository;
    }

    /**
     * {@code POST  /riddles} : Create a new riddle.
     *
     * @param riddle the riddle to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new riddle, or with status {@code 400 (Bad Request)} if the riddle has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/riddles")
    public ResponseEntity<Riddle> createRiddle(@Valid @RequestBody Riddle riddle) throws URISyntaxException {
        log.debug("REST request to save Riddle : {}", riddle);
        if (riddle.getId() != null) {
            throw new BadRequestAlertException("A new riddle cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Riddle result = riddleRepository.save(riddle);
        return ResponseEntity.created(new URI("/api/riddles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /riddles} : Updates an existing riddle.
     *
     * @param riddle the riddle to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated riddle,
     * or with status {@code 400 (Bad Request)} if the riddle is not valid,
     * or with status {@code 500 (Internal Server Error)} if the riddle couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/riddles")
    public ResponseEntity<Riddle> updateRiddle(@Valid @RequestBody Riddle riddle) throws URISyntaxException {
        log.debug("REST request to update Riddle : {}", riddle);
        if (riddle.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Riddle result = riddleRepository.save(riddle);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, riddle.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /riddles} : get all the riddles.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of riddles in body.
     */
    @GetMapping("/riddles")
    public ResponseEntity<List<Riddle>> getAllRiddles(Pageable pageable) {
        log.debug("REST request to get a page of Riddles");
        Page<Riddle> page = riddleRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /riddles/:id} : get the "id" riddle.
     *
     * @param id the id of the riddle to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the riddle, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/riddles/{id}")
    public ResponseEntity<Riddle> getRiddle(@PathVariable Long id) {
        log.debug("REST request to get Riddle : {}", id);
        Optional<Riddle> riddle = riddleRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(riddle);
    }

    /**
     * {@code DELETE  /riddles/:id} : delete the "id" riddle.
     *
     * @param id the id of the riddle to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/riddles/{id}")
    public ResponseEntity<Void> deleteRiddle(@PathVariable Long id) {
        log.debug("REST request to delete Riddle : {}", id);
        riddleRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
