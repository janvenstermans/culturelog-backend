package culturelog.backend.dto;

import culturelog.backend.domain.DisplayDateType;

import java.util.Date;

/**
 * Dto format{@link culturelog.backend.domain.DisplayDate}.
 * @author Jan Venstermans
 */
public class DisplayDateDto {

    private Long id;

    private DisplayDateType type;

    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DisplayDateType getType() {
        return type;
    }

    public void setType(DisplayDateType type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format(
                "DisplayDateDto[id=%s, type='%s', date='%s']",
                id, type, date);
    }
}
