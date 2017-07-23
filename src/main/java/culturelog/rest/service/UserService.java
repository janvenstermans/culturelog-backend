package culturelog.rest.service;

import culturelog.rest.domain.User;
import culturelog.rest.dto.UserCreateDto;
import culturelog.rest.dto.UserDto;
import culturelog.rest.exception.CultureLogException;
import culturelog.rest.repository.UserRepository;
import culturelog.rest.utils.CultureLogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;

/**
 * @author Jan Venstermans
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User save(User user) {
        return userRepository.save(user);
    }

    public User getByUserName(String userName) {
        return userRepository.findByUsername(userName);
    }

    public User getById(Long userId) {
        return userRepository.findOne(userId);
    }

    public User registerUser(UserCreateDto userCreateDto) throws CultureLogException {
        if (userCreateDto == null) {
            throw new IllegalArgumentException("Cannot save null user");
        }
        String username = userCreateDto.getUsername();
        String password = userCreateDto.getPassword();
        if (CultureLogUtils.isNullOrEmpty(username) || CultureLogUtils.isNullOrEmpty(password)) {
            throw new CultureLogException("Cannot create user: username or password not provided");
        }
        // check if username is not yet in db
        if (getByUserName(username) != null) {
            throw new CultureLogException("Cannot create user: username allready in use");
        }
//        // check if username is email: is done also in entity, but this will give more ok
        if (!CultureLogUtils.isEmail(username)) {
            throw new CultureLogException("Cannot create user: username must be email");
        }
        // don't save the given user object, create new object and save some attributes
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setActive(true);
        try {
            return userRepository.save(newUser);
        } catch (ConstraintViolationException cve) {
            //TODO: use well
            throw new CultureLogException(cve.getMessage());
        }
    }

    public UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setActive(user.isActive());
        return userDto;
    }
}
