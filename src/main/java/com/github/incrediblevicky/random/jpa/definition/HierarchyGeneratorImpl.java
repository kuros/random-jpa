package com.github.incrediblevicky.random.jpa.definition;

import com.github.incrediblevicky.random.jpa.mapper.HierarchyGraph;
import com.github.incrediblevicky.random.jpa.mapper.Relation;

import java.util.List;

/**
 * Created by Kumar Rohit on 5/8/15.
 */
public class HierarchyGeneratorImpl implements HierarchyGenerator {

    public HierarchyGraph addRelation(final HierarchyGraph hierarchyGraph, final List<Relation> relations) {
        for (Relation relation : relations) {
            hierarchyGraph.addRelation(relation);
        }

        return hierarchyGraph;
    }

    public HierarchyGraph generate(final List<Relation> relations) {
        return addRelation(HierarchyGraph.newInstance(), relations);
    }
}
