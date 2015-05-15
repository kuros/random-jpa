package com.kuro.random.jpa.types;

import com.kuro.random.jpa.mapper.TableNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kumar Rohit on 5/10/15.
 */
public final class CreationPlan {
    private List<Class<?>> creationPlan;

    private CreationPlan() {
        this.creationPlan = new ArrayList<Class<?>>();
    }

    public static CreationPlan newInstance() {
        return new CreationPlan();
    }

    public void add(final Class<?> type) {
        creationPlan.add(type);
    }

    public List<Class<?>> getCreationPlan() {
        return creationPlan;
    }

    public boolean contains(final Class<?> type) {
        return creationPlan.contains(type);
    }
}
