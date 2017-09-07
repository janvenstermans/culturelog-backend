package culturelog.rest.controller;

import culturelog.rest.CulturelogRestApplication;
import culturelog.rest.configuration.CultureLogTestConfiguration;
import culturelog.rest.domain.Medium;
import culturelog.rest.dto.MediumDto;
import culturelog.rest.repository.MediumRepository;
import culturelog.rest.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for {@link MediumController}.
 *
 * @author Jan Venstermans
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CulturelogRestApplication.class)
@WebAppConfiguration
@Transactional // db changes in one test are rolled back after test
public class MediumControllerTest extends ControllerTestAbstract {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MediumRepository mediumRepository;

    private static final String URL_MEDIA = "/media";

    // url /media OPTIONS

    @Test
    public void testMediaUrl_allowedMethods() throws Exception {
        testUrlAllowedMethods(URL_MEDIA, HttpMethod.POST, HttpMethod.GET);
    }

    // url /media POST

    @Test
    public void testCreateMedium_nameNew() throws Exception {
        List<Medium> mediumListBefore = mediumRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, mediumListBefore.size());

        String mediumName = "mediumName1";
        MediumDto mediumDto = new MediumDto();
        mediumDto.setName(mediumName);

        mockMvc.perform(post(URL_MEDIA)
                .with(httpBasic(CultureLogTestConfiguration.USER1, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(mediumDto))
                .contentType(contentType))
                .andExpect(status().isCreated());

        List<Medium> mediumList = mediumRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(1, mediumList.size());
        Medium mediumSaved = mediumList.get(0);
        Assert.assertEquals(mediumName, mediumSaved.getName());
    }

    @Test
    public void testCreateMedium_nameAlreadyExistsForUser() throws Exception {
        List<Medium> mediumListBefore = mediumRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, mediumListBefore.size());

        String mediumName = "mediumName1";
        MediumDto mediumDto = new MediumDto();
        mediumDto.setName(mediumName);

        mockMvc.perform(post(URL_MEDIA)
                .with(httpBasic(CultureLogTestConfiguration.USER1, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(mediumDto))
                .contentType(contentType))
                .andExpect(status().isCreated());

        mockMvc.perform(post(URL_MEDIA)
                .with(httpBasic(CultureLogTestConfiguration.USER1, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(mediumDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest());

        List<Medium> mediumList = mediumRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(1, mediumList.size());
        Medium mediumSaved = mediumList.get(0);
        Assert.assertEquals(mediumName, mediumSaved.getName());
    }

    //TODO: test
    // without mediumName
    // test description info is saved

    // url /media GET

    // with global media
}
