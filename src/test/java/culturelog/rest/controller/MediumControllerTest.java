package culturelog.rest.controller;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import culturelog.rest.CulturelogRestApplication;
import culturelog.rest.configuration.CultureLogTestConfiguration;
import culturelog.rest.domain.Medium;
import culturelog.rest.domain.User;
import culturelog.rest.dto.MediumDto;
import culturelog.rest.repository.MediumRepository;
import culturelog.rest.repository.UserRepository;
import net.minidev.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    private static final String URL_MEDIA_ONE = "/media/%d";

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
//                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        ReadContext ctx = JsonPath.parse(result.getResponse().getContentAsString());
        // test all media id's are present
        assertIdList(expectedIdList, ctx.read("$.[*].id"));
        for (Long expectedId : expectedIdList) {
            assertMedium(mediumRepository.findOne(expectedId), ctx.read(String.format("$.[?(@.id==%d)]", expectedId)));
        }
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

        MvcResult result = mockMvc.perform(get(URL_MEDIA)
                .with(httpBasic(CultureLogTestConfiguration.USER1, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedIdList.size())))
//                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        ReadContext ctx = JsonPath.parse(result.getResponse().getContentAsString());
        // test all media id's are present
        assertIdList(expectedIdList, ctx.read("$.[*].id"));
        for (Long expectedId : expectedIdList) {
            assertMedium(mediumRepository.findOne(expectedId), ctx.read(String.format("$.[?(@.id==%d)]", expectedId)));
        }
    }

    // url /media/{mediumId} OPTIONS

    @Test
    public void testMediaOneUrl_allowedMethods() throws Exception {
        testUrlAllowedMethods(String.format(URL_MEDIA_ONE, CultureLogTestConfiguration.getGlobalMediumIdBook()), HttpMethod.GET, HttpMethod.PUT);
    }

    // url /media/{mediumId} GET

    @Test
    public void testGetMediaOne_notAuthorized() throws Exception {
        mockMvc.perform(get(String.format(URL_MEDIA_ONE, CultureLogTestConfiguration.getGlobalMediumIdBook())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetMediaOne_canGetGlobalMedium() throws Exception {
        Long mediumId = CultureLogTestConfiguration.getGlobalMediumIdBook();

        MvcResult result = mockMvc.perform(get(String.format(URL_MEDIA_ONE, mediumId))
                .with(httpBasic(CultureLogTestConfiguration.USER1, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isOk())
                .andReturn();

        ReadContext ctx = JsonPath.parse(result.getResponse().getContentAsString());
        assertMediumJson(mediumRepository.findOne(mediumId), ctx.read("$"));
    }

    @Test
    public void testGetMediaOne_canGetOwnMedium() throws Exception {
        User user1 = userRepository.findOne(CultureLogTestConfiguration.getUser1Id());
        Long mediumId = mediumRepository.save(createMediumToSave("testOne", user1)).getId();

        MvcResult result = mockMvc.perform(get(String.format(URL_MEDIA_ONE, mediumId))
                .with(httpBasic(CultureLogTestConfiguration.USER1, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isOk())
                .andReturn();

        ReadContext ctx = JsonPath.parse(result.getResponse().getContentAsString());
        assertMediumJson(mediumRepository.findOne(mediumId), ctx.read("$"));
    }

    @Test
    public void testGetMediaOne_cannotGetOtherUsersMedium() throws Exception {
        User user2 = userRepository.findOne(CultureLogTestConfiguration.getUser2Id());
        Long mediumId = mediumRepository.save(createMediumToSave("testTwo", user2)).getId();

        mockMvc.perform(get(String.format(URL_MEDIA_ONE, mediumId))
                .with(httpBasic(CultureLogTestConfiguration.USER1, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetMediaOne_cannotGetNonExistingMedium() throws Exception {
        Long mediumId = 1504648460L;
        Assert.assertNull(mediumRepository.findOne(mediumId));

        mockMvc.perform(get(String.format(URL_MEDIA_ONE, mediumId))
                .with(httpBasic(CultureLogTestConfiguration.USER1, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isBadRequest());
    }

    // url /media/{mediumId} PUT

    @Test
    public void testPutMediaOne_notAuthorized() throws Exception {
        MediumDto mediumDto = new MediumDto();

        mockMvc.perform(put(String.format(URL_MEDIA_ONE, CultureLogTestConfiguration.getGlobalMediumIdBook()))
                .content(this.json(mediumDto))
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testPutMediaOne_cannotEditGlobalMedium() throws Exception {
        Long mediumId = CultureLogTestConfiguration.getGlobalMediumIdBook();

        MediumDto mediumDto = new MediumDto();
        mediumDto.setId(mediumId);

        mockMvc.perform(put(String.format(URL_MEDIA_ONE, mediumId))
                .with(httpBasic(CultureLogTestConfiguration.USER1, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(mediumDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPutMediaOne_canEditOwnMedium() throws Exception {
        User user1 = userRepository.findOne(CultureLogTestConfiguration.getUser1Id());
        Medium medium1 = mediumRepository.save(createMediumToSave("testOne", user1));
        Long mediumId = medium1.getId();

        MediumDto mediumDto = new MediumDto();
        mediumDto.setId(mediumId);
        mediumDto.setName(medium1.getName() + "Edited");
        mediumDto.setDescription((medium1.getDescription() != null ? medium1.getDescription() : "") + "Edited");

        MvcResult result = mockMvc.perform(put(String.format(URL_MEDIA_ONE, mediumId))
                .with(httpBasic(CultureLogTestConfiguration.USER1, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(mediumDto))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

        Medium mediumAfterwards = mediumRepository.findOne(mediumId);
        ReadContext ctx = JsonPath.parse(result.getResponse().getContentAsString());
        assertMediumJson(mediumAfterwards, ctx.read("$"));
        Assert.assertEquals(mediumDto.getName(), mediumAfterwards.getName());
        Assert.assertEquals(mediumDto.getDescription(), mediumAfterwards.getDescription());
    }

    @Test
    public void testPutMediaOne_canEditOwnMedium_mediumDtoIdDoesNotMatter() throws Exception {
        User user1 = userRepository.findOne(CultureLogTestConfiguration.getUser1Id());
        Medium medium1 = mediumRepository.save(createMediumToSave("testOne", user1));
        Long mediumId = medium1.getId();

        MediumDto mediumDto = new MediumDto();
        mediumDto.setName(medium1.getName() + "Edited");
        mediumDto.setDescription((medium1.getDescription() != null ? medium1.getDescription() : "") + "Edited");

        MvcResult result = mockMvc.perform(put(String.format(URL_MEDIA_ONE, mediumId))
                .with(httpBasic(CultureLogTestConfiguration.USER1, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(mediumDto))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

        Medium mediumAfterwards = mediumRepository.findOne(mediumId);
        ReadContext ctx = JsonPath.parse(result.getResponse().getContentAsString());
        assertMediumJson(mediumAfterwards, ctx.read("$"));
        Assert.assertEquals(mediumDto.getName(), mediumAfterwards.getName());
        Assert.assertEquals(mediumDto.getDescription(), mediumAfterwards.getDescription());
    }

    @Test
    public void testPutMediaOne_cannotEditOwnMediumSoThatNameIsSameAsExistingOwnMedium() throws Exception {
        User user1 = userRepository.findOne(CultureLogTestConfiguration.getUser1Id());
        Medium medium1 = mediumRepository.save(createMediumToSave("testOne", user1));
        Medium medium2 = mediumRepository.save(createMediumToSave("testTwo", user1));
        Long mediumId = medium1.getId();

        MediumDto mediumDto = new MediumDto();
        mediumDto.setId(mediumId);
        mediumDto.setName(medium2.getName());
        mediumDto.setDescription((medium1.getDescription() != null ? medium1.getDescription() : "") + "Edited");

        mockMvc.perform(put(String.format(URL_MEDIA_ONE, mediumId))
                .with(httpBasic(CultureLogTestConfiguration.USER1, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(mediumDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest());

        Medium mediumAfterwards = mediumRepository.findOne(mediumId);
        Assert.assertEquals(medium1.getName(), mediumAfterwards.getName());
        Assert.assertEquals(medium1.getDescription(), mediumAfterwards.getDescription());
        Assert.assertNotEquals(mediumDto.getName(), mediumAfterwards.getName());
        Assert.assertNotEquals(mediumDto.getDescription(), mediumAfterwards.getDescription());
    }

    @Test
    public void testPutMediaOne_cannotEditOtherUsersMedium() throws Exception {
        User user2 = userRepository.findOne(CultureLogTestConfiguration.getUser2Id());
        Long mediumId = mediumRepository.save(createMediumToSave("testTwo", user2)).getId();

        MediumDto mediumDto = new MediumDto();
        mediumDto.setId(mediumId);

        mockMvc.perform(put(String.format(URL_MEDIA_ONE, mediumId))
                .with(httpBasic(CultureLogTestConfiguration.USER1, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(mediumDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPutMediaOne_cannotEditNonExistingMedium() throws Exception {
        Long mediumId = 1504648460L;
        Assert.assertNull(mediumRepository.findOne(mediumId));

        MediumDto mediumDto = new MediumDto();
        mediumDto.setId(mediumId);

        mockMvc.perform(put(String.format(URL_MEDIA_ONE, mediumId))
                .with(httpBasic(CultureLogTestConfiguration.USER1, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(mediumDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    // helper methods

    private Medium createMediumToSave(String name, User user) {
        Medium medium = new Medium();
        medium.setName(name);
        medium.setUser(user);
        return medium;
    }

    private void assertIdList(List<Long> expectedIdList, JSONArray jsonPathResult) {
        Assert.assertEquals(expectedIdList.size(), jsonPathResult.size());
        for (int i = 0; i < jsonPathResult.size(); i++) {
            Number value = (Number) jsonPathResult.get(i);
            Assert.assertTrue(expectedIdList.contains(value.longValue()));
        }
    }

    private void assertMedium(Medium medium, JSONArray jsonPathResult) {
        Assert.assertNotNull(medium);
        Assert.assertNotNull(jsonPathResult);
        Assert.assertEquals(1, jsonPathResult.size());
        Map<String, Object> mediumJson = (Map<String, Object>) jsonPathResult.get(0);
        assertMediumJson(medium, mediumJson);
    }

    private void assertMediumJson(Medium medium, Map<String, Object> mediumJson) {
        Assert.assertNotNull(medium);
        Assert.assertNotNull(mediumJson);
        Assert.assertEquals(medium.getId().longValue(), ((Number) mediumJson.get("id")).longValue());
        Assert.assertEquals(medium.getName(), mediumJson.get("name"));
        Assert.assertEquals(medium.getDescription(), mediumJson.get("description"));
        Assert.assertEquals(medium.getUser() == null, mediumJson.get("global"));
    }
}
