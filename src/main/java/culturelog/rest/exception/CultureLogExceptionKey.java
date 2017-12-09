package culturelog.rest.exception;

/**
 * @author Jan Venstermans
 */
public enum CultureLogExceptionKey {
    CREATE_WITH_ID("createWithId"),

    MEDIUM_NEEDS_NAME_ATTRIBUTE("medium.nameRequired"),
    MEDIUM_WITH_NAME_FOR_USER_ALREADY_EXISTS("mediumWithNameForUserAlreadyExists"),

    LOCATION_NEEDS_NAME_ATTRIBUTE("location.nameRequired"),
    LOCATION_WITH_NAME_FOR_USER_ALREADY_EXISTS("locationWithNameForUserAlreadyExists"),

    EXPERIENCE_NEEDS_NAME_ATTRIBUTE("experience.nameRequired"),
    EXPERIENCE_NEEDS_TYPE_ATTRIBUTE("experience.typeRequired"),
    EXPERIENCE_NEEDS_EXISTING_TYPE_ATTRIBUTE_FOR_USER("experience.typeRequired.existingForUser"),
    EXPERIENCE_OPTIONAL_LOCATION_ATTRIBUTE_ID_REQUIRED("experience.locationOptional.idRequired"),
    EXPERIENCE_OPTIONAL_LOCATION_NOT_FOUND_FOR_USER("experience.locationOptional.notExistingForUser"),

    USER_NULL("user.null"),
    USER_NOT_ENOUGH_DATA("user.notEnoughData"),
    USERNAME_IN_USE("userNameInUse"),
    USERNAME_MUST_BE_EMAIL("userNameMustBeEmail"),
    USERSAVE_CONTRAINT_VIOLATION("userSave.contraintViolation"); // to show something in case of a ConstraintViolationException

    private final String key;

    CultureLogExceptionKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}