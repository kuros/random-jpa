package com.github.incrediblevicky.random.jpa.definition;

import com.github.incrediblevicky.random.jpa.mapper.HierarchyGraph;
import com.github.incrediblevicky.random.jpa.mapper.Relation;

import java.util.List;

/**
 * Created by Kumar Rohit on 5/8/15.
 */
public interface HierarchyGenerator {

    HierarchyGraph generate(List<Relation> relations);

}
