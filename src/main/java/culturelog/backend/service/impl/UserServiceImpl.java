package culturelog.backend.service.impl;

import culturelog.backend.domain.User;
import culturelog.backend.dto.UserCreateDto;
import culturelog.backend.dto.UserDto;
import culturelog.backend.exception.CultureLogException;
import culturelog.backend.exception.CultureLogExceptionKey;
import culturelog.backend.repository.UserRepository;
import culturelog.backend.service.UserService;
import culturelog.backend.utils.CultureLogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;

/**
 * @author Jan Venstermans
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getByUserName(String userName) {
        return userRepository.findByUsername(userName);
    }

    @Override
    public User getById(Long userId) {
        return userRepository.findOne(userId);
    }

    @Override
    public User registerUser(UserCreateDto userCreateDto) throws CultureLogException {
        if (userCreateDto == null) {
            throw new CultureLogException(CultureLogExceptionKey.USER_NULL);
        }
        String username = userCreateDto.getUsername();
        String password = userCreateDto.getPassword();
        if (CultureLogUtils.isNullOrEmpty(username) || CultureLogUtils.isNullOrEmpty(password)) {
            throw new CultureLogException(CultureLogExceptionKey.USER_NOT_ENOUGH_DATA);
        }
        // check if username is not yet in db
        if (getByUserName(username) != null) {
            throw new CultureLogException(CultureLogExceptionKey.USERNAME_IN_USE);
        }
//        // check if username is email: is done also in entity, but this will give more ok
        if (!CultureLogUtils.isEmail(username)) {
            throw new CultureLogException(CultureLogExceptionKey.USERNAME_MUST_BE_EMAIL);
        }
        // don't save the given user object, create new object and save some attributes
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setActive(true);
        try {
            return userRepository.save(newUser);
        } catch (ConstraintViolationException cve) {
            throw new CultureLogException(CultureLogExceptionKey.USERSAVE_CONTRAINT_VIOLATION);
        }
    }

    @Override
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
