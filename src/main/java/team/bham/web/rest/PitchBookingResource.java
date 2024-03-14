package team.bham.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import team.bham.domain.PitchBooking;
import team.bham.repository.PitchBookingRepository;
import team.bham.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.PitchBooking}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PitchBookingResource {

    private final Logger log = LoggerFactory.getLogger(PitchBookingResource.class);

    private static final String ENTITY_NAME = "pitchBooking";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PitchBookingRepository pitchBookingRepository;

    public PitchBookingResource(PitchBookingRepository pitchBookingRepository) {
        this.pitchBookingRepository = pitchBookingRepository;
    }

    /**
     * {@code POST  /pitch-bookings} : Create a new pitchBooking.
     *
     * @param pitchBooking the pitchBooking to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pitchBooking, or with status {@code 400 (Bad Request)} if the pitchBooking has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pitch-bookings")
    public ResponseEntity<PitchBooking> createPitchBooking(@Valid @RequestBody PitchBooking pitchBooking) throws URISyntaxException {
        log.debug("REST request to save PitchBooking : {}", pitchBooking);
        if (pitchBooking.getId() != null) {
            throw new BadRequestAlertException("A new pitchBooking cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PitchBooking result = pitchBookingRepository.save(pitchBooking);
        return ResponseEntity
            .created(new URI("/api/pitch-bookings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /pitch-bookings/:id} : Updates an existing pitchBooking.
     *
     * @param id the id of the pitchBooking to save.
     * @param pitchBooking the pitchBooking to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pitchBooking,
     * or with status {@code 400 (Bad Request)} if the pitchBooking is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pitchBooking couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pitch-bookings/{id}")
    public ResponseEntity<PitchBooking> updatePitchBooking(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PitchBooking pitchBooking
    ) throws URISyntaxException {
        log.debug("REST request to update PitchBooking : {}, {}", id, pitchBooking);
        if (pitchBooking.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pitchBooking.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pitchBookingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PitchBooking result = pitchBookingRepository.save(pitchBooking);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pitchBooking.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /pitch-bookings/:id} : Partial updates given fields of an existing pitchBooking, field will ignore if it is null
     *
     * @param id the id of the pitchBooking to save.
     * @param pitchBooking the pitchBooking to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pitchBooking,
     * or with status {@code 400 (Bad Request)} if the pitchBooking is not valid,
     * or with status {@code 404 (Not Found)} if the pitchBooking is not found,
     * or with status {@code 500 (Internal Server Error)} if the pitchBooking couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pitch-bookings/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PitchBooking> partialUpdatePitchBooking(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PitchBooking pitchBooking
    ) throws URISyntaxException {
        log.debug("REST request to partial update PitchBooking partially : {}, {}", id, pitchBooking);
        if (pitchBooking.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pitchBooking.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pitchBookingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PitchBooking> result = pitchBookingRepository
            .findById(pitchBooking.getId())
            .map(existingPitchBooking -> {
                if (pitchBooking.getBookingDate() != null) {
                    existingPitchBooking.setBookingDate(pitchBooking.getBookingDate());
                }
                if (pitchBooking.getStartTime() != null) {
                    existingPitchBooking.setStartTime(pitchBooking.getStartTime());
                }
                if (pitchBooking.getEndTime() != null) {
                    existingPitchBooking.setEndTime(pitchBooking.getEndTime());
                }

                return existingPitchBooking;
            })
            .map(pitchBookingRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pitchBooking.getId().toString())
        );
    }

    /**
     * {@code GET  /pitch-bookings} : get all the pitchBookings.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pitchBookings in body.
     */
    @GetMapping("/pitch-bookings")
    public List<PitchBooking> getAllPitchBookings() {
        log.debug("REST request to get all PitchBookings");
        return pitchBookingRepository.findAll();
    }

    /**
     * {@code GET  /pitch-bookings/:id} : get the "id" pitchBooking.
     *
     * @param id the id of the pitchBooking to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pitchBooking, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pitch-bookings/{id}")
    public ResponseEntity<PitchBooking> getPitchBooking(@PathVariable Long id) {
        log.debug("REST request to get PitchBooking : {}", id);
        Optional<PitchBooking> pitchBooking = pitchBookingRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(pitchBooking);
    }

    /**
     * GET  /available-bookings : Get available bookings for a specific date.
     *
     * @param date the date for which to check availability
     * @return the ResponseEntity with status 200 (OK) and the list of available bookings in body
     */
    @GetMapping("/available-bookings")
    public ResponseEntity<List<PitchBooking>> getAvailableBookingsForDate(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        log.debug("REST request to get available bookings for date : {}", date);

        // Assuming you have a method in your repository to find available bookings for a given date
        List<PitchBooking> availableBookings = pitchBookingRepository.findByBookingDate(date);
        return ResponseEntity.ok().body(availableBookings);
    }

    /**
     * {@code DELETE  /pitch-bookings/:id} : delete the "id" pitchBooking.
     *
     * @param id the id of the pitchBooking to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pitch-bookings/{id}")
    public ResponseEntity<Void> deletePitchBooking(@PathVariable Long id) {
        log.debug("REST request to delete PitchBooking : {}", id);
        pitchBookingRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
