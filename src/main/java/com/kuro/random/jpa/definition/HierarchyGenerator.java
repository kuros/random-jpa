package com.kuro.random.jpa.definition;

import com.kuro.random.jpa.mapper.HierarchyGraph;
import com.kuro.random.jpa.mapper.Relation;
import com.kuro.random.jpa.provider.ForeignKeyRelation;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Map;

/**
 * Created by Kumar Rohit on 5/8/15.
 */
public interface HierarchyGenerator {

    HierarchyGraph generate(List<Relation> relations);

}
