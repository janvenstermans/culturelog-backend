package culturelog.rest.controller;

import culturelog.rest.domain.Experience;
import culturelog.rest.exception.CultureLogException;
import culturelog.rest.security.CultureLogSecurityService;
import culturelog.rest.service.ExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author Jan Venstermans
 */
//TODO: check later
//@RestController
//@RequestMapping("/experiences")
public class ExperiencesController {

    @Autowired
    private ExperienceService experienceService;

    @Autowired
    private CultureLogSecurityService securityService;

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createExperience(@RequestBody(required = true) Experience experience) {
        if (experience.getId() != null) {
            return ResponseEntity.badRequest().body("Cannot create experience with id ");
        }
        String username = securityService.getLoggedInUsername();
        experience.setUsername(username);
        try {
            if (experience.getDate() == null) {
                experience.setDate(new Date());
            }
            Experience newExperience = experienceService.save(experience);
            return ResponseEntity.ok(newExperience);
        } catch (CultureLogException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public List<Experience> getExperienceList() {
        String username = securityService.getLoggedInUsername();
        return experienceService.getExperiencesOfUser(username);
    }

    @RequestMapping(value = "/{experienceId}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getExperience(@PathVariable(value="experienceId", required = true) Long experienceId) {
        String username = securityService.getLoggedInUsername();
        Experience experience = experienceService.getById(experienceId);
        if (experience != null && experience.getUsername().equals(username)) {
            return ResponseEntity.ok(experience);
        }
        return ResponseEntity.badRequest().body("Cannot find experience with id " + experienceId);
    }

    @RequestMapping(value = "/{experienceId}", method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateExperience(@PathVariable(value="experienceId", required = true) Long experienceId,
                                              @RequestBody(required = true) Experience experience) {
        String username = securityService.getLoggedInUsername();
        Experience existingExperience = experienceService.getById(experienceId);
        if (existingExperience == null || !username.equals(existingExperience.getUsername())) {
            return ResponseEntity.badRequest().body("Cannot update experience with id " + experienceId);
        }
        experience.setId(experienceId);
        experience.setUsername(username);
        try {
            Experience updateExperience = experienceService.save(experience);
            return ResponseEntity.ok(updateExperience);
        } catch (CultureLogException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }
}