package culturelog.rest.controller;

import culturelog.rest.CulturelogRestApplication;
import culturelog.rest.configuration.CultureLogTestConfiguration;
import culturelog.rest.domain.Medium;
import culturelog.rest.domain.User;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    public void testCreateMedium_notAuthorized() throws Exception {
        MediumDto mediumDto = new MediumDto();

        mockMvc.perform(post(URL_MEDIA)
                .content(this.json(mediumDto))
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreateMedium_withId() throws Exception {
        List<Medium> mediumListBefore = mediumRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, mediumListBefore.size());

        MediumDto mediumDto = new MediumDto();
        mediumDto.setId(123L);

        mockMvc.perform(post(URL_MEDIA)
                .with(httpBasic(CultureLogTestConfiguration.USER1, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(mediumDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest());

        List<Medium> mediumList = mediumRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, mediumList.size());
    }

    @Test
    public void testCreateMedium_noName() throws Exception {
        List<Medium> mediumListBefore = mediumRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, mediumListBefore.size());

        MediumDto mediumDto = new MediumDto();

        mockMvc.perform(post(URL_MEDIA)
                .with(httpBasic(CultureLogTestConfiguration.USER1, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(mediumDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest());

        List<Medium> mediumList = mediumRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, mediumList.size());
    }

    @Test
    public void testCreateMedium_nameNew_minimum() throws Exception {
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

    @Test
    public void testCreateMedium_nameNew_allFields() throws Exception {
        List<Medium> mediumListBefore = mediumRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, mediumListBefore.size());

        String mediumName = "mediumName1";
        String description = "description5";
        MediumDto mediumDto = new MediumDto();
        mediumDto.setName(mediumName);
        mediumDto.setGlobal(true);
        mediumDto.setDescription(description);

        mockMvc.perform(post(URL_MEDIA)
                .with(httpBasic(CultureLogTestConfiguration.USER1, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(mediumDto))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(mediumName)))
                .andExpect(jsonPath("$.description", is(description)))
                .andExpect(jsonPath("$.global", is(false)));

        List<Medium> mediumList = mediumRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(1, mediumList.size());
        Medium mediumSaved = mediumList.get(0);
        Assert.assertEquals(mediumName, mediumSaved.getName());
        Assert.assertEquals(description, mediumSaved.getDescription());
        Assert.assertEquals(description, mediumSaved.getDescription());
        Assert.assertNotNull(mediumSaved.getId());
    }

    @Test
    public void testCreateMedium_nameNew_sameAsGlobalAllowed() throws Exception {
        List<Medium> mediumListBefore = mediumRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, mediumListBefore.size());

        String mediumName = CultureLogTestConfiguration.GLOBAL_MEDIUM_NAME_BOOK;
        MediumDto mediumDto = new MediumDto();
        mediumDto.setName(mediumName);

        Assert.assertEquals(1, mediumRepository.findByName(mediumName).size());

        mockMvc.perform(post(URL_MEDIA)
                .with(httpBasic(CultureLogTestConfiguration.USER1, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(mediumDto))
                .contentType(contentType))
                .andExpect(status().isCreated());

        List<Medium> mediumList = mediumRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(1, mediumList.size());
        Medium mediumSaved = mediumList.get(0);
        Assert.assertEquals(mediumName, mediumSaved.getName());

        Assert.assertEquals(2, mediumRepository.findByName(mediumName).size());
    }

    // url /media GET

    @Test
    public void testGetMedia_notAuthorized() throws Exception {
        mockMvc.perform(get(URL_MEDIA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetMedia_noOwnMedia() throws Exception {
        List<Medium> mediumListUser = mediumRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, mediumListUser.size());
        List<Medium> generalMedia = mediumRepository.findByUserId(null);
        List<Long> expectedIdList = generalMedia.stream().map(medium -> medium.getId()).collect(Collectors.toList());

        MvcResult result = mockMvc.perform(get(URL_MEDIA)
                .with(httpBasic(CultureLogTestConfiguration.USER1, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedIdList.size())))
//                .andExpect(jsonPath("$.[*].id", containsInAnyOrder(expectedIdList))).andReturn();;
                .andExpect(jsonPath("$.[*].id", hasSize(expectedIdList.size()))).andReturn();;
        result.getResponse();
    }

    @Test
    public void testGetMedia_withOwnMedia() throws Exception {
        User user1 = userRepository.findOne(CultureLogTestConfiguration.getUser1Id());
        mediumRepository.save(createMediumToSave("testOne", user1));
        mediumRepository.save(createMediumToSave("testTwo", user1));
        mediumRepository.save(createMediumToSave("testThree", user1));
        List<Medium> mediumListUser = mediumRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(3, mediumListUser.size());
        List<Medium> generalMedia = mediumRepository.findByUserId(null);
        List<Long> expectedIdList = Stream.concat(mediumListUser.stream(), generalMedia.stream()).map(medium -> medium.getId()).collect(Collectors.toList());

        mockMvc.perform(get(URL_MEDIA)
                .with(httpBasic(CultureLogTestConfiguration.USER1, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedIdList.size())))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(expectedIdList)));
    }

    private Medium createMediumToSave(String name, User user) {
        Medium medium = new Medium();
        medium.setName(name);
        medium.setUser(user);
        return medium;
    }
}
