package culturelog.rest.exception;

/**
 * @author Jan Venstermans
 */
public enum CulturLogControllerExceptionKey {
    USERS_CREATE("users.create"),

    MEDIA_CREATE("media.create"),
    MEDIA_GET_ONE_UNKNOWN_ID("media.getOne.unknownId"),
    MEDIA_UPDATE_ONE("media.updateOne"),

    LOCATIONS_CREATE("locations.create"),
    LOCATIONS_GET_ONE_UNKNOWN_ID("locations.getOne.unknownId"),
    LOCATIONS_UPDATE_ONE("locations.updateOne"),

    EXPERIENCES_CREATE("experiences.create")
    ;

    private final String key;

    CulturLogControllerExceptionKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}