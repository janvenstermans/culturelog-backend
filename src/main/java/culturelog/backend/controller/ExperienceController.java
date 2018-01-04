package culturelog.backend.controller;

import culturelog.backend.domain.Experience;
import culturelog.backend.dto.ExperienceDto;
import culturelog.backend.exception.CultureLogControllerExceptionKey;
import culturelog.backend.exception.CultureLogException;
import culturelog.backend.exception.CultureLogExceptionKey;
import culturelog.backend.security.CultureLogSecurityService;
import culturelog.backend.service.ExperienceService;
import culturelog.backend.service.MessageService;
import culturelog.backend.utils.ExperienceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final String DEFAULT_SORT_COLUMN = "moment.sortDate";
    public static final boolean DEFAULT_SORT_ASC = false;

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
            return ResponseEntity.badRequest().body(messageService.getControllerMessage(CultureLogControllerExceptionKey.EXPERIENCES_CREATE, e, locale));
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getExperienceList(@PageableDefault(page = DEFAULT_PAGE_NUMBER, size = DEFAULT_PAGE_SIZE)
                                                   @SortDefault.SortDefaults({
                                                           @SortDefault(sort = DEFAULT_SORT_COLUMN, direction = Sort.Direction.DESC)
                                                   })
                                                           Pageable pageable) {
        Page<Experience> experiencePage = experienceService.getExperiencesOfUser(securityService.getLoggedInUserId(), pageable);
        return ResponseEntity.ok().body(experiencePage.map(ExperienceUtils::toExperienceDto));
    }

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