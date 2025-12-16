package ch.hearc.ig.guideresto.business;

/**
 * @author cedric.baudet
 */

import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="COMMENTAIRES")
@NamedQueries({
        @NamedQuery(name="CompleteEvaluation.findAll",
                query="SELECT ce FROM CompleteEvaluation ce"),
        @NamedQuery(name="CompleteEvaluation.findById",
                query="SELECT ce FROM CompleteEvaluation ce WHERE ce.id=:id")
})

public class CompleteEvaluation extends Evaluation {

    @Column(name="COMMENTAIRE", nullable=false)
    private String comment;

    @Column(name="NOM_UTILISATEUR", nullable=false)
    private String username;

    @OneToMany(mappedBy="evaluation")
    private Set<Grade> grades;

    public CompleteEvaluation() {
        this(null, null, null, null);
    }

    public CompleteEvaluation(Date visitDate, Restaurant restaurant, String comment, String username) {
        this(null, visitDate, restaurant, comment, username);
    }

    public CompleteEvaluation(Integer id, Date visitDate, Restaurant restaurant, String comment, String username) {
        super(id, visitDate, restaurant);
        this.comment = comment;
        this.username = username;
        this.grades = new HashSet();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Grade> getGrades() {
        return grades;
    }

    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
    }
}