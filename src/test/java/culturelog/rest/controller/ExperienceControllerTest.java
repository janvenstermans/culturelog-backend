package culturelog.rest.controller;

import culturelog.rest.CulturelogRestApplication;
import culturelog.rest.configuration.CultureLogTestConfiguration;
import culturelog.rest.domain.Experience;
import culturelog.rest.domain.User;
import culturelog.rest.dto.ExperienceDto;
import culturelog.rest.dto.LocationDto;
import culturelog.rest.dto.UserCreateDto;
import culturelog.rest.repository.ExperienceRepository;
import culturelog.rest.repository.UserRepository;
import culturelog.rest.service.MessageService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for {@link ExperienceController}.
 *
 * @author Jan Venstermans
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CulturelogRestApplication.class)
@WebAppConfiguration
public class ExperienceControllerTest extends ControllerTestAbstract {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private MessageService messageService;

    private static final String URL_EXPERIENCES = "/experiences";
    private static final String URL_EXPERIENCE = "/experiences/%d";

    // experiences OPTIONS

    @Test
    public void testLocationsUrl_allowedMethods() throws Exception {
        testUrlAllowedMethods(URL_EXPERIENCES, HttpMethod.POST);
    }

    // experiences POST

    @Test
    public void testCreateExperience_notAuthorized() throws Exception {
        ExperienceDto experienceDto = new ExperienceDto();

        mockMvc.perform(post(URL_EXPERIENCES)
                .content(this.json(experienceDto))
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreateExperience_nameAbsent_badRequestMediumRequired() throws Exception {
        ExperienceDto experienceDto = creatExperienceToSave(null);

        int experienceCountBefore = experienceRepository.findAll().size();

        mockMvc.perform(post(URL_EXPERIENCES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceDto))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE));

        int experienceCountAfter = experienceRepository.findAll().size();
        assertEquals(experienceCountBefore, experienceCountAfter);
    }

    // helper methods

    private ExperienceDto creatExperienceToSave(String name) {
        ExperienceDto experience = new ExperienceDto();
        experience.setName(name);
        return experience;
    }
}