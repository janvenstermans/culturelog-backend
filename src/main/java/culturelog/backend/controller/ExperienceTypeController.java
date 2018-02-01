package culturelog.backend.controller;

import culturelog.backend.domain.ExperienceType;
import culturelog.backend.domain.User;
import culturelog.backend.dto.ExperienceTypeDto;
import culturelog.backend.exception.CultureLogControllerExceptionKey;
import culturelog.backend.exception.CultureLogException;
import culturelog.backend.exception.CultureLogExceptionKey;
import culturelog.backend.security.CultureLogSecurityService;
import culturelog.backend.service.ExperienceTypeService;
import culturelog.backend.service.MessageService;
import culturelog.backend.utils.ExperienceTypeUtils;
import culturelog.backend.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

/**
 * @author Jan Venstermans
 */
@RestController
@RequestMapping("/experienceTypes")
public class ExperienceTypeController {

    @Autowired
    private ExperienceTypeService experienceTypeService;

    @Autowired
    private CultureLogSecurityService securityService;

    @Autowired
    private MessageService messageService;

    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final String DEFAULT_SORT_COLUMN_0 = "name";
    public static final boolean DEFAULT_SORT_ASC_0 = true;
    public static final String DEFAULT_SORT_COLUMN_1 = "id";
    public static final boolean DEFAULT_SORT_ASC_1 = true;

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createExperienceType(@RequestBody ExperienceTypeDto experienceTypeDto, Locale locale) {
        ExperienceType experienceType = ExperienceTypeUtils.fromExperienceTypeDto(experienceTypeDto);
        try {
            if (experienceType.getId() != null) {
                throw new CultureLogException(CultureLogExceptionKey.CREATE_WITH_ID);
            }
            experienceType.setUser(securityService.getLoggedInUser());

            ExperienceType newExperienceType = experienceTypeService.save(experienceType);
            return ResponseEntity.status(HttpStatus.CREATED).body(ExperienceTypeUtils.toExperienceTypeDto(newExperienceType));
        } catch (CultureLogException e) {
            return ResponseEntity.badRequest().body(messageService.getControllerMessage(CultureLogControllerExceptionKey.EXPERIENCETYPES_CREATE, e, locale));
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getExperienceTypesOfUserAndGlobalExperienceTypes(@PageableDefault(page = DEFAULT_PAGE_NUMBER, size = DEFAULT_PAGE_SIZE)
                                                                                  @SortDefault.SortDefaults({
                                                                                          @SortDefault(sort = DEFAULT_SORT_COLUMN_0, direction = Sort.Direction.ASC),
                                                                                          @SortDefault(sort = DEFAULT_SORT_COLUMN_1, direction = Sort.Direction.ASC)
                                                                                  }) Pageable pageable) {
        Long userId = securityService.getLoggedInUserId();
        Page<ExperienceType> experienceTypePage = experienceTypeService.getExperienceTypesOfUserByUserId(userId, true, pageable);
        return ResponseEntity.ok(experienceTypePage.map(ExperienceTypeUtils::toExperienceTypeDto));
    }

    @RequestMapping(value = "/{experienceTypeId}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getExperienceType(@PathVariable(value="experienceTypeId") Long experienceTypeId, Locale locale) {
        User user = securityService.getLoggedInUser();
        ExperienceType experienceType = experienceTypeService.getById(experienceTypeId);
        if (experienceType != null && ExperienceTypeUtils.isExperienceTypeOfUser(experienceType, user, true)) {
            return ResponseEntity.ok(ExperienceTypeUtils.toExperienceTypeDto(experienceType));
        }
        return ResponseEntity.badRequest().body(messageService.getControllerMessage(
                CultureLogControllerExceptionKey.EXPERIENCETYPES_GET_ONE_UNKNOWN_ID, new Object[]{experienceTypeId}, locale));
    }

    @RequestMapping(value = "/{experienceTypeId}", method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateExperienceType(@PathVariable(value="experienceTypeId") Long experienceTypeId, @RequestBody ExperienceTypeDto experienceTypeDto, Locale locale) {
        ExperienceType experienceType = ExperienceTypeUtils.fromExperienceTypeDto(experienceTypeDto);
        User user = securityService.getLoggedInUser();
        ExperienceType existingExperienceType = experienceTypeService.getById(experienceTypeId);
        if (existingExperienceType == null || !UserUtils.areUsersSame(existingExperienceType.getUser(), user)) {
            return ResponseEntity.badRequest().body(messageService.getControllerMessage(
                    CultureLogControllerExceptionKey.EXPERIENCETYPES_UPDATE_ONE, new Object[]{experienceTypeId}, locale));
        }
        experienceType.setId(experienceTypeId);
        experienceType.setUser(user);
        try {
            ExperienceType updatedExperienceType = experienceTypeService.save(experienceType);
            return ResponseEntity.ok(ExperienceTypeUtils.toExperienceTypeDto(updatedExperienceType));
        } catch (CultureLogException e) {
            return ResponseEntity.badRequest().body(messageService.getControllerMessage(
                    CultureLogControllerExceptionKey.EXPERIENCETYPES_UPDATE_ONE, new Object[]{experienceTypeId}, e, locale));
        }
    }

    //TODO: remove experienceType, with check if used in experience
}