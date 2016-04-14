package com.github.kuros.random.jpa.testUtil.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "relation")
public class RelationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "relation_one_to_one_id")
    private RelationOneToOne relationOneToOne;

    @ManyToOne
    @JoinColumn(name = "relation_many_to_one_id")
    private RelationManyToOne relationManyToOne;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public RelationOneToOne getRelationOneToOne() {
        return relationOneToOne;
    }

    public void setRelationOneToOne(final RelationOneToOne relationOneToOne) {
        this.relationOneToOne = relationOneToOne;
    }

    public RelationManyToOne getRelationManyToOne() {
        return relationManyToOne;
    }

    public void setRelationManyToOne(final RelationManyToOne relationManyToOne) {
        this.relationManyToOne = relationManyToOne;
    }
}
