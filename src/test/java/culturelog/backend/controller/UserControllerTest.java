package culturelog.backend.controller;

import culturelog.backend.CultureLogBackendApplication;
import culturelog.backend.configuration.CultureLogTestConfiguration;
import culturelog.backend.domain.User;
import culturelog.backend.dto.UserCreateDto;
import culturelog.backend.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for {@link UserController}.
 *
 * @author Jan Venstermans
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CultureLogBackendApplication.class)
@WebAppConfiguration
@Transactional // db changes in one test are rolled back after test
public class UserControllerTest extends ControllerTestAbstract {

    @Autowired
    private UserRepository userRepository;

    private static final String URL_USERS = "/users";

    // users OPTIONS

    @Test
    public void testUsersUrl_allowedMethods() throws Exception {
        testUrlAllowedMethods(URL_USERS, HttpMethod.POST);
    }

    // users POST

    @Test
    public void testCreateUser_userNameAlreadyExists() throws Exception {
        String userName = CultureLogTestConfiguration.USER1_NAME;
        String passwordNotEncoded = "password";
        Assert.assertNotNull(userRepository.findByUsername(userName));

        UserCreateDto userCreateDto = createUserCreateDto(userName, passwordNotEncoded);

        mockMvc.perform(post(URL_USERS)
                .content(this.json(userCreateDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());
    }

    @Test
    public void testCreateUser_userNameIsEmailAdress() throws Exception {
        String userName = "abv@b.cd";
        String passwordNotEncoded = "password";
        assertNull(userRepository.findByUsername(userName));

        UserCreateDto userCreateDto = createUserCreateDto(userName, passwordNotEncoded);

        mockMvc.perform(post(URL_USERS)
                .content(this.json(userCreateDto))
                .contentType(contentType))
                .andExpect(status().isCreated());

        User user = userRepository.findByUsername(userName);
        assertNotNull(user);
        assertTrue(user.isActive());
        assertNotEquals(passwordNotEncoded, user.getPassword());
    }

    @Test
    public void testCreateUser_userNameIsNotEmailAdress() throws Exception {
        String userName = "abcd";
        String passwordNotEncoded = "password";
        assertNull(userRepository.findByUsername(userName));

        UserCreateDto userCreateDto = createUserCreateDto(userName, passwordNotEncoded);

        mockMvc.perform(post(URL_USERS)
                .content(this.json(userCreateDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());

        assertNull(userRepository.findByUsername(userName));
    }

    @Test
    public void testCreateUser_dtoEmpty() throws Exception {
        String userName = null;
        String passwordNotEncoded = null;
        assertNull(userRepository.findByUsername(userName));

        UserCreateDto userCreateDto = createUserCreateDto(userName, passwordNotEncoded);

        mockMvc.perform(post(URL_USERS)
                .content(this.json(userCreateDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());

        assertNull(userRepository.findByUsername(userName));
    }

    @Test
    public void testCreateUser_noUserName() throws Exception {
        String userName = null;
        String passwordNotEncoded = "password";
        assertNull(userRepository.findByUsername(userName));

        UserCreateDto userCreateDto = createUserCreateDto(userName, passwordNotEncoded);

        mockMvc.perform(post(URL_USERS)
                .content(this.json(userCreateDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());

        assertNull(userRepository.findByUsername(userName));
    }

    @Test
    public void testCreateUser_noPassword() throws Exception {
        String userName = "abv@b.cd";
        String passwordNotEncoded = null;
        assertNull(userRepository.findByUsername(userName));

        UserCreateDto userCreateDto = createUserCreateDto(userName, passwordNotEncoded);

        mockMvc.perform(post(URL_USERS)
                .content(this.json(userCreateDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());

        assertNull(userRepository.findByUsername(userName));
    }

    //TODO: create test that is succesful for user create

    // helper methods

    private UserCreateDto createUserCreateDto(String userName, String passwordNotEncoded) {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername(userName);
        userCreateDto.setPassword(passwordNotEncoded);
        return userCreateDto;
    }
}