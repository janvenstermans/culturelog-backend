package culturelog.rest.config;

import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Locale;

/**
 * Determine which locale is currently being used, based on
 * 1) the session,
 * 2) the Accept-Language header (only the one with the highest value is retrieved)
 * 3) or a fixed value.
 * (what with cookies?)
 *
 * @author Jan Venstermans
 */
public class CultureLogSessionLocaleResolver extends SessionLocaleResolver {
    public Locale resolveLocale(HttpServletRequest request) {
        Locale locale = (Locale) WebUtils.getSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME);
        if (locale != null) {
            return locale;
        }
        locale = getLocaleFromAcceptLanguageHeader(request);
        if (locale != null) {
            return locale;
        }
        return determineDefaultLocale(request);
    }

    public Locale getLocaleFromAcceptLanguageHeader(HttpServletRequest request) {
        String acceptLanguageTag = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
        if (acceptLanguageTag == null || acceptLanguageTag.isEmpty()) {
            return null;
        }
        Enumeration<Locale> locales = request.getLocales();
        if (locales != null && locales.hasMoreElements()) {
            return locales.nextElement();
        }
        return null;
    }
}
