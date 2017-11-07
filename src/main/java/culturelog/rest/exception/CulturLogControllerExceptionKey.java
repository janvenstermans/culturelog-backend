package culturelog.rest.exception;

/**
 * @author Jan Venstermans
 */
public enum CulturLogControllerExceptionKey {
    USERS_CREATE("users.create"),

    MEDIA_CREATE("media.create"),
    MEDIA_CREATE_WITH_ID("media.create.withId"),
    MEDIA_GET_ONE_UNKNOWN_ID("media.getOne.unknownId"),
    MEDIA_UPDATE_ONE("media.updateOne"),

    LOCATIONS_CREATE("locations.create"),
    LOCATIONS_CREATE_WITH_ID("locations.create.withId"),
    LOCATIONS_GET_ONE_UNKNOWN_ID("locations.getOne.unknownId"),
    LOCATIONS_UPDATE_ONE("locations.updateOne");

    private final String key;

    CulturLogControllerExceptionKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}