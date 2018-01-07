package culturelog.backend.configuration;

import culturelog.backend.domain.ExperienceType;
import culturelog.backend.domain.Location;
import culturelog.backend.dto.UserCreateDto;
import culturelog.backend.exception.CultureLogException;
import culturelog.backend.service.LocationService;
import culturelog.backend.service.ExperienceTypeService;
import culturelog.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 *  @author Jan Venstermans
 */
@Configuration
public class CultureLogTestConfiguration {

    @Autowired
    private UserService userService;

    @Autowired
    private ExperienceTypeService experienceTypeService;

    @Autowired
    private LocationService locationService;

    public static final String USER1_NAME = "a@b.cd";
    public static final String USER1_PASS = "password";
    private static Long USER1_ID;

    public static final String USER2_NAME = "b@b.cd";
    public static final String USER2_PASS = "password";
    private static Long USER2_ID;

    public static final String GLOBAL_EXPERIENCETYPE_NAME_FILM = "film";
    public static final String GLOBAL_EXPERIENCETYPE_NAME_BOOK = "boek";
    private static Long GLOBAL_EXPERIENCETYPE_ID_FILM;
    private static Long GLOBAL_EXPERIENCETYPE_ID_BOOK;

    public static final String GLOBAL_LOCATION_NAME_KINEPOLIS_GENT = "Kinepolis Gent";
    public static final String GLOBAL_LOCATION_NAME_VOORUIT = "Vooruit";
    private static Long GLOBAL_LOCATION_ID_KINEPOLIS_GENT;
    private static Long GLOBAL_LOCATION_ID_VOORUIT;

    @PostConstruct
    protected void fillDbWithTestData() throws CultureLogException {
        addUsers();
        addGlobalExperienceTypes();
        addGlobalLocations();
    }

    public static Long getUser1Id() {
        return USER1_ID;
    }

    public static Long getUser2Id() {
        return USER2_ID;
    }

    public static Long getGlobalExperienceTypeIdFilm() {
        return GLOBAL_EXPERIENCETYPE_ID_FILM;
    }

    public static Long getGlobalExperienceTypeIdBook() {
        return GLOBAL_EXPERIENCETYPE_ID_BOOK;
    }

    public static Long getGlobalLocationIdKinepolis() {
        return GLOBAL_LOCATION_ID_KINEPOLIS_GENT;
    }

    public static Long getGlobalLocationIdVooruit() {
        return GLOBAL_LOCATION_ID_VOORUIT;
    }

    private void addUsers() throws CultureLogException {
        UserCreateDto user1CreateDto = new UserCreateDto();
        user1CreateDto.setUsername(USER1_NAME);
        user1CreateDto.setPassword(USER1_PASS);
        USER1_ID = userService.registerUser(user1CreateDto).getId();

        UserCreateDto user2CreateDto = new UserCreateDto();
        user2CreateDto.setUsername(USER2_NAME);
        user2CreateDto.setPassword(USER2_PASS);
        USER2_ID = userService.registerUser(user2CreateDto).getId();
    }

    private void addGlobalExperienceTypes() throws CultureLogException {
        ExperienceType experienceTypeFilm = new ExperienceType();
        experienceTypeFilm.setName(GLOBAL_EXPERIENCETYPE_NAME_FILM);
        GLOBAL_EXPERIENCETYPE_ID_FILM = experienceTypeService.save(experienceTypeFilm).getId();
        ExperienceType experienceTypeBook = new ExperienceType();
        experienceTypeBook.setName(GLOBAL_EXPERIENCETYPE_NAME_BOOK);
        GLOBAL_EXPERIENCETYPE_ID_BOOK = experienceTypeService.save(experienceTypeBook).getId();
    }

    private void addGlobalLocations() throws CultureLogException {
        Location locationKinepolisGent = new Location();
        locationKinepolisGent.setName(GLOBAL_LOCATION_NAME_KINEPOLIS_GENT);
        GLOBAL_LOCATION_ID_KINEPOLIS_GENT = locationService.save(locationKinepolisGent).getId();
        Location locationVooruit = new Location();
        locationVooruit.setName(GLOBAL_LOCATION_NAME_VOORUIT);
        GLOBAL_LOCATION_ID_VOORUIT = locationService.save(locationVooruit).getId();
    }

}