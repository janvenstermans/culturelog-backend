package culturelog.backend.configuration;

import culturelog.backend.domain.Location;
import culturelog.backend.domain.Medium;
import culturelog.backend.dto.UserCreateDto;
import culturelog.backend.exception.CultureLogException;
import culturelog.backend.service.LocationService;
import culturelog.backend.service.MediumService;
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
    private MediumService mediumService;

    @Autowired
    private LocationService locationService;

    public static final String USER1_NAME = "a@b.cd";
    public static final String USER1_PASS = "password";
    private static Long USER1_ID;

    public static final String USER2_NAME = "b@b.cd";
    public static final String USER2_PASS = "password";
    private static Long USER2_ID;

    public static final String GLOBAL_MEDIUM_NAME_FILM = "film";
    public static final String GLOBAL_MEDIUM_NAME_BOOK = "boek";
    private static Long GLOBAL_MEDIUM_ID_FILM;
    private static Long GLOBAL_MEDIUM_ID_BOOK;

    public static final String GLOBAL_LOCATION_NAME_KINEPOLIS_GENT = "Kinepolis Gent";
    public static final String GLOBAL_LOCATION_NAME_VOORUIT = "Vooruit";
    private static Long GLOBAL_LOCATION_ID_KINEPOLIS_GENT;
    private static Long GLOBAL_LOCATION_ID_VOORUIT;

    @PostConstruct
    protected void fillDbWithTestData() throws CultureLogException {
        addUsers();
        addGlobalMedia();
        addGlobalLocations();
    }

    public static Long getUser1Id() {
        return USER1_ID;
    }

    public static Long getUser2Id() {
        return USER2_ID;
    }

    public static Long getGlobalMediumIdFilm() {
        return GLOBAL_MEDIUM_ID_FILM;
    }

    public static Long getGlobalMediumIdBook() {
        return GLOBAL_MEDIUM_ID_BOOK;
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

    private void addGlobalMedia() throws CultureLogException {
        Medium mediumFilm = new Medium();
        mediumFilm.setName(GLOBAL_MEDIUM_NAME_FILM);
        GLOBAL_MEDIUM_ID_FILM = mediumService.save(mediumFilm).getId();
        Medium mediumBook = new Medium();
        mediumBook.setName(GLOBAL_MEDIUM_NAME_BOOK);
        GLOBAL_MEDIUM_ID_BOOK = mediumService.save(mediumBook).getId();
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