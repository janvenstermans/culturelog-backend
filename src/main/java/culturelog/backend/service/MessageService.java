package culturelog.backend.service;

import culturelog.backend.exception.CultureLogControllerExceptionKey;
import culturelog.backend.exception.CultureLogException;
import culturelog.backend.exception.CultureLogExceptionKey;

import java.util.Locale;

/**
 * @author Jan Venstermans
 */
public interface MessageService {

    String getControllerMessage(CultureLogControllerExceptionKey controllerExceptionKey, Locale locale);
    String getControllerMessage(CultureLogControllerExceptionKey controllerExceptionKey, CultureLogException e, Locale locale);
    String getControllerMessage(CultureLogControllerExceptionKey controllerExceptionKey, Object[] objects, Locale locale);
    String getControllerMessage(CultureLogControllerExceptionKey controllerExceptionKey, Object[] objects, CultureLogException e, Locale locale);

    String getMessage(CultureLogExceptionKey key, Object[] objects, Locale locale);
}
