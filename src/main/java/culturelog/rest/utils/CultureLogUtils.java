package culturelog.rest.utils;


import org.apache.commons.validator.routines.EmailValidator;

/**
 * General utils class.
 * @author Jan Venstermans
 */
public class CultureLogUtils {

    private CultureLogUtils() {
    }

    public static boolean isNullOrEmpty(String input) {
        return input == null || input.isEmpty();
    }

    public static boolean isEmail(String input) {
        return EmailValidator.getInstance().isValid(input);
    }
}
