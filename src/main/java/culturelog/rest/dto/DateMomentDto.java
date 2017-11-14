package culturelog.rest.dto;

import culturelog.rest.domain.MomentType;

/**
 * Dto format{@link culturelog.rest.domain.Moment} of type {@link culturelog.rest.domain.MomentType#DATE}.
 * @author Jan Venstermans
 */
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
