package culturelog.rest.configuration;

import culturelog.rest.config.CultureLogSessionLocaleResolver;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Test class for {@link CultureLogSessionLocaleResolver}.
 *
 * @author Jan Venstermans
 */
public class CultureLogSessionLocaleResolverTest {

    @Test
    public void noSessionNoHeader_takeDefaultValue() throws Exception {
        CultureLogSessionLocaleResolver localeResolver = new CultureLogSessionLocaleResolver();
        HttpServletRequest request = new MockHttpServletRequest();

        // test for default value Chinese
        Locale defaultLocale = Locale.CHINESE;
        localeResolver.setDefaultLocale(defaultLocale);

        Locale result = localeResolver.resolveLocale(request);

        Assert.assertEquals(defaultLocale, result);

        // test for default value Chinese
        Locale defaultLocale2 = Locale.ITALY;
        localeResolver.setDefaultLocale(defaultLocale2);

        Locale result2 = localeResolver.resolveLocale(request);

        Assert.assertEquals(defaultLocale2, result2);
    }

    @Test
    public void noSessionWithSingleHeader_returnHeader() throws Exception {
        CultureLogSessionLocaleResolver localeResolver = new CultureLogSessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.CANADA_FRENCH);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Locale expected = Locale.ITALY;
        setAcceptLanguageHeader(request, expected.toLanguageTag());

        Locale result = localeResolver.resolveLocale(request);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void noSessionWithMultipleHeader_returnHeaderWithHighestQ() throws Exception {
        CultureLogSessionLocaleResolver localeResolver = new CultureLogSessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.CANADA_FRENCH);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Locale expected = Locale.GERMAN;
        String headerValue = new StringBuilder()
                .append("nl-NL;q=0.1").append(',')
                .append("nl;q=0.8").append(',')
                .append(expected.toLanguageTag()).append(';').append("0.9").append(',')
                .append("en-US;q=0.6").append(',')
                .append("en;q=0.4")
                .toString();
        setAcceptLanguageHeader(request, headerValue);

        Locale result = localeResolver.resolveLocale(request);

        Assert.assertEquals(expected, result);
    }

    // TODO: test session of request

    private void setAcceptLanguageHeader(MockHttpServletRequest request, String headerValue) {
        request.addHeader(HttpHeaders.ACCEPT_LANGUAGE, headerValue);
        //Stupid that I have to set this, this should be in the MockHttpServletRequest object!!
        List<LocaleHelper> localeHelperList = toLocaleHelperList(headerValue);
        request.setPreferredLocales(localeHelperList.stream().map(localeHelper -> localeHelper.getLocale()).collect(Collectors.toList()));
    }

    /**
     *
     * @param headerValue
     * @return sorted list
     */
    private List<LocaleHelper> toLocaleHelperList(String headerValue) {
        String[] langArray = headerValue.split(",");
        List<LocaleHelper> localeHelperList = new ArrayList<>();
        for (String lang : langArray) {
            localeHelperList.add(new LocaleHelper(lang));
        }
        localeHelperList.sort(new Comparator<LocaleHelper>() {
            @Override
            public int compare(LocaleHelper o1, LocaleHelper o2) {
                // return the one with the HIGHEST q first, i.e. sort on q DESC
                float diff = o1.q - o2.q;
                if (diff > 0) {
                    return -1;
                }
                if (diff < 0) {
                    return 1;
                }
                return 0;
            }
        });
        return localeHelperList;
    }

    private static class LocaleHelper {
        private final Locale locale;
        private final float q;
        public LocaleHelper(String localeHeaderElement) {
            String[] split = localeHeaderElement.split(";");
            locale = Locale.forLanguageTag(split[0]);
            if (split.length == 1) {
                q = 1;
            } else {
                q = Float.valueOf(split[1].substring(split[1].indexOf('=') + 1));
            }
        }

        public Locale getLocale() {
            return locale;
        }

        public float getQ() {
            return q;
        }
    }
}
