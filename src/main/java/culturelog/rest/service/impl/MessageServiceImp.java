package culturelog.rest.service.impl;

import culturelog.rest.exception.CulturLogControllerExceptionKey;
import culturelog.rest.exception.CultureLogException;
import culturelog.rest.exception.CultureLogExceptionKey;
import culturelog.rest.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * @author Jan Venstermans
 */
@Service
public class MessageServiceImp implements MessageService {

    @Autowired
    private MessageSource cultureLogControllerMessages;

    @Autowired
    private MessageSource cultureLogMessages;

    // controller messages

    @Override
    public String getControllerMessage(CulturLogControllerExceptionKey controllerExceptionKey, Locale locale) {
        return getControllerMessageInternal(controllerExceptionKey.getKey(), new Object[0], locale);
    }

    @Override
    public String getControllerMessage(CulturLogControllerExceptionKey controllerExceptionKey, CultureLogException e, Locale locale) {
        return getControllerMessage(controllerExceptionKey, new Object[0], e, locale);
    }

    @Override
    public String getControllerMessage(CulturLogControllerExceptionKey controllerExceptionKey, Object[] objects, Locale locale) {
        return getControllerMessage(controllerExceptionKey, objects, null, locale);
    }

    @Override
    public String getControllerMessage(CulturLogControllerExceptionKey controllerExceptionKey, Object[] objects, CultureLogException e, Locale locale) {
        String main = getControllerMessageInternal(controllerExceptionKey.getKey(), objects, locale);
        if (e == null) {
            return main;
        }
        return main + ": " + getMessage(e.getKey(), e.getObjects(), locale);
    }

    // messages

    @Override
    public String getMessage(CultureLogExceptionKey key, Object[] objects, Locale locale) {
        return cultureLogMessages.getMessage(key.getKey(), objects != null ? objects : new Object[]{}, locale);
    }

    // helper methods

    private String getControllerMessageInternal(String key, Object[] objects, Locale locale) {
        return cultureLogControllerMessages.getMessage(key, objects, locale);
    }
}
