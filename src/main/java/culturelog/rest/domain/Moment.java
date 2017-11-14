package culturelog.rest.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
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

    /**
     * All {@link DisplayDate}s linked to this moment. The type will imply the amount expected.
     */
    @ManyToMany
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

    public List<DisplayDate> getDisplayDates() {
        return displayDates;
    }

    public void setDisplayDates(List<DisplayDate> displayDates) {
        this.displayDates = displayDates;
    }

    @Override
    public String toString() {
        return String.format(
                "Moment[id=%s, type='%s', displayDatesCount='%s']",
                id, type, displayDates != null ? displayDates.size() : 0);
    }
}
