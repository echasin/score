package com.innvo.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Score.
 */
@Entity
@Table(name = "score")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "score")
public class Score implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "scorevalue", nullable = false)
    private String scorevalue;

    @Column(name = "jhi_comment")
    private String comment;

    @NotNull
    @Column(name = "lastmodifiedby", nullable = false)
    private String lastmodifiedby;

    @NotNull
    @Column(name = "lastmodifieddatetime", nullable = false)
    private ZonedDateTime lastmodifieddatetime;

    @NotNull
    @Column(name = "domain", nullable = false)
    private String domain;

    @ManyToOne
    private Scorestatus scorestatus;

    @ManyToOne
    private Scorerecordtype scorerecordtype;

    @ManyToOne
    private Scoreclass scoreclass;

    @ManyToOne
    private Scorecategory scorecategory;

    @ManyToOne
    private Scoretype scoretype;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScorevalue() {
        return scorevalue;
    }

    public Score scorevalue(String scorevalue) {
        this.scorevalue = scorevalue;
        return this;
    }

    public void setScorevalue(String scorevalue) {
        this.scorevalue = scorevalue;
    }

    public String getComment() {
        return comment;
    }

    public Score comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLastmodifiedby() {
        return lastmodifiedby;
    }

    public Score lastmodifiedby(String lastmodifiedby) {
        this.lastmodifiedby = lastmodifiedby;
        return this;
    }

    public void setLastmodifiedby(String lastmodifiedby) {
        this.lastmodifiedby = lastmodifiedby;
    }

    public ZonedDateTime getLastmodifieddatetime() {
        return lastmodifieddatetime;
    }

    public Score lastmodifieddatetime(ZonedDateTime lastmodifieddatetime) {
        this.lastmodifieddatetime = lastmodifieddatetime;
        return this;
    }

    public void setLastmodifieddatetime(ZonedDateTime lastmodifieddatetime) {
        this.lastmodifieddatetime = lastmodifieddatetime;
    }

    public String getDomain() {
        return domain;
    }

    public Score domain(String domain) {
        this.domain = domain;
        return this;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Scorestatus getScorestatus() {
        return scorestatus;
    }

    public Score scorestatus(Scorestatus scorestatus) {
        this.scorestatus = scorestatus;
        return this;
    }

    public void setScorestatus(Scorestatus scorestatus) {
        this.scorestatus = scorestatus;
    }

    public Scorerecordtype getScorerecordtype() {
        return scorerecordtype;
    }

    public Score scorerecordtype(Scorerecordtype scorerecordtype) {
        this.scorerecordtype = scorerecordtype;
        return this;
    }

    public void setScorerecordtype(Scorerecordtype scorerecordtype) {
        this.scorerecordtype = scorerecordtype;
    }

    public Scoreclass getScoreclass() {
        return scoreclass;
    }

    public Score scoreclass(Scoreclass scoreclass) {
        this.scoreclass = scoreclass;
        return this;
    }

    public void setScoreclass(Scoreclass scoreclass) {
        this.scoreclass = scoreclass;
    }

    public Scorecategory getScorecategory() {
        return scorecategory;
    }

    public Score scorecategory(Scorecategory scorecategory) {
        this.scorecategory = scorecategory;
        return this;
    }

    public void setScorecategory(Scorecategory scorecategory) {
        this.scorecategory = scorecategory;
    }

    public Scoretype getScoretype() {
        return scoretype;
    }

    public Score scoretype(Scoretype scoretype) {
        this.scoretype = scoretype;
        return this;
    }

    public void setScoretype(Scoretype scoretype) {
        this.scoretype = scoretype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Score score = (Score) o;
        if (score.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), score.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Score{" +
            "id=" + getId() +
            ", scorevalue='" + getScorevalue() + "'" +
            ", comment='" + getComment() + "'" +
            ", lastmodifiedby='" + getLastmodifiedby() + "'" +
            ", lastmodifieddatetime='" + getLastmodifieddatetime() + "'" +
            ", domain='" + getDomain() + "'" +
            "}";
    }
}
