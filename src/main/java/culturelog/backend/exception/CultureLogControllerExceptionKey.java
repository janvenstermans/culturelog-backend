package culturelog.backend.exception;

/**
 * @author Jan Venstermans
 */
public enum CultureLogControllerExceptionKey {
    USERS_CREATE("users.create"),

    EXPERIENCETYPES_CREATE("experienceTypes.create"),
    EXPERIENCETYPES_GET_ONE_UNKNOWN_ID("experienceTypes.getOne.unknownId"),
    EXPERIENCETYPES_UPDATE_ONE("experienceTypes.updateOne"),

    LOCATIONS_CREATE("locations.create"),
    LOCATIONS_GET_ONE_UNKNOWN_ID("locations.getOne.unknownId"),
    LOCATIONS_UPDATE_ONE("locations.updateOne"),

    EXPERIENCES_CREATE("experiences.create")
    ;

    private final String key;

    CultureLogControllerExceptionKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}