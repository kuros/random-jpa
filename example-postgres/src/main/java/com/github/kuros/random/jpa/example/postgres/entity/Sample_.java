package com.github.kuros.random.jpa.example.postgres.entity;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Sample.class)
public class Sample_ {
    public static volatile SingularAttribute<Sample, Integer> id;
    public static volatile SingularAttribute<Sample, String> name;
}
