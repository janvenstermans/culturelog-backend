package culturelog.rest.configuration;

import culturelog.rest.dto.UserCreateDto;
import culturelog.rest.exception.CultureLogException;
import culturelog.rest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 *  @author Jan Venstermans
 */
@Configuration
public class CultureLogTestConfiguration {

    @Autowired
    private UserService userService;

    public static final String USER1 = "a@b.cd";
    public static final String USER1_PASS = "password";

    @PostConstruct
    protected void fillDbWithTestData() throws CultureLogException {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername(USER1);
        userCreateDto.setPassword(USER1_PASS);
        userService.registerUser(userCreateDto);
    }

}