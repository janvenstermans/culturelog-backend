package culturelog.backend.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import culturelog.backend.config.MomentDtoDeserializer;
import culturelog.backend.domain.MomentType;

/**
 * Dto format{@link culturelog.backend.domain.Moment}.
 * @author Jan Venstermans
 */
@JsonDeserialize(using = MomentDtoDeserializer.class)
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
