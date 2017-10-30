package culturelog.rest.exception;

/**
 * @author Jan Venstermans
 */
public enum CulturLogControllerExceptionKey {
    MEDIA_CREATE("media.create"),
    MEDIA_CREATE_WITH_ID("media.create.withId"),
    MEDIUM_GET_ONE_UNKNOWN_ID("media.getOne.unknownId"),
    MEDIUM_UPDATE_ONE("media.updateOne");

    private final String key;

    CulturLogControllerExceptionKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}