package culturelog.rest.controller;

import culturelog.rest.domain.User;
import culturelog.rest.dto.UserCreateDto;
import culturelog.rest.dto.UserDto;
import culturelog.rest.exception.CultureLogException;
import culturelog.rest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jan Venstermans
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     *
     * @param userCreateDto containing not-encoded password
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @PreAuthorize("permitAll")
    public ResponseEntity<?> registerUser(@RequestBody(required = true) UserCreateDto userCreateDto) {
        try {
            User newUser = userService.registerUser(userCreateDto);
            return ResponseEntity.ok(userService.toUserDto(newUser));
        } catch (CultureLogException e) {
            // maybe split it up, in different return codes
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    /**
     *  Changing info except for username and password.
     * @param userDto change the user info (limited info can be changed)
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUser(@RequestBody(required = true) UserDto userDto) {
       throw new UnsupportedOperationException();
    }

    /**
     *  Changing password of logged in user.
     * @param userCreateDto should contain username and password
     * @return
     */
    @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(@RequestBody(required = true) UserCreateDto userCreateDto) {
       throw new UnsupportedOperationException();
    }
}