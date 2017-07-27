package culturelog.rest.controller;

import culturelog.rest.CulturelogRestApplication;
import culturelog.rest.domain.User;
import culturelog.rest.dto.UserCreateDto;
import culturelog.rest.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Test class for {@link UserController}.
 * Created by janv on 24-Jul-17.
 *
 * @see http://spring.io/guides/tutorials/bookmarks/
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CulturelogRestApplication.class)
@WebAppConfiguration
public class UserControllerTest {

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setUp() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
    }

    // users/register POST

    @Test
    public void testRegisterUser_userNameIsEmailAdress() throws Exception {
        String userName = "a@b.cd";
        String passwordNotEncoded = "password";
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername(userName);
        userCreateDto.setPassword(passwordNotEncoded);

        mockMvc.perform(post("/users/register")
                .content(this.json(userCreateDto))
                .contentType(contentType))
                .andExpect(status().isCreated());

        User user = userRepository.findByUsername(userName);
        assertNotNull(user);
        assertTrue(user.isActive());
        assertNotEquals(passwordNotEncoded, user.getPassword());
    }

    @Test
    public void testRegisterUser_userNameIsNotEmailAdress() throws Exception {
        String userName = "abcd";
        String passwordNotEncoded = "password";
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername(userName);
        userCreateDto.setPassword(passwordNotEncoded);

        mockMvc.perform(post("/users/register")
                .content(this.json(userCreateDto))
                .contentType(contentType))
                .andExpect(status().is4xxClientError());

        User user = userRepository.findByUsername(userName);
        assertNull(user);
    }

    @Test
    public void testRegisterUser_methodNotAllowed() throws Exception {
        UserCreateDto dummy = new UserCreateDto();

        mockMvc.perform(get("/users/register"))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(head("/users/register"))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(put("/users/register")
                .content(this.json(dummy))
                .contentType(contentType))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(patch("/users/register")
                .content(this.json(dummy))
                .contentType(contentType))
                .andExpect(status().isMethodNotAllowed());
        mockMvc.perform(delete("/users/register"))
                .andExpect(status().isMethodNotAllowed());

        //OPTIONS
        //TODO: make options header check more general, for multiple methods (order not important).
        mockMvc.perform(options("/users/register"))
                .andExpect(status().isOk())
                .andExpect(header().string(ControllerTestUtils.ALLOW_HEADER, "POST"));
    }

    // helper methods

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}