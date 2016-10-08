package culturelog.rest.dto;

/**
 * @author Jan Venstermans
 */
public class UserCreateDto extends UserDto {

    private String password;

    public UserCreateDto() {}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format(
                "UserCreateDto[]",
                super.toString());
    }
}
