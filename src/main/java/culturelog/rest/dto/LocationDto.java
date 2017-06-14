package culturelog.rest.dto;

/**
 * Dto format{@link culturelog.rest.domain.Location}.
 * @author Jan Venstermans
 */
public class LocationDto {

    private String id;

    private String description;

    private String address;

    private double lat;

    private double lng;

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

    @Override
    public String toString() {
        return String.format(
                "Location[id=%s, description='%s', address='%s', lat='%s', lng='%s']",
                id, description, address, lat, lng);
    }
}
