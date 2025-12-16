package ch.hearc.ig.guideresto.business;

import jakarta.persistence.*;

import java.util.Date;

/**
 * @author cedric.baudet
 */
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class Evaluation implements IBusinessObject {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "SEQ_EVAL"
    )
    @Column(name="NUMERO", nullable=false)
    @SequenceGenerator(
            name = "SEQ_EVAL",
            sequenceName = "SEQ_EVAL",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "SEQ_EVAL"
    )
    private Integer id;

    @Column(name="DATE_EVAL", nullable=false)
    private Date visitDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="FK_REST", nullable=false)
    private Restaurant restaurant;

    public Evaluation() {
        this(null, null, null);
    }

    public Evaluation(Integer id, Date visitDate, Restaurant restaurant) {
        this.id = id;
        this.visitDate = visitDate;
        this.restaurant = restaurant;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

}