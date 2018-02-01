package culturelog.backend.controller;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import culturelog.backend.CultureLogBackendApplication;
import culturelog.backend.configuration.CultureLogTestConfiguration;
import culturelog.backend.domain.Location;
import culturelog.backend.domain.User;
import culturelog.backend.dto.LocationDto;
import culturelog.backend.exception.CultureLogException;
import culturelog.backend.repository.LocationRepository;
import culturelog.backend.repository.UserRepository;
import culturelog.backend.service.LocationService;
import net.minidev.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
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
 * Test class for {@link LocationController}.
 *
 * @author Jan Venstermans
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CultureLogBackendApplication.class)
@WebAppConfiguration
@Transactional // db changes in one test are rolled back after test
public class LocationControllerTest extends ControllerTestAbstract {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private LocationService locationService;

    private static final String URL_LOCATIONS = "/locations";
    private static final String URL_LOCATIONS_ONE = "/locations/%d";

    // url /locations OPTIONS

    @Test
    public void testLocationsUrl_allowedMethods() throws Exception {
        testUrlAllowedMethods(URL_LOCATIONS, HttpMethod.POST, HttpMethod.GET);
    }

    // -----------------------------------------
    // url /locations POST
    // -----------------------------------------

    @Test
    public void testCreateLocation_notAuthorized() throws Exception {
        LocationDto locationDto = new LocationDto();

        mockMvc.perform(post(URL_LOCATIONS)
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreateLocation_withId() throws Exception {
        List<Location> locationListBefore = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id(), new PageRequest(0, 20)).getContent();
        Assert.assertEquals(0, locationListBefore.size());

        LocationDto locationDto = new LocationDto();
        locationDto.setId(123L);

        mockMvc.perform(post(URL_LOCATIONS)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());

        List<Location> locationList = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id(), new PageRequest(0, 20)).getContent();
        Assert.assertEquals(0, locationList.size());
    }

    @Test
    public void testCreateLocation_noName() throws Exception {
        List<Location> locationListBefore = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id(), new PageRequest(0, 20)).getContent();
        Assert.assertEquals(0, locationListBefore.size());

        LocationDto locationDto = new LocationDto();

        mockMvc.perform(post(URL_LOCATIONS)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());

        List<Location> locationList = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id(), new PageRequest(0, 20)).getContent();
        Assert.assertEquals(0, locationList.size());
    }

    @Test
    public void testCreateLocation_nameNew_minimum() throws Exception {
        List<Location> locationListBefore = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id(), new PageRequest(0, 20)).getContent();
        Assert.assertEquals(0, locationListBefore.size());

        String locationName = "locationName1";
        LocationDto locationDto = new LocationDto();
        locationDto.setName(locationName);

        mockMvc.perform(post(URL_LOCATIONS)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isCreated());

        List<Location> locationList = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id(), new PageRequest(0, 20)).getContent();
        Assert.assertEquals(1, locationList.size());
        Location locationSaved = locationList.get(0);
        Assert.assertEquals(locationName, locationSaved.getName());
    }

    @Test
    public void testCreateLocation_nameAlreadyExistsForUser() throws Exception {
        List<Location> locationListBefore = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id(), new PageRequest(0, 20)).getContent();
        Assert.assertEquals(0, locationListBefore.size());

        String locationName = "locationName1";
        LocationDto locationDto = new LocationDto();
        locationDto.setName(locationName);

        mockMvc.perform(post(URL_LOCATIONS)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isCreated());

        mockMvc.perform(post(URL_LOCATIONS)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());

        List<Location> locationList = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id(), new PageRequest(0, 20)).getContent();
        Assert.assertEquals(1, locationList.size());
        Location locationSaved = locationList.get(0);
        Assert.assertEquals(locationName, locationSaved.getName());
    }

    @Test
    public void testCreateLocation_nameNew_allFields() throws Exception {
        List<Location> locationListBefore = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id(), new PageRequest(0, 20)).getContent();
        Assert.assertEquals(0, locationListBefore.size());

        String locationName = "locationName1";
        String description = "description5";
        LocationDto locationDto = new LocationDto();
        locationDto.setName(locationName);
        locationDto.setGlobal(true);
        locationDto.setDescription(description);

        mockMvc.perform(post(URL_LOCATIONS)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is(locationName)))
                .andExpect(jsonPath("$.description", is(description)))
                .andExpect(jsonPath("$.global", is(false)));

        List<Location> locationList = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id(), new PageRequest(0, 20)).getContent();
        Assert.assertEquals(1, locationList.size());
        Location locationSaved = locationList.get(0);
        Assert.assertEquals(locationName, locationSaved.getName());
        Assert.assertEquals(description, locationSaved.getDescription());
        Assert.assertEquals(description, locationSaved.getDescription());
        Assert.assertNotNull(locationSaved.getId());
    }

    @Test
    public void testCreateLocation_nameNew_sameAsGlobalAllowed() throws Exception {
        List<Location> locationListBefore = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id(), new PageRequest(0, 20)).getContent();
        Assert.assertEquals(0, locationListBefore.size());

        String locationName = CultureLogTestConfiguration.GLOBAL_LOCATION_NAME_VOORUIT;
        LocationDto locationDto = new LocationDto();
        locationDto.setName(locationName);

        Assert.assertEquals(1, locationRepository.findByName(locationName, new PageRequest(0, 20)).getContent().size());

        mockMvc.perform(post(URL_LOCATIONS)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isCreated());

        List<Location> locationList = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id(), new PageRequest(0, 20)).getContent();
        Assert.assertEquals(1, locationList.size());
        Location locationSaved = locationList.get(0);
        Assert.assertEquals(locationName, locationSaved.getName());

        Assert.assertEquals(2, locationRepository.findByName(locationName, new PageRequest(0, 20)).getContent().size());
    }

    // -----------------------------------------
    // url /locations?page=X&size=Y GET
    // -----------------------------------------

    @Test
    public void testGetLocations_notAuthorized() throws Exception {
        mockMvc.perform(get(URL_LOCATIONS))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetLocations_noOwnLocations() throws Exception {
        int page = 0;
        int size = 3;
        Pageable pageable = new PageRequest(page, size);
        Page<Location> locationListUser = locationRepository.findByUserIdIncludingGlobal(CultureLogTestConfiguration.getUser1Id(), pageable);
        Assert.assertEquals(2L, locationListUser.getTotalElements());
        List<Long> expectedIdList = locationListUser.getContent().stream().map(location -> location.getId()).collect(Collectors.toList());;

        executeAndAssertGetLocationsPage(page, size, null, false, expectedIdList, locationListUser);
    }

    @Test
    public void testGetLocations_defaultPagingInfo() throws Exception {
        String direction0 = LocationController.DEFAULT_SORT_ASC_0 ? Sort.Direction.ASC.name() : Sort.Direction.DESC.name();
        String direction1 = LocationController.DEFAULT_SORT_ASC_1 ? Sort.Direction.ASC.name() : Sort.Direction.DESC.name();
        mockMvc.perform(get(URL_LOCATIONS)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.number", equalTo(LocationController.DEFAULT_PAGE_NUMBER)))
                .andExpect(jsonPath("$.size", equalTo(LocationController.DEFAULT_PAGE_SIZE)))
                .andExpect(jsonPath("$.sort", hasSize(2)))
                .andExpect(jsonPath("$.sort[0].property", equalTo(LocationController.DEFAULT_SORT_COLUMN_0)))
                .andExpect(jsonPath("$.sort[0].direction", equalTo(direction0)))
                .andExpect(jsonPath("$.sort[0].ascending", equalTo(LocationController.DEFAULT_SORT_ASC_0)))
                .andExpect(jsonPath("$.sort[1].property", equalTo(LocationController.DEFAULT_SORT_COLUMN_1)))
                .andExpect(jsonPath("$.sort[1].direction", equalTo(direction1)))
                .andExpect(jsonPath("$.sort[1].ascending", equalTo(LocationController.DEFAULT_SORT_ASC_1)))
        ;
    }

    @Test
    public void testGetLocations_withOwnExperiences() throws Exception {
        List<Location> savedLocationsIncludingGlobal = createLocationsForUser(CultureLogTestConfiguration.getUser1Id());

        int pageSize = 3;
        String sort = null; //default
        Page<Location> locationPage0 = locationRepository.findByUserIdIncludingGlobal(CultureLogTestConfiguration.getUser1Id(), new PageRequest(0, pageSize));
        Page<Location> locationPage1 = locationRepository.findByUserIdIncludingGlobal(CultureLogTestConfiguration.getUser1Id(), new PageRequest(1, pageSize));

        //determine ids in pages
        List<Long> savedLocationIdsOrderedDefault = savedLocationsIncludingGlobal.stream().map(location -> location.getId()).collect(Collectors.toList());
        List<Long> locationIdListPage0 = savedLocationIdsOrderedDefault.subList(0, 3);
        List<Long> locationIdListPage1 = savedLocationIdsOrderedDefault.subList(3, 5);

        executeAndAssertGetLocationsPage(0, pageSize, sort, false, locationIdListPage0, locationPage0);
        executeAndAssertGetLocationsPage(1, pageSize, sort, false, locationIdListPage1, locationPage1);
    }

    @Test
    public void testGetLocations_withOwnExperiences_sortByLocationIdAsc() throws Exception {
        List<Location> savedLocationsIncludingGlobal = createLocationsForUser(CultureLogTestConfiguration.getUser1Id());

        int pageSize = 3;
        String sort = "id";
        Page<Location> locationPage0 = locationRepository.findByUserIdIncludingGlobal(CultureLogTestConfiguration.getUser1Id(), new PageRequest(0, pageSize));
        Page<Location> locationPage1 = locationRepository.findByUserIdIncludingGlobal(CultureLogTestConfiguration.getUser1Id(), new PageRequest(1, pageSize));

        //determine ids in pages
        List<Long> savedLocationIdsOrderedDefault = savedLocationsIncludingGlobal.stream().map(location -> location.getId()).collect(Collectors.toList());
        Collections.sort(savedLocationIdsOrderedDefault);
        List<Long> locationIdListPage0 = savedLocationIdsOrderedDefault.subList(0, 3);
        List<Long> locationIdListPage1 = savedLocationIdsOrderedDefault.subList(3, 5);

        executeAndAssertGetLocationsPage(0, pageSize, sort, false, locationIdListPage0, locationPage0);
        executeAndAssertGetLocationsPage(1, pageSize, sort, false, locationIdListPage1, locationPage1);
    }

    @Test
    public void testGetLocations_withOwnExperiences_sortByLocationIdDesc() throws Exception {
        List<Location> savedLocationsIncludingGlobal = createLocationsForUser(CultureLogTestConfiguration.getUser1Id());

        int pageSize = 3;
        String sort = "id";
        Page<Location> locationPage0 = locationRepository.findByUserIdIncludingGlobal(CultureLogTestConfiguration.getUser1Id(), new PageRequest(0, pageSize));
        Page<Location> locationPage1 = locationRepository.findByUserIdIncludingGlobal(CultureLogTestConfiguration.getUser1Id(), new PageRequest(1, pageSize));

        //determine ids in pages
        List<Long> savedLocationIdsOrderedDefault = savedLocationsIncludingGlobal.stream().map(location -> location.getId()).collect(Collectors.toList());
        Collections.sort(savedLocationIdsOrderedDefault);
        Collections.reverse(savedLocationIdsOrderedDefault);
        List<Long> locationIdListPage0 = savedLocationIdsOrderedDefault.subList(0, 3);
        List<Long> locationIdListPage1 = savedLocationIdsOrderedDefault.subList(3, 5);

        executeAndAssertGetLocationsPage(0, pageSize, sort, true, locationIdListPage0, locationPage0);
        executeAndAssertGetLocationsPage(1, pageSize, sort, true, locationIdListPage1, locationPage1);
    }

    // -----------------------------------------
    // url /locations/{locationId} OPTIONS
    // -----------------------------------------

    @Test
    public void testLocationsOneUrl_allowedMethods() throws Exception {
        testUrlAllowedMethods(String.format(URL_LOCATIONS_ONE, CultureLogTestConfiguration.getGlobalLocationIdVooruit()), HttpMethod.GET, HttpMethod.PUT);
    }

    // -----------------------------------------
    // url /locations/{locationId} GET
    // -----------------------------------------

    @Test
    public void testGetLocationsOne_notAuthorized() throws Exception {
        mockMvc.perform(get(String.format(URL_LOCATIONS_ONE, CultureLogTestConfiguration.getGlobalLocationIdVooruit())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetLocationsOne_canGetGlobalLocation() throws Exception {
        Long locationId = CultureLogTestConfiguration.getGlobalLocationIdVooruit();

        MvcResult result = mockMvc.perform(get(String.format(URL_LOCATIONS_ONE, locationId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isOk())
                .andReturn();

        ReadContext ctx = JsonPath.parse(result.getResponse().getContentAsString());
        assertLocationJson(locationRepository.findOne(locationId), ctx.read("$"));
    }

    @Test
    public void testGetLocationsOne_canGetOwnLocation() throws Exception {
        User user1 = userRepository.findOne(CultureLogTestConfiguration.getUser1Id());
        Long locationId = locationRepository.save(createLocationToSave("testOne", user1)).getId();

        MvcResult result = mockMvc.perform(get(String.format(URL_LOCATIONS_ONE, locationId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isOk())
                .andReturn();

        ReadContext ctx = JsonPath.parse(result.getResponse().getContentAsString());
        assertLocationJson(locationRepository.findOne(locationId), ctx.read("$"));
    }

    @Test
    public void testGetLocationsOne_cannotGetOtherUsersLocation() throws Exception {
        User user2 = userRepository.findOne(CultureLogTestConfiguration.getUser2Id());
        Long locationId = locationRepository.save(createLocationToSave("testTwo", user2)).getId();

        mockMvc.perform(get(String.format(URL_LOCATIONS_ONE, locationId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());
    }

    @Test
    public void testGetLocationsOne_cannotGetNonExistingLocation() throws Exception {
        Long locationId = 1504648460L;
        Assert.assertNull(locationRepository.findOne(locationId));

        mockMvc.perform(get(String.format(URL_LOCATIONS_ONE, locationId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());
    }

    // -----------------------------------------
    // url /locations/{locationId} PUT
    // -----------------------------------------

    @Test
    public void testPutLocationsOne_notAuthorized() throws Exception {
        LocationDto locationDto = new LocationDto();

        mockMvc.perform(put(String.format(URL_LOCATIONS_ONE, CultureLogTestConfiguration.getGlobalLocationIdVooruit()))
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testPutLocationsOne_cannotEditGlobalLocation() throws Exception {
        Long locationId = CultureLogTestConfiguration.getGlobalLocationIdKinepolis();

        LocationDto locationDto = new LocationDto();
        locationDto.setId(locationId);

        mockMvc.perform(put(String.format(URL_LOCATIONS_ONE, locationId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());
    }

    @Test
    public void testPutLocationsOne_canEditOwnLocation() throws Exception {
        User user1 = userRepository.findOne(CultureLogTestConfiguration.getUser1Id());
        Location location1 = locationRepository.save(createLocationToSave("testOne", user1));
        Long locationId = location1.getId();

        LocationDto locationDto = new LocationDto();
        locationDto.setId(locationId);
        locationDto.setName(location1.getName() + "Edited");
        locationDto.setDescription((location1.getDescription() != null ? location1.getDescription() : "") + "Edited");

        MvcResult result = mockMvc.perform(put(String.format(URL_LOCATIONS_ONE, locationId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

        Location locationAfterwards = locationRepository.findOne(locationId);
        ReadContext ctx = JsonPath.parse(result.getResponse().getContentAsString());
        assertLocationJson(locationAfterwards, ctx.read("$"));
        Assert.assertEquals(locationDto.getName(), locationAfterwards.getName());
        Assert.assertEquals(locationDto.getDescription(), locationAfterwards.getDescription());
    }

    @Test
    public void testPutLocationsOne_canEditOwnLocation_locationDtoIdDoesNotMatter() throws Exception {
        User user1 = userRepository.findOne(CultureLogTestConfiguration.getUser1Id());
        Location location1 = locationRepository.save(createLocationToSave("testOne", user1));
        Long locationId = location1.getId();

        LocationDto locationDto = new LocationDto();
        locationDto.setName(location1.getName() + "Edited");
        locationDto.setDescription((location1.getDescription() != null ? location1.getDescription() : "") + "Edited");

        MvcResult result = mockMvc.perform(put(String.format(URL_LOCATIONS_ONE, locationId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andReturn();

        Location locationAfterwards = locationRepository.findOne(locationId);
        ReadContext ctx = JsonPath.parse(result.getResponse().getContentAsString());
        assertLocationJson(locationAfterwards, ctx.read("$"));
        Assert.assertEquals(locationDto.getName(), locationAfterwards.getName());
        Assert.assertEquals(locationDto.getDescription(), locationAfterwards.getDescription());
    }

    @Test
    public void testPutLocationsOne_cannotEditOwnLocationSoThatNameIsSameAsExistingOwnLocation() throws Exception {
        User user1 = userRepository.findOne(CultureLogTestConfiguration.getUser1Id());
        Location location1 = locationRepository.save(createLocationToSave("testOne", user1));
        Location location2 = locationRepository.save(createLocationToSave("testTwo", user1));
        Long locationId = location1.getId();

        LocationDto locationDto = new LocationDto();
        locationDto.setId(locationId);
        locationDto.setName(location2.getName());
        locationDto.setDescription((location1.getDescription() != null ? location1.getDescription() : "") + "Edited");

        mockMvc.perform(put(String.format(URL_LOCATIONS_ONE, locationId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());

        Location locationAfterwards = locationRepository.findOne(locationId);
        Assert.assertEquals(location1.getName(), locationAfterwards.getName());
        Assert.assertEquals(location1.getDescription(), locationAfterwards.getDescription());
        Assert.assertNotEquals(locationDto.getName(), locationAfterwards.getName());
        Assert.assertNotEquals(locationDto.getDescription(), locationAfterwards.getDescription());
    }

    @Test
    public void testPutLocationsOne_cannotEditOwnLocationSoThatNameIsNull() throws Exception {
        User user1 = userRepository.findOne(CultureLogTestConfiguration.getUser1Id());
        Location location1 = locationRepository.save(createLocationToSave("testOne", user1));
        Long locationId = location1.getId();

        LocationDto locationDto = new LocationDto();
        locationDto.setId(locationId);
        locationDto.setName(null);
        locationDto.setDescription((location1.getDescription() != null ? location1.getDescription() : "") + "Edited");

        mockMvc.perform(put(String.format(URL_LOCATIONS_ONE, locationId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());

        Location locationAfterwards = locationRepository.findOne(locationId);
        Assert.assertEquals(location1.getName(), locationAfterwards.getName());
        Assert.assertEquals(location1.getDescription(), locationAfterwards.getDescription());
        Assert.assertNotEquals(locationDto.getName(), locationAfterwards.getName());
        Assert.assertNotEquals(locationDto.getDescription(), locationAfterwards.getDescription());
    }

    @Test
    public void testPutLocationsOne_cannotEditOtherUsersLocation() throws Exception {
        User user2 = userRepository.findOne(CultureLogTestConfiguration.getUser2Id());
        Long locationId = locationRepository.save(createLocationToSave("testTwo", user2)).getId();

        LocationDto locationDto = new LocationDto();
        locationDto.setId(locationId);

        mockMvc.perform(put(String.format(URL_LOCATIONS_ONE, locationId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());
    }

    @Test
    public void testPutLocationsOne_cannotEditNonExistingLocation() throws Exception {
        Long locationId = 1504648460L;
        Assert.assertNull(locationRepository.findOne(locationId));

        LocationDto locationDto = new LocationDto();
        locationDto.setId(locationId);

        mockMvc.perform(put(String.format(URL_LOCATIONS_ONE, locationId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print());
    }

    // helper methods

    public static Location createLocationToSave(String name, User user) {
        Location location = new Location();
        location.setName(name);
        location.setUser(user);
        return location;
    }

    private void assertLocation(Location location, JSONArray jsonPathResult) {
        Assert.assertNotNull(location);
        Assert.assertNotNull(jsonPathResult);
        Assert.assertEquals(1, jsonPathResult.size());
        Map<String, Object> locationJson = (Map<String, Object>) jsonPathResult.get(0);
        assertLocationJson(location, locationJson);
    }

    private void assertLocationJson(Location location, Map<String, Object> locationJson) {
        Assert.assertNotNull(location);
        Assert.assertNotNull(locationJson);
        Assert.assertEquals(location.getId().longValue(), ((Number) locationJson.get("id")).longValue());
        Assert.assertEquals(location.getName(), locationJson.get("name"));
        Assert.assertEquals(location.getDescription(), locationJson.get("description"));
        Assert.assertEquals(location.getUser() == null, locationJson.get("global"));
    }

    private void executeAndAssertGetLocationsPage(int page, int pageSize, String sort, boolean desc, List<Long> expectedIdList, Page pageInfoExpected) throws Exception {
        MvcResult resultPage = mockMvc.perform(get(getUrlPaged(URL_LOCATIONS, page, pageSize, sort, desc))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content", hasSize(expectedIdList.size())))
                //paging info
                .andExpect(jsonPath("$.totalElements", equalTo((int) pageInfoExpected.getTotalElements())))
                .andExpect(jsonPath("$.totalPages", equalTo(pageInfoExpected.getTotalPages())))
                .andExpect(jsonPath("$.first", equalTo(pageInfoExpected.isFirst())))
                .andExpect(jsonPath("$.last", equalTo(pageInfoExpected.isLast())))
                .andExpect(jsonPath("$.number", equalTo(pageInfoExpected.getNumber())))
                .andExpect(jsonPath("$.numberOfElements", equalTo(pageInfoExpected.getNumberOfElements())))
                .andExpect(jsonPath("$.size", equalTo(pageInfoExpected.getSize())))
                .andReturn();

        ReadContext ctx = JsonPath.parse(resultPage.getResponse().getContentAsString());
        // test all experience id's are present
        assertIdList(expectedIdList, ctx.read("$.content[*].id"));
        for (Long expectedId : expectedIdList) {
            assertLocation(locationRepository.findOne(expectedId), ctx.read(String.format("$.content.[?(@.id==%d)]", expectedId)));
        }
    }

    /**
     *
     * @param userId
     * @return ordered list, by name
     * @throws CultureLogException
     */
    private List<Location> createLocationsForUser(Long userId) throws CultureLogException {
        List<Location> savedLocations = new ArrayList<>();
        User user1 = userRepository.findOne(userId);
        savedLocations.add(locationService.getById(CultureLogTestConfiguration.getGlobalLocationIdKinepolis()));
        savedLocations.add(locationService.getById(CultureLogTestConfiguration.getGlobalLocationIdVooruit()));
        savedLocations.add(locationService.save(createLocationToSave("werk", user1)));
        savedLocations.add(locationService.save(createLocationToSave("thuis", user1)));
        savedLocations.add(locationService.save(createLocationToSave(savedLocations.get(0).getName(), user1)));
        Collections.sort(savedLocations, new Comparator<Location>() {
            @Override
            public int compare(Location location, Location t1) {
                int compareName = location.getName().compareTo(t1.getName());
                if (compareName != 0) {
                    return compareName;
                }
                return location.getId().compareTo(t1.getId());
            }
        });
        return savedLocations;
    }
}
