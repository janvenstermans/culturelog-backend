package culturelog.backend.domain;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Wrapper around a date with type of display.
 * @author Jan Venstermans
 */
@Entity
@Table(name = "display_date")
public class DisplayDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * display type enum.
     */
    @NotNull
    private DisplayDateType type =  DisplayDateType.DATE;

    /**
     * Date, default now.
     */
    @NotNull
    private Date date = new Date();

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
                "DisplayDate[id=%s, type='%s', date='%s']",
                id, type, date);
    }
}
