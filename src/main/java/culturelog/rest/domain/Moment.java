package culturelog.rest.domain;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Jan Venstermans
 */
@Entity
@Table(name = "moment")
public class Moment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     */
    @NotNull
    private MomentType type = MomentType.DATE;

    @NotNull
    private Date sortDate;

    /**
     * All {@link DisplayDate}s linked to this moment. The type will imply the amount expected.
     */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name="moment_display_date",
            joinColumns=@JoinColumn(name="moment_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="display_date_id", referencedColumnName="id"))
    private List<DisplayDate> displayDates = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MomentType getType() {
        return type;
    }

    public void setType(MomentType type) {
        this.type = type;
    }

    public Date getSortDate() {
        return sortDate;
    }

    public void setSortDate(Date sortDate) {
        this.sortDate = sortDate;
    }

    public List<DisplayDate> getDisplayDates() {
        return displayDates;
    }

    public void setDisplayDates(List<DisplayDate> displayDates) {
        this.displayDates = displayDates;
    }

    @Override
    public String toString() {
        return String.format(
                "Moment[id=%s, type='%s', sortDate=%s, displayDatesCount='%s']",
                id, type, sortDate, displayDates != null ? displayDates.size() : 0);
    }
}
