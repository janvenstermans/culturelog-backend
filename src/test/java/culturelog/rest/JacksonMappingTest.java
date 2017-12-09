package culturelog.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import culturelog.rest.configuration.CultureLogTestConfiguration;
import culturelog.rest.controller.ControllerTestAbstract;
import culturelog.rest.controller.LocationController;
import culturelog.rest.domain.DisplayDateType;
import culturelog.rest.domain.Location;
import culturelog.rest.domain.User;
import culturelog.rest.dto.DateMomentDto;
import culturelog.rest.dto.DisplayDateDto;
import culturelog.rest.dto.LocationDto;
import culturelog.rest.dto.MomentDto;
import culturelog.rest.repository.LocationRepository;
import culturelog.rest.repository.UserRepository;
import culturelog.rest.utils.DisplayDateUtils;
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

import java.util.Date;
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
 * Test class for {@link LocationController}.
 *
 * @author Jan Venstermans
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CulturelogRestApplication.class)
@WebAppConfiguration
public class JacksonMappingTest extends ControllerTestAbstract {

    @Autowired
    private ObjectMapper objectMapper; // this is the application-configured objectMapper

    @Test
    public void testLocationDtoMapping() throws Exception {
        LocationDto locationDto = new LocationDto();
        locationDto.setId(159L);
        locationDto.setName("my place");
        locationDto.setDescription("what a place");
        locationDto.setGlobal(true);

        String json = objectMapper.writeValueAsString(locationDto);

        LocationDto deserilialized = objectMapper.readValue(json, LocationDto.class);

        // assert serialization-deserialization leads to same object
        Assert.assertEquals(locationDto.getId(), deserilialized.getId());
        Assert.assertEquals(locationDto.getName(), deserilialized.getName());
        Assert.assertEquals(locationDto.getDescription(), deserilialized.getDescription());
        Assert.assertEquals(locationDto.isGlobal(), deserilialized.isGlobal());
    }

    @Test
    public void testDateMomentDtoMapping() throws Exception {
        DateMomentDto dateMomentDto = new DateMomentDto();
        dateMomentDto.setId(856L);
        DisplayDateDto displayDateDto = DisplayDateUtils.toDisplayDateDto(DisplayDateUtils.createDisplayDate(DisplayDateType.DATE_TIME, new Date(159)));
        displayDateDto.setId(9565L);
        dateMomentDto.setDisplayDate(displayDateDto);

        String json = objectMapper.writeValueAsString(dateMomentDto);

        MomentDto deserilialized = objectMapper.readValue(json, MomentDto.class);

        // assert serialization-deserialization leads to same object
        DateMomentDto result = (DateMomentDto) deserilialized;
        Assert.assertEquals(dateMomentDto.getId(), result.getId());
        Assert.assertEquals(dateMomentDto.getMomentType(), result.getMomentType());
        DisplayDateDto resultDisplayDate = result.getDisplayDate();
        Assert.assertNotNull(resultDisplayDate);
        Assert.assertEquals(displayDateDto.getId(), resultDisplayDate.getId());
        Assert.assertEquals(displayDateDto.getType(), resultDisplayDate.getType());
        Assert.assertEquals(displayDateDto.getDate().getTime(), resultDisplayDate.getDate().getTime());
    }
}
