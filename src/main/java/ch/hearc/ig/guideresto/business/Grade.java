package ch.hearc.ig.guideresto.business;

import jakarta.persistence.*;

/**
 * @author cedric.baudet
 */

@Entity
@Table(name="NOTES")
@NamedQueries({
        @NamedQuery(name="Grade.findAll",
                query="SELECT g FROM Grade g"),
        @NamedQuery(name="Grade.findById",
                query="SELECT g FROM Grade g WHERE g.id=:id"),
        @NamedQuery(name = "Grade.findByEvaluationId",
                query = "SELECT g FROM Grade g WHERE g.evaluation.id = :evaluationId")
})
public class Grade implements IBusinessObject {

    @Id
    @SequenceGenerator(
            name = "SEQ_NOTES",
            sequenceName = "SEQ_NOTES",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "SEQ_NOTES"
    )
    @Column(name="NUMERO")
    private Integer id;

    @Column(name="NOTE")
    private Integer grade;

    @ManyToOne
    @JoinColumn(name="FK_COMM", nullable = false)
    private CompleteEvaluation evaluation;

    @ManyToOne
    @JoinColumn(name="FK_CRIT", nullable = false)
    private EvaluationCriteria criteria;


    public Grade() {
        this(null, null, null);
    }

    public Grade(Integer grade, CompleteEvaluation evaluation, EvaluationCriteria criteria) {
        this(null, grade, evaluation, criteria);
    }

    public Grade(Integer id, Integer grade, CompleteEvaluation evaluation, EvaluationCriteria criteria) {
        this.id = id;
        this.grade = grade;
        this.evaluation = evaluation;
        this.criteria = criteria;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public CompleteEvaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(CompleteEvaluation evaluation) {
        this.evaluation = evaluation;
    }

    public EvaluationCriteria getCriteria() {
        return criteria;
    }

    public void setCriteria(EvaluationCriteria criteria) {
        this.criteria = criteria;
    }


}