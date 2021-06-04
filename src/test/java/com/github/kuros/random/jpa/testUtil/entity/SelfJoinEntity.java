package com.github.kuros.random.jpa.testUtil.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
public class SelfJoinEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    private Integer refId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private SelfJoinEntity joinEntity;


    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getRefId() {
        return refId;
    }

    public SelfJoinEntity getJoinEntity() {
        return joinEntity;
    }

    public void setJoinEntity(final SelfJoinEntity joinEntity) {
        this.joinEntity = joinEntity;
    }

    public void setRefId(final Integer refId) {
        this.refId = refId;
    }
}
