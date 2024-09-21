package com.github.kuros.random.jpa.testUtil.entity;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(SelfJoinEntity.class)
public class SelfJoinEntity_ {

    public static volatile SingularAttribute<SelfJoinEntity, Integer> id;
    public static volatile SingularAttribute<SelfJoinEntity, String> name;
    public static volatile SingularAttribute<SelfJoinEntity, Integer> refId;
    public static volatile SingularAttribute<SelfJoinEntity, SelfJoinEntity> joinEntity;
}
