package culturelog.rest.controller;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import culturelog.rest.CulturelogRestApplication;
import culturelog.rest.configuration.CultureLogTestConfiguration;
import culturelog.rest.domain.Location;
import culturelog.rest.domain.User;
import culturelog.rest.dto.LocationDto;
import culturelog.rest.repository.LocationRepository;
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
 * Test class for {@link LocationController}.
 *
 * @author Jan Venstermans
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CulturelogRestApplication.class)
@WebAppConfiguration
@Transactional // db changes in one test are rolled back after test
public class LocationControllerTest extends ControllerTestAbstract {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    private static final String URL_LOCATIONS = "/locations";
    private static final String URL_LOCATIONS_ONE = "/locations/%d";

    // url /locations OPTIONS

    @Test
    public void testLocationsUrl_allowedMethods() throws Exception {
        testUrlAllowedMethods(URL_LOCATIONS, HttpMethod.POST, HttpMethod.GET);
    }

    // url /locations POST

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
        List<Location> locationListBefore = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, locationListBefore.size());

        LocationDto locationDto = new LocationDto();
        locationDto.setId(123L);

        mockMvc.perform(post(URL_LOCATIONS)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest());

        List<Location> locationList = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, locationList.size());
    }

    @Test
    public void testCreateLocation_noName() throws Exception {
        List<Location> locationListBefore = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, locationListBefore.size());

        LocationDto locationDto = new LocationDto();

        mockMvc.perform(post(URL_LOCATIONS)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isBadRequest());

        List<Location> locationList = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, locationList.size());
    }

    @Test
    public void testCreateLocation_nameNew_minimum() throws Exception {
        List<Location> locationListBefore = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, locationListBefore.size());

        String locationName = "locationName1";
        LocationDto locationDto = new LocationDto();
        locationDto.setName(locationName);

        mockMvc.perform(post(URL_LOCATIONS)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isCreated());

        List<Location> locationList = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(1, locationList.size());
        Location locationSaved = locationList.get(0);
        Assert.assertEquals(locationName, locationSaved.getName());
    }

    @Test
    public void testCreateLocation_nameAlreadyExistsForUser() throws Exception {
        List<Location> locationListBefore = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
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
                .andExpect(status().isBadRequest());

        List<Location> locationList = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(1, locationList.size());
        Location locationSaved = locationList.get(0);
        Assert.assertEquals(locationName, locationSaved.getName());
    }

    @Test
    public void testCreateLocation_nameNew_allFields() throws Exception {
        List<Location> locationListBefore = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
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

        List<Location> locationList = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(1, locationList.size());
        Location locationSaved = locationList.get(0);
        Assert.assertEquals(locationName, locationSaved.getName());
        Assert.assertEquals(description, locationSaved.getDescription());
        Assert.assertEquals(description, locationSaved.getDescription());
        Assert.assertNotNull(locationSaved.getId());
    }

    @Test
    public void testCreateLocation_nameNew_sameAsGlobalAllowed() throws Exception {
        List<Location> locationListBefore = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, locationListBefore.size());

        String locationName = CultureLogTestConfiguration.GLOBAL_LOCATION_NAME_VOORUIT;
        LocationDto locationDto = new LocationDto();
        locationDto.setName(locationName);

        Assert.assertEquals(1, locationRepository.findByName(locationName).size());

        mockMvc.perform(post(URL_LOCATIONS)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isCreated());

        List<Location> locationList = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(1, locationList.size());
        Location locationSaved = locationList.get(0);
        Assert.assertEquals(locationName, locationSaved.getName());

        Assert.assertEquals(2, locationRepository.findByName(locationName).size());
    }

    // url /locations GET

    @Test
    public void testGetLocations_notAuthorized() throws Exception {
        mockMvc.perform(get(URL_LOCATIONS))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetLocations_noOwnLocations() throws Exception {
        List<Location> locationListUser = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, locationListUser.size());
        List<Location> generalLocations = locationRepository.findByUserId(null);
        List<Long> expectedIdList = generalLocations.stream().map(location -> location.getId()).collect(Collectors.toList());

        MvcResult result = mockMvc.perform(get(URL_LOCATIONS)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedIdList.size())))
//                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        ReadContext ctx = JsonPath.parse(result.getResponse().getContentAsString());
        // test all locations id's are present
        assertIdList(expectedIdList, ctx.read("$.[*].id"));
        for (Long expectedId : expectedIdList) {
            assertLocation(locationRepository.findOne(expectedId), ctx.read(String.format("$.[?(@.id==%d)]", expectedId)));
        }
    }

    @Test
    public void testGetLocations_withOwnLocations() throws Exception {
        User user1 = userRepository.findOne(CultureLogTestConfiguration.getUser1Id());
        locationRepository.save(createLocationToSave("testOne", user1));
        locationRepository.save(createLocationToSave("testTwo", user1));
        locationRepository.save(createLocationToSave("testThree", user1));
        List<Location> locationListUser = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(3, locationListUser.size());
        List<Location> generalLocations = locationRepository.findByUserId(null);
        List<Long> expectedIdList = Stream.concat(locationListUser.stream(), generalLocations.stream()).map(location -> location.getId()).collect(Collectors.toList());

        MvcResult result = mockMvc.perform(get(URL_LOCATIONS)
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedIdList.size())))
//                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        ReadContext ctx = JsonPath.parse(result.getResponse().getContentAsString());
        // test all locations id's are present
        assertIdList(expectedIdList, ctx.read("$.[*].id"));
        for (Long expectedId : expectedIdList) {
            assertLocation(locationRepository.findOne(expectedId), ctx.read(String.format("$.[?(@.id==%d)]", expectedId)));
        }
    }

    // url /locations/{locationId} OPTIONS

    @Test
    public void testLocationsOneUrl_allowedMethods() throws Exception {
        testUrlAllowedMethods(String.format(URL_LOCATIONS_ONE, CultureLogTestConfiguration.getGlobalLocationIdVooruit()), HttpMethod.GET, HttpMethod.PUT);
    }

    // url /locations/{locationId} GET

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
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetLocationsOne_cannotGetNonExistingLocation() throws Exception {
        Long locationId = 1504648460L;
        Assert.assertNull(locationRepository.findOne(locationId));

        mockMvc.perform(get(String.format(URL_LOCATIONS_ONE, locationId))
                .with(httpBasic(CultureLogTestConfiguration.USER1_NAME, CultureLogTestConfiguration.USER1_PASS)))
                .andExpect(status().isBadRequest());
    }

    // url /locations/{locationId} PUT

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
                .andExpect(status().isBadRequest());
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
                .andExpect(status().isBadRequest());

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
                .andExpect(status().isBadRequest());
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
                .andExpect(status().isBadRequest());
    }

    // helper methods

    private Location createLocationToSave(String name, User user) {
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
}
