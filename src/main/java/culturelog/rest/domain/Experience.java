package culturelog.rest.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author Jan Venstermans
 */
@Entity
@Table(name = "experience")
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the experience.
     */
    @NotNull
    private String name;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "experienceTypeId", nullable = false)
    private Medium type;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "momentId", nullable = false)
    private Moment moment;

    @ManyToOne
    @JoinColumn(name = "locationId")
    private Location location;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Medium getType() {
        return type;
    }

    public void setType(Medium type) {
        this.type = type;
    }

    public Moment getMoment() {
        return moment;
    }

    public void setMoment(Moment moment) {
        this.moment = moment;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
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
                "Experience[id=%s, username='%s', type='%s', moment='%s', location='%s', comment='%s']",
                id,
                user != null ? user.getUsername() : "global",
                type.getDescription(),
                moment.toString(),
                location != null ? location.getDescription() : "--",
                comment);
    }
}
