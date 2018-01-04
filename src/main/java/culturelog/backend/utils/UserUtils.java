package culturelog.backend.utils;


import culturelog.backend.domain.User;

/**
 * Utils class for {@link User}.
 * @author Jan Venstermans
 */
public class UserUtils {

    private UserUtils() {
    }

    public static boolean areUsersSame(User user1, User user2) {
        if (user1 == null && user2 == null) {
            return true;
        }
        if (user1 == null || user2 == null) {
            return false;
        }
        return areUsersIdSame(user1.getId(), user2.getId());
    }

    public static boolean areUsersIdSame(Long userId1, Long userId2) {
        if (userId1 == null && userId2 == null) {
            return true;
        }
        if (userId1 == null || userId2 == null) {
            return false;
        }
        return userId1.equals(userId2);
    }
}
