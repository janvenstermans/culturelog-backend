package culturelog.rest.domain;

import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @author Jan Venstermans
 */
public class Experience {

    @Id
    private String id;

    /**
     * Name of the user that has created the experience.
     *
     * required=true
     */
    private String username;

    /**
     * required=true
     */
    private String title;

    /**
     * required=true
     */
    private Date date;

    private String medium;

    private String comment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
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
                "Experience[id=%s, username='%s', title='%s', date='%s', medium='%s', comment='%s']",
                id, username, title, date, medium, comment);
    }
}
