package culturelog.rest.dto;

/**
 * Dto format{@link culturelog.rest.domain.Location}.
 * @author Jan Venstermans
 */
public class LocationDto {

    private Long id;

    private String description;

//    private String address;
//
//    private double lat;
//
//    private double lng;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public double getLat() {
//        return lat;
//    }
//
//    public void setLat(double lat) {
//        this.lat = lat;
//    }
//
//    public double getLng() {
//        return lng;
//    }
//
//    public void setLng(double lng) {
//        this.lng = lng;
//    }

    @Override
    public String toString() {
        return String.format(
                "Location[id=%s, description='%s']",
//                "Location[id=%s, description='%s', address='%s', lat='%s', lng='%s']",
                id, description);
    }
}
