package culturelog.backend.dto;

/**
 * Dto format{@link culturelog.backend.domain.Medium}.
 * @author Jan Venstermans
 */
public class MediumDto {

    private Long id;

    private String name;

    private String description;

    private boolean global;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    @Override
    public String toString() {
        return String.format(
                "MediumDto[id=%s, name='%s', description='%s', global='%s']",
                id, name, description, global);
    }
}
