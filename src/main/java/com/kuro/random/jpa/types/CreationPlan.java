package com.kuro.random.jpa.types;

import com.kuro.random.jpa.mapper.TableNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kumar Rohit on 5/10/15.
 */
public class CreationPlan {
    private List<TableNode> creationPlan;

    private CreationPlan() {
        this.creationPlan = new ArrayList<TableNode>();
    }

    public static CreationPlan newIntance() {
        return new CreationPlan();
    }

    public void add(final TableNode tableNode) {
        this.creationPlan.add(tableNode);
    }

    public List<TableNode> getCreationPlan() {
        return creationPlan;
    }

    public boolean contains(final TableNode node) {
        return this.creationPlan.contains(node);
    }
}
