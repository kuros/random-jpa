package com.github.kuros.random.jpa.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kumar Rohit on 5/17/15.
 */
public final class Plan {

    private List<Entity> entities;

    private Plan() {
        this.entities = new ArrayList<Entity>();
    }

    public static Plan create() {
        return new Plan();
    }

    public <T> Plan add(final Entity<T> entity) {
        entities.add(entity);
        return this;
    }

    public List<Entity> getEntities() {
        return entities;
    }
}
