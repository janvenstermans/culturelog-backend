package culturelog.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import culturelog.backend.controller.ControllerTestAbstract;
import culturelog.backend.controller.LocationController;
import culturelog.backend.domain.DisplayDateType;
import culturelog.backend.dto.DateMomentDto;
import culturelog.backend.dto.DisplayDateDto;
import culturelog.backend.dto.LocationDto;
import culturelog.backend.dto.MomentDto;
import culturelog.backend.utils.DisplayDateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Test class for {@link LocationController}.
 *
 * @author Jan Venstermans
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CultureLogBackendApplication.class)
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
