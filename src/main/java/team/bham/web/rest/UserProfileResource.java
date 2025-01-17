package team.bham.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import team.bham.domain.Team;
import team.bham.domain.User;
import team.bham.domain.UserProfile;
import team.bham.repository.UserProfileRepository;
import team.bham.repository.UserRepository;
import team.bham.security.SecurityUtils;
import team.bham.service.UserProfileService;
import team.bham.web.rest.errors.BadRequestAlertException;
import team.bham.web.rest.errors.EmailAlreadyUsedException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link team.bham.domain.UserProfile}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UserProfileResource {

    private final Logger log = LoggerFactory.getLogger(UserProfileResource.class);

    private static class UserProfileResourceException extends RuntimeException {

        private UserProfileResourceException(String message) {
            super(message);
        }
    }

    private static final String ENTITY_NAME = "userProfile";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserProfileService userProfileService;

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    public UserProfileResource(
        UserProfileService userProfileService,
        UserProfileRepository userProfileRepository,
        UserRepository userRepository
    ) {
        this.userProfileService = userProfileService;
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST  /user-profiles} : Create a new userProfile.
     *
     * @param userProfile the userProfile to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userProfile, or with status {@code 400 (Bad Request)} if the userProfile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-profiles")
    public ResponseEntity<UserProfile> createUserProfile(@Valid @RequestBody UserProfile userProfile) throws URISyntaxException {
        log.debug("REST request to save UserProfile : {}", userProfile);
        if (userProfile.getId() != null) {
            throw new BadRequestAlertException("A new userProfile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        long userId = userProfileService.getUserId();
        if (userProfileRepository.existsById(userId)) {
            throw new UserProfileResource.UserProfileResourceException("Profile Already exists");
        }
        userProfile.setId(userId);
        UserProfile result = userProfileRepository.save(userProfile);
        return ResponseEntity
            .created(new URI("/api/user-profiles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-profiles/:id} : Updates an existing userProfile.
     *
     * @param id the id of the userProfile to save.
     * @param userProfile the userProfile to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userProfile,
     * or with status {@code 400 (Bad Request)} if the userProfile is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userProfile couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-profiles/{id}")
    public ResponseEntity<UserProfile> updateUserProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserProfile userProfile
    ) throws URISyntaxException {
        log.debug("REST request to update UserProfile : {}, {}", id, userProfile);
        if (userProfile.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userProfile.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userProfileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        long userId = userProfileService.getUserId();
        if (!Objects.equals(userId, userProfile.getId())) {
            throw new UserProfileResource.UserProfileResourceException("Not Authorised"); //replace with 403 response
        }

        UserProfile result = userProfileRepository.save(userProfile);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userProfile.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /user-profiles/:id} : Partial updates given fields of an existing userProfile, field will ignore if it is null
     *
     * @param id the id of the userProfile to save.
     * @param userProfile the userProfile to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userProfile,
     * or with status {@code 400 (Bad Request)} if the userProfile is not valid,
     * or with status {@code 404 (Not Found)} if the userProfile is not found,
     * or with status {@code 500 (Internal Server Error)} if the userProfile couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-profiles/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserProfile> partialUpdateUserProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserProfile userProfile
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserProfile partially : {}, {}", id, userProfile);

        if (userProfile.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userProfile.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userProfileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        long userId = userProfileService.getUserId();
        if (!Objects.equals(userId, userProfile.getId())) {
            throw new UserProfileResource.UserProfileResourceException("Not Authorised"); //replace with 403 response
        }

        Optional<UserProfile> result = userProfileRepository
            .findById(userProfile.getId())
            .map(existingUserProfile -> {
                if (userProfile.getCreated() != null) {
                    existingUserProfile.setCreated(userProfile.getCreated());
                }
                if (userProfile.getName() != null) {
                    existingUserProfile.setName(userProfile.getName());
                }
                if (userProfile.getProfilePic() != null) {
                    existingUserProfile.setProfilePic(userProfile.getProfilePic());
                }
                if (userProfile.getProfilePicContentType() != null) {
                    existingUserProfile.setProfilePicContentType(userProfile.getProfilePicContentType());
                }
                if (userProfile.getGender() != null) {
                    existingUserProfile.setGender(userProfile.getGender());
                }
                if (userProfile.getLocation() != null) {
                    existingUserProfile.setLocation(userProfile.getLocation());
                }
                if (userProfile.getPosition() != null) {
                    existingUserProfile.setPosition(userProfile.getPosition());
                }
                if (userProfile.getReferee() != null) {
                    existingUserProfile.setReferee(userProfile.getReferee());
                }

                return existingUserProfile;
            })
            .map(userProfileRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userProfile.getId().toString())
        );
    }

    /**
     * {@code GET  /user-profiles} : get all the userProfiles.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userProfiles in body.
     */
    @GetMapping("/user-profiles")
    public List<UserProfile> getAllUserProfiles(@RequestParam(required = false) String filter) {
        if ("teamowned-is-null".equals(filter)) {
            log.debug("REST request to get all UserProfiles where teamOwned is null");
            return StreamSupport
                .stream(userProfileRepository.findAll().spliterator(), false)
                .filter(userProfile -> userProfile.getTeamOwned() == null)
                .collect(Collectors.toList());
        }
        log.debug("REST request to get all UserProfiles");
        return userProfileRepository.findAll();
    }

    /**
     * GET  /user-profiles/search?name={name} : search for user profiles by name.
     *
     * @param name the name of the team to search for.
     * @return the ResponseEntity with status 200 (OK) and the list of teams in body.
     */
    @GetMapping("/user-profiles/search")
    public List<UserProfile> searchUsers(@RequestParam(required = false) String name) {
        log.debug("REST request to search user profile by name : {}", name);

        List<UserProfile> searchResults;
        if (name != null) {
            searchResults = userProfileRepository.findByNameContainingIgnoreCase(name);
        } else {
            searchResults = userProfileRepository.findAll();
        }
        return searchResults;
    }

    /**
     * {@code GET  /user-profiles/:id} : get the "id" userProfile.
     *
     * @param id the id of the userProfile to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userProfile, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-profiles/{id}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable Long id) {
        log.debug("REST request to get UserProfile : {}", id);
        Optional<UserProfile> userProfile = userProfileRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(userProfile);
    }

    /**
     * {@code DELETE  /user-profiles/:id} : delete the "id" userProfile.
     *
     * @param id the id of the userProfile to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-profiles/{id}")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable Long id) {
        long userId = userProfileService.getUserId();
        if (!Objects.equals(userId, id)) {
            throw new UserProfileResource.UserProfileResourceException("Not Authorised"); //replace with 403 response
        }
        log.debug("REST request to delete UserProfile : {}", id);
        userProfileRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
