package culturelog.backend.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import culturelog.backend.domain.MomentType;

/**
 * Dto format{@link culturelog.backend.domain.Moment} of type {@link culturelog.backend.domain.MomentType#DATE}.
 * @author Jan Venstermans
 */
@JsonDeserialize(as = DateMomentDto.class) // need this to override MomentDtoDeserializer, to avoid cyclic deserialization
public class DateMomentDto extends MomentDto {

    private DisplayDateDto displayDate;

    public DateMomentDto() {
        super(MomentType.DATE);
    }

    public DisplayDateDto getDisplayDate() {
        return displayDate;
    }

    public void setDisplayDate(DisplayDateDto displayDate) {
        this.displayDate = displayDate;
    }


    @Override
    public String toString() {
        return String.format(
                "DateMomentDto[id=%s, type='%s', displayDate='%s']",
                getId(), getMomentType(), displayDate);
    }
}
