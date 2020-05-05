package org.telegram.bot.beldtp.model;

import javax.persistence.*;

@Entity
@Table(name = "location")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "location")
    private Incident incident;

    private Float longitude;

    private Float latitude;

    /**
     * See {@link com.byteowls.jopencage.model.JOpenCageComponents } for more details about
     * {@code String} values in this class
     */

    private String city;

    private String cityDistrict;

    private String country;

    private String countryCode;

    private String county;

    private String postcode;

    private String road;

    private String houseNumber;

    private String state;

    public Location() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityDistrict() {
        return cityDistrict;
    }

    public void setCityDistrict(String cityDistrict) {
        this.cityDistrict = cityDistrict;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if(getLongitude() != null && getLatitude() != null){
//            builder.append("[Месцазнаходжанне](https://www.google.com/maps?q=")
//                    .append(getLatitude()).append(",").append(getLongitude()).append(")").append("\n");
            builder.append("<a href=\"https://www.google.com/maps?q=")
                    .append(getLatitude()).append(",").append(getLongitude()).append("\">")
                    .append("Месцазнаходжанне").append("</a>")
                    .append("\n");
        }

        if(getCountry() != null){
            builder.append(getCountry()).append("\n");
        }


        if(getCity() != null){
            builder.append(getCity()).append(", ");
        }

        if(getState() != null){
            builder.append(getState()).append("\n");
        }

        if(getRoad() != null){
            builder.append(getRoad()).append(" ");
        }

        if(getHouseNumber() != null){
            builder.append(getHouseNumber()).append("\n");
        }

        return builder.toString();
    }
}
