package culturelog.backend.controller;

import net.minidev.json.JSONArray;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Test class for {@link UserController}.
 *
 * @author Jan Venstermans
 */
public abstract class ControllerTestAbstract {

    public static MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    protected MockMvc mockMvc;

    protected HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setUp() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
    }

    //---------------------------
    // helper methods
    //---------------------------

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    public void testUrlAllowedMethods(String url, HttpMethod...expectedAllowed) throws Exception {
        MvcResult mvcResult = mockMvc.perform(options(url))
                .andExpect(status().isOk())
                .andReturn();

        ControllerTestUtils.assertOptionsResult(mvcResult, expectedAllowed);
    }

    protected static void assertIdList(List<Long> expectedIdList, JSONArray jsonPathResult) {
        Assert.assertEquals(expectedIdList.size(), jsonPathResult.size());
        for (int i = 0; i < jsonPathResult.size(); i++) {
            Number value = (Number) jsonPathResult.get(i);
            Assert.assertTrue(expectedIdList.contains(value.longValue()));
        }
    }

    protected static String getUrlPaged(String url, Integer page, Integer size, String sort, boolean desc) {
        if (page == null && size == null && sort == null) {
            return url;
        }
        StringBuilder stringBuilder = new StringBuilder(url).append('?');
        if (page != null) {
            stringBuilder.append("page=").append(page).append('&');
        }
        if (size != null) {
            stringBuilder.append("size=").append(size).append('&');
        }
        if (sort != null) {
            stringBuilder.append("sort=").append(sort);
            if (desc) {
                stringBuilder.append(",desc");
            }
            stringBuilder.append('&');
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}