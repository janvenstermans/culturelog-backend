package culturelog.rest.service;

/**
 * @author Jan Venstermans
 */
public class Utils {

    private Utils() {
    }

    public static boolean isNullOrEmpty(String input) {
        return input == null || input.isEmpty();
    }
}
