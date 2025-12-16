package ch.hearc.ig.guideresto.business;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @author cedric.baudet
 */

@Entity
@Table(name = "TYPES_GASTRONOMIQUES")
@NamedQueries({
        @NamedQuery(name = "RestaurantType.findAll",
                query = "SELECT rt FROM RestaurantType rt" ),
        @NamedQuery(name = "RestaurantType.findById",
                query = "SELECT rt FROM RestaurantType rt WHERE rt.id=:id")
})
public class RestaurantType implements IBusinessObject {

    @Id
    @SequenceGenerator(
            name = "SEQ_TYPES_GASTRONOMIQUES",
            sequenceName = "SEQ_TYPES_GASTRONOMIQUES",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SEQ_TYPES_GASTRONOMIQUES"
    )
    @Column(name = "NUMERO", nullable = false)
    private Integer id;

    @Column(name = "LIBELLE", nullable = false)
    private String label;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @OneToMany(mappedBy = "type")
    private Set<Restaurant> restaurants;

    public RestaurantType() {
        this(null, null);
    }

    public RestaurantType(String label, String description) {
        this(null, label, description);
    }

    public RestaurantType(Integer id, String label, String description) {
        this.restaurants = new HashSet();
        this.id = id;
        this.label = label;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Type{id=" + id + ", name='" + label + "'}";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(Set<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }
}