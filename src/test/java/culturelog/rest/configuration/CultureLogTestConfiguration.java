package culturelog.rest.configuration;

import culturelog.rest.domain.Medium;
import culturelog.rest.domain.User;
import culturelog.rest.dto.UserCreateDto;
import culturelog.rest.exception.CultureLogException;
import culturelog.rest.service.MediumService;
import culturelog.rest.service.UserService;
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

    public static final String USER1 = "a@b.cd";
    public static final String USER1_PASS = "password";
    private static Long USER1_ID;

    public static final String USER2 = "b@b.cd";
    public static final String USER2_PASS = "password";
    private static Long USER2_ID;

    public static final String GLOBAL_MEDIUM_NAME_FILM = "film";
    public static final String GLOBAL_MEDIUM_NAME_BOOK = "boek";
    private static Long GLOBAL_MEDIUM_ID_FILM;
    private static Long GLOBAL_MEDIUM_ID_BOOK;

    @PostConstruct
    protected void fillDbWithTestData() throws CultureLogException {
        addUsers();
        addGlobalMedia();
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

    private void addUsers() throws CultureLogException {
        UserCreateDto user1CreateDto = new UserCreateDto();
        user1CreateDto.setUsername(USER1);
        user1CreateDto.setPassword(USER1_PASS);
        USER1_ID = userService.registerUser(user1CreateDto).getId();

        UserCreateDto user2CreateDto = new UserCreateDto();
        user2CreateDto.setUsername(USER2);
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

}