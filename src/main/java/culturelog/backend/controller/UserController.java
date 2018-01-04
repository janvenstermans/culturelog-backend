package culturelog.backend.controller;

import culturelog.backend.domain.User;
import culturelog.backend.dto.UserCreateDto;
import culturelog.backend.exception.CultureLogControllerExceptionKey;
import culturelog.backend.exception.CultureLogException;
import culturelog.backend.service.MessageService;
import culturelog.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

/**
 * @author Jan Venstermans
 */
@RestController
@RequestMapping("/users")
@Transactional // db changes in one test are rolled back after test
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    /**
     *
     * @param userCreateDto containing not-encoded password
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @PreAuthorize("permitAll")
    public ResponseEntity<?> registerUser(@RequestBody UserCreateDto userCreateDto, Locale locale) {
        try {
            User newUser = userService.registerUser(userCreateDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.toUserDto(newUser));
        } catch (CultureLogException e) {
            return ResponseEntity.badRequest().body(messageService.getControllerMessage(CultureLogControllerExceptionKey.USERS_CREATE, e, locale));
        }
    }

    /**
     *  Changing info except for username and password.
     * @param userDto change the user info (limited info can be changed)
     * @return
     */
//    @RequestMapping(value = "/update", method = RequestMethod.PUT)
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<?> updateUser(@RequestBody(required = true) UserDto userDto) {
//       throw new UnsupportedOperationException();
//    }

    /**
     *  Changing password of logged in user.
     * @param userCreateDto should contain username and password
     * @return
     */
//    @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<?> changePassword(@RequestBody(required = true) UserCreateDto userCreateDto) {
//       throw new UnsupportedOperationException();
//    }
}