package culturelog.rest.dto;

import culturelog.rest.domain.MomentType;

/**
 * Dto format{@link culturelog.rest.domain.Moment}.
 * @author Jan Venstermans
 */
public abstract class MomentDto {

    private Long id;

    private final MomentType momentType;

    public MomentDto(MomentType momentType) {
        this.momentType = momentType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MomentType getMomentType() {
        return momentType;
    }
}
