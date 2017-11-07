package culturelog.rest.dto;

/**
 * @author Jan Venstermans
 */
public class ExperienceDto {

    private Long id;

    private String name;

    //cannot be null
    private MediumDto mediumDto;

    //can be null
    private LocationDto locationDto;

    //can be null?
    private MomentDto momentDto;

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

    public MediumDto getMediumDto() {
        return mediumDto;
    }

    public void setMediumDto(MediumDto mediumDto) {
        this.mediumDto = mediumDto;
    }

    public LocationDto getLocationDto() {
        return locationDto;
    }

    public void setLocationDto(LocationDto locationDto) {
        this.locationDto = locationDto;
    }

    public MomentDto getMomentDto() {
        return momentDto;
    }

    public void setMomentDto(MomentDto momentDto) {
        this.momentDto = momentDto;
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
                "ExperienceDto[id=%s, name='%s', medium='%s', location='%s', moment='%s', comment='%s']",
                id, name, mediumDto != null ? mediumDto.getDescription() : "",
                locationDto != null ? locationDto.getDescription() : "",
                momentDto != null ? momentDto.toString() : "",
                comment);
    }
}
