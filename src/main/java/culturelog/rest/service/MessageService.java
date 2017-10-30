package culturelog.rest.service;

import culturelog.rest.exception.CulturLogControllerExceptionKey;
import culturelog.rest.exception.CultureLogException;
import culturelog.rest.exception.CultureLogExceptionKey;

import java.util.Locale;

/**
 * @author Jan Venstermans
 */
public interface MessageService {

    String getControllerMessage(CulturLogControllerExceptionKey controllerExceptionKey, Locale locale);
    String getControllerMessage(CulturLogControllerExceptionKey controllerExceptionKey, CultureLogException e, Locale locale);
    String getControllerMessage(CulturLogControllerExceptionKey controllerExceptionKey, Object[] objects, Locale locale);
    String getControllerMessage(CulturLogControllerExceptionKey controllerExceptionKey, Object[] objects, CultureLogException e, Locale locale);

    String getMessage(CultureLogExceptionKey key, Object[] objects, Locale locale);
}
