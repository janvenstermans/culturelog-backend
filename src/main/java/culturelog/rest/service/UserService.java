package culturelog.rest.service;

import culturelog.rest.domain.User;
import culturelog.rest.dto.UserCreateDto;
import culturelog.rest.dto.UserDto;
import culturelog.rest.exception.CultureLogException;

/**
 * @author Jan Venstermans
 */
public interface UserService {

    User save(User user);

    User getByUserName(String userName);

    User getById(Long userId);

    User registerUser(UserCreateDto userCreateDto) throws CultureLogException;

    UserDto toUserDto(User user);
}
