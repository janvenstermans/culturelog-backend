package culturelog.backend.controller;

import org.junit.Assert;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utils class for Controller tests.
 *
 * @author Jan Venstermans
 */
public class ControllerTestUtils {

    // don't check because result of OPTIONS call or extension of other.
    private static final List<HttpMethod> httpMethodnoCheckList;
    private static final List<HttpMethod> httpMethodListToCheck;

    static {
        httpMethodnoCheckList = Arrays.asList(HttpMethod.OPTIONS, HttpMethod.HEAD, HttpMethod.PATCH);
        httpMethodListToCheck = new ArrayList<HttpMethod>(Arrays.asList(HttpMethod.values()));
        httpMethodListToCheck.removeAll(httpMethodnoCheckList);
    }

    private ControllerTestUtils() {
    }

    public static final String ALLOW_HEADER  = "Allow";

    public static void assertOptionsResult(MvcResult mvcResult, HttpMethod...expectedAllowed) {
        String allowHeaderValue = mvcResult.getResponse().getHeader(ALLOW_HEADER);
        List<HttpMethod> expectedAllowedList = Arrays.asList(expectedAllowed);
        expectedAllowedList.removeAll(httpMethodnoCheckList);
        List<String> allowedMethodList = Arrays.asList(allowHeaderValue.split(","));
        for (HttpMethod httpMethod : httpMethodListToCheck) {
            boolean result = allowedMethodList.contains(httpMethod.name());
            if (expectedAllowedList.contains(httpMethod)) {
                Assert.assertTrue(result);
            } else {
                Assert.assertFalse(result);
            }
        }
    }
}