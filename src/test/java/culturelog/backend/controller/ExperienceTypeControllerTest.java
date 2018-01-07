package culturelog.backend.controller;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import culturelog.backend.CultureLogBackendApplication;
import culturelog.backend.configuration.CultureLogTestConfiguration;
import culturelog.backend.domain.ExperienceType;
import culturelog.backend.domain.User;
import culturelog.backend.dto.ExperienceTypeDto;
import culturelog.backend.repository.ExperienceTypeRepository;
import culturelog.backend.repository.UserRepository;
import net.minidev.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for {@link ExperienceTypeController}.
 *
 * @author Jan Venstermans
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CultureLogBackendApplication.class)
@WebAppConfiguration
@Transactional // db changes in one test are rolled back after test
public class ExperienceTypeControllerTest extends ControllerTestAbstract {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExperienceTypeRepository experienceTypeRepository;

    private static final String URL_EXPERIENCE_TYPES = "/experienceTypes";
    private static final String URL_EXPERIENCE_TYPES_ONE = "/experienceTypes/%d";

    // url /experienceTypes OPTIONS

    @Test
    public void testExperienceTypesUrl_allowedMethods() throws Exception {
        testUrlAllowedMethods(URL_EXPERIENCE_TYPES, HttpMethod.POST, HttpMethod.GET);
    }

    // url /experienceTypes POST

    @Test
    public void testCreateExperienceType_notAuthorized() throws Exception {
        ExperienceTypeDto experienceTypeDto = new ExperienceTypeDto();

        mockMvc.perform(post(URL_EXPERIENCE_TYPES)
                .content(this.json(experienceTypeDto))
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreateExperienceType_withId() throws Exception {
        List<ExperienceType> experienceTypeListBefore = experienceTypeRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, experienceTypeListBefore.size());

        ExperienceTypeDto experienceTypeDto = new ExperienceTypeDto();
        experienceTypeDto.setId(123L);

        mockMvc.perform(post(URL_EXPERIENCE_TYPES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceTypeDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());

        List<ExperienceType> experienceTypeList = experienceTypeRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, experienceTypeList.size());
    }

    @Test
    public void testCreateExperienceType_noName() throws Exception {
        List<ExperienceType> experienceTypeListBefore = experienceTypeRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, experienceTypeListBefore.size());

        ExperienceTypeDto experienceTypeDto = new ExperienceTypeDto();

        mockMvc.perform(post(URL_EXPERIENCE_TYPES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceTypeDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());

        List<ExperienceType> experienceTypeList = experienceTypeRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, experienceTypeList.size());
    }

    @Test
    public void testCreateExperienceType_nameNew_minimum() throws Exception {
        List<ExperienceType> experienceTypeListBefore = experienceTypeRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, experienceTypeListBefore.size());

        String experienceTypeName = "experienceTypeName1";
        ExperienceTypeDto experienceTypeDto = new ExperienceTypeDto();
        experienceTypeDto.setName(experienceTypeName);

        mockMvc.perform(post(URL_EXPERIENCE_TYPES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceTypeDto))
                .contentType(contentType))
                .andExpect(status().isCreated());

        List<ExperienceType> experienceTypeList = experienceTypeRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(1, experienceTypeList.size());
        ExperienceType experienceTypeSaved = experienceTypeList.get(0);
        Assert.assertEquals(experienceTypeName, experienceTypeSaved.getName());
    }

    @Test
    public void testCreateExperienceType_nameAlreadyExistsForUser() throws Exception {
        List<ExperienceType> experienceTypeListBefore = experienceTypeRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, experienceTypeListBefore.size());

        String experienceTypeName = "experienceTypeName1";
        ExperienceTypeDto experienceTypeDto = new ExperienceTypeDto();
        experienceTypeDto.setName(experienceTypeName);

        mockMvc.perform(post(URL_EXPERIENCE_TYPES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceTypeDto))
                .contentType(contentType))
                .andExpect(status().isCreated());

        mockMvc.perform(post(URL_EXPERIENCE_TYPES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceTypeDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());

        List<ExperienceType> experienceTypeList = experienceTypeRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(1, experienceTypeList.size());
        ExperienceType experienceTypeSaved = experienceTypeList.get(0);
        Assert.assertEquals(experienceTypeName, experienceTypeSaved.getName());
    }

    @Test
    public void testCreateExperienceType_nameNew_allFields() throws Exception {
        List<ExperienceType> experienceTypeListBefore = experienceTypeRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, experienceTypeListBefore.size());

        String experienceTypeName = "experienceTypeName1";
        String description = "description5";
        ExperienceTypeDto experienceTypeDto = new ExperienceTypeDto();
        experienceTypeDto.setName(experienceTypeName);
        experienceTypeDto.setGlobal(true);
        experienceTypeDto.setDescription(description);

        mockMvc.perform(post(URL_EXPERIENCE_TYPES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceTypeDto))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(experienceTypeName)))
                .andExpect(jsonPath("$.description", is(description)))
                .andExpect(jsonPath("$.global", is(false)));

        List<ExperienceType> experienceTypeList = experienceTypeRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(1, experienceTypeList.size());
        ExperienceType experienceTypeSaved = experienceTypeList.get(0);
        Assert.assertEquals(experienceTypeName, experienceTypeSaved.getName());
        Assert.assertEquals(description, experienceTypeSaved.getDescription());
        Assert.assertEquals(description, experienceTypeSaved.getDescription());
        Assert.assertNotNull(experienceTypeSaved.getId());
    }

    @Test
    public void testCreateExperienceType_nameNew_sameAsGlobalAllowed() throws Exception {
        List<ExperienceType> experienceTypeListBefore = experienceTypeRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, experienceTypeListBefore.size());

        String experienceTypeName = CultureLogTestConfiguration.GLOBAL_EXPERIENCETYPE_NAME_BOOK;
        ExperienceTypeDto experienceTypeDto = new ExperienceTypeDto();
        experienceTypeDto.setName(experienceTypeName);

        Assert.assertEquals(1, experienceTypeRepository.findByName(experienceTypeName).size());

        mockMvc.perform(post(URL_EXPERIENCE_TYPES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceTypeDto))
                .contentType(contentType))
                .andExpect(status().isCreated());

        List<ExperienceType> experienceTypeList = experienceTypeRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(1, experienceTypeList.size());
        ExperienceType experienceTypeSaved = experienceTypeList.get(0);
        Assert.assertEquals(experienceTypeName, experienceTypeSaved.getName());

        Assert.assertEquals(2, experienceTypeRepository.findByName(experienceTypeName).size());
    }

    // url /experienceTypes GET

    @Test
    public void testGetExperienceTypes_notAuthorized() throws Exception {
        mockMvc.perform(get(URL_EXPERIENCE_TYPES))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetExperienceTypes_noOwnExperienceTypes() throws Exception {
        List<ExperienceType> experienceTypeListUser = experienceTypeRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, experienceTypeListUser.size());
        List<ExperienceType> generalExperienceTypes = experienceTypeRepository.findByUserId(null);
        List<Long> expectedIdList = generalExperienceTypes.stream().map(experienceType -> experienceType.getId()).collect(Collectors.toList());

        MvcResult result = mockMvc.perform(get(URL_EXPERIENCE_TYPES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedIdList.size())))
//                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        ReadContext ctx = JsonPath.parse(result.getResponse().getContentAsString());
        // test all experienceTypes id's are present
        assertIdList(expectedIdList, ctx.read("$.[*].id"));
        for (Long expectedId : expectedIdList) {
            assertExperienceType(experienceTypeRepository.findOne(expectedId), ctx.read(String.format("$.[?(@.id==%d)]", expectedId)));
        }
    }

    @Test
    public void testGetExperienceTypes_withOwnExperienceTypes() throws Exception {
        User user1 = userRepository.findOne(CultureLogTestConfiguration.getUser1Id());
        experienceTypeRepository.save(createExperienceTypeToSave("testOne", user1));
        experienceTypeRepository.save(createExperienceTypeToSave("testTwo", user1));
        experienceTypeRepository.save(createExperienceTypeToSave("testThree", user1));
        List<ExperienceType> experienceTypeListUser = experienceTypeRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(3, experienceTypeListUser.size());
        List<ExperienceType> generalExperienceTypes = experienceTypeRepository.findByUserId(null);
        List<Long> expectedIdList = Stream.concat(experienceTypeListUser.stream(), generalExperienceTypes.stream()).map(experienceType -> experienceType.getId()).collect(Collectors.toList());

        MvcResult result = mockMvc.perform(get(URL_EXPERIENCE_TYPES)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedIdList.size())))
//                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        ReadContext ctx = JsonPath.parse(result.getResponse().getContentAsString());
        // test all experienceTypes id's are present
        assertIdList(expectedIdList, ctx.read("$.[*].id"));
        for (Long expectedId : expectedIdList) {
            assertExperienceType(experienceTypeRepository.findOne(expectedId), ctx.read(String.format("$.[?(@.id==%d)]", expectedId)));
        }
    }

    // url /experienceTypes/{experienceTypeId} OPTIONS

    @Test
    public void testExperienceTypesOneUrl_allowedMethods() throws Exception {
        testUrlAllowedMethods(String.format(URL_EXPERIENCE_TYPES_ONE, CultureLogTestConfiguration.getGlobalExperienceTypeIdBook()), HttpMethod.GET, HttpMethod.PUT);
    }

    // url /experienceTypes/{experienceTypeId} GET

    @Test
    public void testGetExperienceTypesOne_notAuthorized() throws Exception {
        mockMvc.perform(get(String.format(URL_EXPERIENCE_TYPES_ONE, CultureLogTestConfiguration.getGlobalExperienceTypeIdBook())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetExperienceTypesOne_canGetGlobalExperienceType() throws Exception {
        Long experienceTypeId = CultureLogTestConfiguration.getGlobalExperienceTypeIdBook();

        MvcResult result = mockMvc.perform(get(String.format(URL_EXPERIENCE_TYPES_ONE, experienceTypeId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isOk())
                .andReturn();

        ReadContext ctx = JsonPath.parse(result.getResponse().getContentAsString());
        assertExperienceTypeJson(experienceTypeRepository.findOne(experienceTypeId), ctx.read("$"));
    }

    @Test
    public void testGetExperienceTypesOne_canGetOwnExperienceType() throws Exception {
        User user1 = userRepository.findOne(CultureLogTestConfiguration.getUser1Id());
        Long experienceTypeId = experienceTypeRepository.save(createExperienceTypeToSave("testOne", user1)).getId();

        MvcResult result = mockMvc.perform(get(String.format(URL_EXPERIENCE_TYPES_ONE, experienceTypeId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isOk())
                .andReturn();

        ReadContext ctx = JsonPath.parse(result.getResponse().getContentAsString());
        assertExperienceTypeJson(experienceTypeRepository.findOne(experienceTypeId), ctx.read("$"));
    }

    @Test
    public void testGetExperienceTypesOne_cannotGetOtherUsersExperienceType() throws Exception {
        User user2 = userRepository.findOne(CultureLogTestConfiguration.getUser2Id());
        Long experienceTypeId = experienceTypeRepository.save(createExperienceTypeToSave("testTwo", user2)).getId();

        mockMvc.perform(get(String.format(URL_EXPERIENCE_TYPES_ONE, experienceTypeId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());
    }

    @Test
    public void testGetExperienceTypesOne_cannotGetNonExistingExperienceType() throws Exception {
        Long experienceTypeId = 1504648460L;
        Assert.assertNull(experienceTypeRepository.findOne(experienceTypeId));

        mockMvc.perform(get(String.format(URL_EXPERIENCE_TYPES_ONE, experienceTypeId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());
    }

    // url /experienceTypes/{experienceTypeId} PUT

    @Test
    public void testPutExperienceTypesOne_notAuthorized() throws Exception {
        ExperienceTypeDto experienceTypeDto = new ExperienceTypeDto();

        mockMvc.perform(put(String.format(URL_EXPERIENCE_TYPES_ONE, CultureLogTestConfiguration.getGlobalExperienceTypeIdBook()))
                .content(this.json(experienceTypeDto))
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testPutExperienceTypesOne_cannotEditGlobalExperienceType() throws Exception {
        Long experienceTypeId = CultureLogTestConfiguration.getGlobalExperienceTypeIdBook();

        ExperienceTypeDto experienceTypeDto = new ExperienceTypeDto();
        experienceTypeDto.setId(experienceTypeId);

        mockMvc.perform(put(String.format(URL_EXPERIENCE_TYPES_ONE, experienceTypeId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceTypeDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());

    }

    @Test
    public void testPutExperienceTypesOne_canEditOwnExperienceType() throws Exception {
        User user1 = userRepository.findOne(CultureLogTestConfiguration.getUser1Id());
        ExperienceType experienceType1 = experienceTypeRepository.save(createExperienceTypeToSave("testOne", user1));
        Long experienceTypeId = experienceType1.getId();

        ExperienceTypeDto experienceTypeDto = new ExperienceTypeDto();
        experienceTypeDto.setId(experienceTypeId);
        experienceTypeDto.setName(experienceType1.getName() + "Edited");
        experienceTypeDto.setDescription((experienceType1.getDescription() != null ? experienceType1.getDescription() : "") + "Edited");

        MvcResult result = mockMvc.perform(put(String.format(URL_EXPERIENCE_TYPES_ONE, experienceTypeId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceTypeDto))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

        ExperienceType experienceTypeAfterwards = experienceTypeRepository.findOne(experienceTypeId);
        ReadContext ctx = JsonPath.parse(result.getResponse().getContentAsString());
        assertExperienceTypeJson(experienceTypeAfterwards, ctx.read("$"));
        Assert.assertEquals(experienceTypeDto.getName(), experienceTypeAfterwards.getName());
        Assert.assertEquals(experienceTypeDto.getDescription(), experienceTypeAfterwards.getDescription());
    }

    @Test
    public void testPutExperienceTypesOne_canEditOwnExperienceType_experienceTypeDtoIdDoesNotMatter() throws Exception {
        User user1 = userRepository.findOne(CultureLogTestConfiguration.getUser1Id());
        ExperienceType experienceType1 = experienceTypeRepository.save(createExperienceTypeToSave("testOne", user1));
        Long experienceTypeId = experienceType1.getId();

        ExperienceTypeDto experienceTypeDto = new ExperienceTypeDto();
        experienceTypeDto.setName(experienceType1.getName() + "Edited");
        experienceTypeDto.setDescription((experienceType1.getDescription() != null ? experienceType1.getDescription() : "") + "Edited");

        MvcResult result = mockMvc.perform(put(String.format(URL_EXPERIENCE_TYPES_ONE, experienceTypeId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceTypeDto))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

        ExperienceType experienceTypeAfterwards = experienceTypeRepository.findOne(experienceTypeId);
        ReadContext ctx = JsonPath.parse(result.getResponse().getContentAsString());
        assertExperienceTypeJson(experienceTypeAfterwards, ctx.read("$"));
        Assert.assertEquals(experienceTypeDto.getName(), experienceTypeAfterwards.getName());
        Assert.assertEquals(experienceTypeDto.getDescription(), experienceTypeAfterwards.getDescription());
    }

    @Test
    public void testPutExperienceTypesOne_cannotEditOwnExperienceTypeSoThatNameIsSameAsExistingOwnExperienceType() throws Exception {
        User user1 = userRepository.findOne(CultureLogTestConfiguration.getUser1Id());
        ExperienceType experienceType1 = experienceTypeRepository.save(createExperienceTypeToSave("testOne", user1));
        ExperienceType experienceType2 = experienceTypeRepository.save(createExperienceTypeToSave("testTwo", user1));
        Long experienceTypeId = experienceType1.getId();

        ExperienceTypeDto experienceTypeDto = new ExperienceTypeDto();
        experienceTypeDto.setId(experienceTypeId);
        experienceTypeDto.setName(experienceType2.getName());
        experienceTypeDto.setDescription((experienceType1.getDescription() != null ? experienceType1.getDescription() : "") + "Edited");

        mockMvc.perform(put(String.format(URL_EXPERIENCE_TYPES_ONE, experienceTypeId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceTypeDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());

        ExperienceType experienceTypeAfterwards = experienceTypeRepository.findOne(experienceTypeId);
        Assert.assertEquals(experienceType1.getName(), experienceTypeAfterwards.getName());
        Assert.assertEquals(experienceType1.getDescription(), experienceTypeAfterwards.getDescription());
        Assert.assertNotEquals(experienceTypeDto.getName(), experienceTypeAfterwards.getName());
        Assert.assertNotEquals(experienceTypeDto.getDescription(), experienceTypeAfterwards.getDescription());
    }

    @Test
    public void testPutExperienceTypesOne_cannotEditOwnExperienceTypeSoThatNameIsNull() throws Exception {
        User user1 = userRepository.findOne(CultureLogTestConfiguration.getUser1Id());
        ExperienceType experienceType1 = experienceTypeRepository.save(createExperienceTypeToSave("testOne", user1));
        Long experienceTypeId = experienceType1.getId();

        ExperienceTypeDto experienceTypeDto = new ExperienceTypeDto();
        experienceTypeDto.setId(experienceTypeId);
        experienceTypeDto.setName(null);
        experienceTypeDto.setDescription((experienceType1.getDescription() != null ? experienceType1.getDescription() : "") + "Edited");

        mockMvc.perform(put(String.format(URL_EXPERIENCE_TYPES_ONE, experienceTypeId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceTypeDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());

        ExperienceType experienceTypeAfterwards = experienceTypeRepository.findOne(experienceTypeId);
        Assert.assertEquals(experienceType1.getName(), experienceTypeAfterwards.getName());
        Assert.assertEquals(experienceType1.getDescription(), experienceTypeAfterwards.getDescription());
        Assert.assertNotEquals(experienceTypeDto.getName(), experienceTypeAfterwards.getName());
        Assert.assertNotEquals(experienceTypeDto.getDescription(), experienceTypeAfterwards.getDescription());
    }

    @Test
    public void testPutExperienceTypesOne_cannotEditOtherUsersExperienceType() throws Exception {
        User user2 = userRepository.findOne(CultureLogTestConfiguration.getUser2Id());
        Long experienceTypeId = experienceTypeRepository.save(createExperienceTypeToSave("testTwo", user2)).getId();

        ExperienceTypeDto experienceTypeDto = new ExperienceTypeDto();
        experienceTypeDto.setId(experienceTypeId);

        mockMvc.perform(put(String.format(URL_EXPERIENCE_TYPES_ONE, experienceTypeId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceTypeDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());
    }

    @Test
    public void testPutExperienceTypesOne_cannotEditNonExistingExperienceType() throws Exception {
        Long experienceTypeId = 1504648460L;
        Assert.assertNull(experienceTypeRepository.findOne(experienceTypeId));

        ExperienceTypeDto experienceTypeDto = new ExperienceTypeDto();
        experienceTypeDto.setId(experienceTypeId);

        mockMvc.perform(put(String.format(URL_EXPERIENCE_TYPES_ONE, experienceTypeId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(experienceTypeDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());
    }

    // helper methods

    public static ExperienceType createExperienceTypeToSave(String name, User user) {
        ExperienceType experienceType = new ExperienceType();
        experienceType.setName(name);
        experienceType.setUser(user);
        return experienceType;
    }

    private void assertExperienceType(ExperienceType experienceType, JSONArray jsonPathResult) {
        Assert.assertNotNull(experienceType);
        Assert.assertNotNull(jsonPathResult);
        Assert.assertEquals(1, jsonPathResult.size());
        Map<String, Object> experienceTypeJson = (Map<String, Object>) jsonPathResult.get(0);
        assertExperienceTypeJson(experienceType, experienceTypeJson);
    }

    private void assertExperienceTypeJson(ExperienceType experienceType, Map<String, Object> experienceTypeJson) {
        Assert.assertNotNull(experienceType);
        Assert.assertNotNull(experienceTypeJson);
        Assert.assertEquals(experienceType.getId().longValue(), ((Number) experienceTypeJson.get("id")).longValue());
        Assert.assertEquals(experienceType.getName(), experienceTypeJson.get("name"));
        Assert.assertEquals(experienceType.getDescription(), experienceTypeJson.get("description"));
        Assert.assertEquals(experienceType.getUser() == null, experienceTypeJson.get("global"));
    }
}
