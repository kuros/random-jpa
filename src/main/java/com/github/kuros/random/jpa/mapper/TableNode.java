package com.github.kuros.random.jpa.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * Copyright (c) 2015 Kumar Rohit
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License or any
 *    later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public final class TableNode {

    private Set<Class<?>> parentClasses;
    private List<Relation> relations;

    public static TableNode newInstance() {
        return new TableNode();
    }

    private TableNode() {
        this.parentClasses = new HashSet<Class<?>>();
        this.relations = new ArrayList<Relation>();
    }

    public TableNode addRelation(final Relation relation) {
        this.relations.add(relation);
        return this;
    }

    public TableNode addParent(final Class<?> parentClass) {
        parentClasses.add(parentClass);
        return this;
    }

    public Set<Class<?>> getParentClasses() {
        return parentClasses;
    }

    public List<Relation> getRelations() {
        return relations;
    }

}
