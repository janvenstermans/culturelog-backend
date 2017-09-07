package culturelog.rest.controller;

import culturelog.rest.CulturelogRestApplication;
import culturelog.rest.configuration.CultureLogTestConfiguration;
import culturelog.rest.domain.Location;
import culturelog.rest.dto.LocationDto;
import culturelog.rest.repository.LocationRepository;
import culturelog.rest.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for {@link LocationController}.
 *
 * @author Jan Venstermans
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CulturelogRestApplication.class)
@WebAppConfiguration
public class LocationControllerTest extends ControllerTestAbstract {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    private static final String URL_LOCATIONS = "/locations";

    // url /locations OPTIONS

    @Test
    public void testLocationsUrl_allowedMethods() throws Exception {
        testUrlAllowedMethods(URL_LOCATIONS, HttpMethod.POST, HttpMethod.GET);
    }

    // url /locations POST

    @Test
    public void testCreateLocation_nameNew() throws Exception {
        List<Location> locationListBefore = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(0, locationListBefore.size());

        String locatieName = "locatieName1";
        LocationDto locationDto = new LocationDto();
        locationDto.setName(locatieName);

        mockMvc.perform(post(URL_LOCATIONS)
                .with(httpBasic(CultureLogTestConfiguration.USER1,CultureLogTestConfiguration.USER1_PASS))
                .content(this.json(locationDto))
                .contentType(contentType))
                .andExpect(status().isCreated());

        List<Location> locationList = locationRepository.findByUserId(CultureLogTestConfiguration.getUser1Id());
        Assert.assertEquals(1, locationList.size());
        Location locationSaved = locationList.get(0);
        Assert.assertEquals(locatieName, locationSaved.getName());
    }

    //TODO: test
    // without locationName
    // when locationName-userName combo exists;
    // same locationName for a different user: ok
    // test description info is saved

    // url /locations GET
}
