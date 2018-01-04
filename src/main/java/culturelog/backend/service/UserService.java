package culturelog.backend.service;

import culturelog.backend.domain.User;
import culturelog.backend.dto.UserCreateDto;
import culturelog.backend.dto.UserDto;
import culturelog.backend.exception.CultureLogException;

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
