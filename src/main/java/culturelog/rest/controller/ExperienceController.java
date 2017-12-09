package culturelog.rest.controller;

import culturelog.rest.domain.DisplayDate;
import culturelog.rest.domain.Experience;
import culturelog.rest.domain.Moment;
import culturelog.rest.domain.MomentType;
import culturelog.rest.dto.ExperienceDto;
import culturelog.rest.exception.CulturLogControllerExceptionKey;
import culturelog.rest.exception.CultureLogException;
import culturelog.rest.exception.CultureLogExceptionKey;
import culturelog.rest.security.CultureLogSecurityService;
import culturelog.rest.service.ExperienceService;
import culturelog.rest.service.MessageService;
import culturelog.rest.utils.ExperienceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Jan Venstermans
 */
@RestController
@RequestMapping("/experiences")
public class ExperienceController {

    @Autowired
    private ExperienceService experienceService;

    @Autowired
    private CultureLogSecurityService securityService;

    @Autowired
    private MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createExperience(@RequestBody ExperienceDto experienceDto, Locale locale) {
        Experience experience = ExperienceUtils.fromExperienceDto(experienceDto);
        try {
            if (experience.getId() != null) {
                throw new CultureLogException(CultureLogExceptionKey.CREATE_WITH_ID);
            }
            experience.setUser(securityService.getLoggedInUser());

            Experience newExperience = experienceService.save(experience);
            return ResponseEntity.status(HttpStatus.CREATED).body(ExperienceUtils.toExperienceDto(newExperience));
        } catch (CultureLogException e) {
            return ResponseEntity.badRequest().body(messageService.getControllerMessage(CulturLogControllerExceptionKey.EXPERIENCES_CREATE, e, locale));
        }
    }
//
//    @RequestMapping(method = RequestMethod.GET)
//    @PreAuthorize("isAuthenticated()")
//    public List<Experience> getExperienceList() {
//        String username = securityService.getLoggedInUsername();
//        return experienceService.getExperiencesOfUser(username);
//    }
//
//    @RequestMapping(value = "/{experienceId}", method = RequestMethod.GET)
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<?> getExperience(@PathVariable(value="experienceId", required = true) Long experienceId) {
//        String username = securityService.getLoggedInUsername();
//        Experience experience = experienceService.getById(experienceId);
//        if (experience != null && experience.getUsername().equals(username)) {
//            return ResponseEntity.ok(experience);
//        }
//        return ResponseEntity.badRequest().body("Cannot find experience with id " + experienceId);
//    }
//
//    @RequestMapping(value = "/{experienceId}", method = RequestMethod.PUT)
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<?> updateExperience(@PathVariable(value="experienceId", required = true) Long experienceId,
//                                              @RequestBody(required = true) Experience experience) {
//        String username = securityService.getLoggedInUsername();
//        Experience existingExperience = experienceService.getById(experienceId);
//        if (existingExperience == null || !username.equals(existingExperience.getUsername())) {
//            return ResponseEntity.badRequest().body("Cannot update experience with id " + experienceId);
//        }
//        experience.setId(experienceId);
//        experience.setUsername(username);
//        try {
//            Experience updateExperience = experienceService.save(experience);
//            return ResponseEntity.ok(updateExperience);
//        } catch (CultureLogException e) {
//            return ResponseEntity.badRequest().body(e);
//        }
//    }
}