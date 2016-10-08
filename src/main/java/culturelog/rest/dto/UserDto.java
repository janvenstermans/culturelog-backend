package culturelog.rest.dto;

/**
 * @author Jan Venstermans
 */
public class UserDto {

    private String username;

    private boolean active;

    public UserDto() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return String.format(
                "UserDto[username='%s', active='%s']",
                username, active);
    }
}
