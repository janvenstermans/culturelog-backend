package culturelog.backend.dto;

/**
 * @author Jan Venstermans
 */
public class ExperienceDto {

    private Long id;

    private String name;

    private MediumDto type;

    private MomentDto moment;

    private LocationDto location;

    private String comment;

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

    public MediumDto getType() {
        return type;
    }

    public void setType(MediumDto type) {
        this.type = type;
    }

    public MomentDto getMoment() {
        return moment;
    }

    public void setMoment(MomentDto moment) {
        this.moment = moment;
    }

    public LocationDto getLocation() {
        return location;
    }

    public void setLocation(LocationDto location) {
        this.location = location;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return String.format(
                "ExperienceDto[id=%s, name='%s', type='%s', moment='%s', location='%s', comment='%s']",
                id,
                name,
                type != null ? type.getDescription() : "",
                moment != null ? moment.toString() : "",
                location != null ? location.getDescription() : "",
                comment);
    }
}
