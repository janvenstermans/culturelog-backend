package culturelog.rest.controller;

import culturelog.rest.domain.Medium;
import culturelog.rest.domain.User;
import culturelog.rest.dto.MediumDto;
import culturelog.rest.exception.CultureLogException;
import culturelog.rest.security.CultureLogSecurityService;
import culturelog.rest.service.MediumService;
import culturelog.rest.service.MediumService;
import culturelog.rest.utils.MediumUtils;
import culturelog.rest.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Jan Venstermans
 */
@RestController
@RequestMapping("/media")
public class MediumController {

    @Autowired
    private MediumService mediumService;

    @Autowired
    private CultureLogSecurityService securityService;

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createMedium(@RequestBody(required = true) MediumDto mediumDto) {
        Medium medium = MediumUtils.fromMediumDto(mediumDto);
        if (medium.getId() != null) {
            return ResponseEntity.badRequest().body("Cannot create medium with id ");
        }
        medium.setUser(securityService.getLoggedInUser());
        try {
            Medium newMedium = mediumService.save(medium);
            return ResponseEntity.status(HttpStatus.CREATED).body(MediumUtils.toMediumDto(newMedium));
        } catch (CultureLogException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMediaOfUserAndGlobalMedia() {
        try {
            Long userId = securityService.getLoggedInUserId();
            List<Medium> mediumList = mediumService.getMediaOfUserByUserId(userId, true);
            return ResponseEntity.ok(MediumUtils.toMediumDtoList(mediumList));
//        } catch (CultureLogException e) {
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @RequestMapping(value = "/{mediumId}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMedium(@PathVariable(value="mediumId", required = true) Long mediumId) {
        User user = securityService.getLoggedInUser();
        Medium medium = mediumService.getById(mediumId);
        if (medium != null && MediumUtils.isMediumOfUser(medium, user, true)) {
            return ResponseEntity.ok(MediumUtils.toMediumDto(medium));
        }
        return ResponseEntity.badRequest().body("Cannot find medium with id " + mediumId);
    }

    @RequestMapping(value = "/{mediumId}", method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateMedium(@PathVariable(value="mediumId", required = true) Long mediumId,
                                              @RequestBody(required = true) MediumDto mediumDto) {
        Medium medium = MediumUtils.fromMediumDto(mediumDto);
        User user = securityService.getLoggedInUser();
        Medium existingMedium = mediumService.getById(mediumId);
        if (existingMedium == null || !UserUtils.areUsersSame(existingMedium.getUser(), user)) {
            return ResponseEntity.badRequest().body("Cannot update medium with id " + mediumId);
        }
        medium.setId(mediumId);
        medium.setUser(user);
        try {
            Medium updatedMedium = mediumService.save(medium);
            return ResponseEntity.ok(MediumUtils.toMediumDto(updatedMedium));
        } catch (CultureLogException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    //TODO: remove medium, with check if used in experience
}