package culturelog.rest.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Jan Venstermans
 */
@Document(collection = "locations")
public class Location {

    @Id
    private String id;

    /**
     * Description or title of the location.
     * Required.
     */
    private String description;

    /**
     * optional
     */
    private String address;

    /**
     * optional
     */
    private double lat;

    /**
     * optional
     */
    private double lng;

    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return String.format(
                "Location[id=%s, description='%s', address='%s', lat='%s', lng='%s']",
                id, description, address, lat, lng);
    }
}
