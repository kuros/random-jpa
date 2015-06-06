package com.github.kuros.random.jpa.definition;

import com.github.kuros.random.jpa.mapper.Relation;

import java.util.List;

/**
 * Created by Kumar Rohit on 5/8/15.
 */
public interface HierarchyGenerator {

    HierarchyGraph generate(List<Relation> relations);

}
