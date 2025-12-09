package ch.hearc.ig.guideresto.business;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @author cedric.baudet
 */

@Entity
@Table(name="VILLES")
@NamedQueries({
        @NamedQuery(name = "City.findAll",
                query = "SELECT c FROM City c"),
        @NamedQuery(name = "City.findById",
                query = "SELECT c FROM City c WHERE c.id=:id")
})

public class City implements IBusinessObject {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SEQ_VILLES"
    )
    @Column(name = "NUMERO")
    private Integer id;

    @Column(name = "CODE_POSTAL", nullable = false, unique = true, length = 4)
    private String zipCode;

    @Column(name = "NOM_VILLE", nullable = false)
    private String cityName;

    @OneToMany(mappedBy = "address.city")
    private Set<Restaurant> restaurants;

    public City() {
        this(null, null);
    }

    public City(String zipCode, String cityName) {
        this(null, zipCode, cityName);
    }

    public City(Integer id, String zipCode, String cityName) {
        this.id = id;
        this.zipCode = zipCode;
        this.cityName = cityName;
        this.restaurants = new HashSet();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String city) {
        this.cityName = city;
    }

    public Set<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(Set<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    @Override
    public String toString() {
        return "City{id=" + id + ", name='" + cityName + "'}";
    }
}