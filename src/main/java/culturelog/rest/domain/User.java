package culturelog.rest.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * @author Jan Venstermans
 */
@Document(collection = "users")
public class User {

    @Id
    private String id;

    /**
     * //TODO: required and unique
     */
    private String username;

    /**
     * TODO: required and encrypted.
     */
    private String password;

    private boolean active;

    public User() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
                "User[id=%s, username='%s', active='%s']",
                id, username, active);
    }
}
