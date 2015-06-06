package com.github.kuros.random.jpa.types;

import com.github.kuros.random.jpa.persistor.model.ResultMap;
import com.github.kuros.random.jpa.persistor.model.ResultMapImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class CreationPlanImpl implements CreationPlan {
    private CreationOrder creationOrder;
    private ResultMap resultMap;
    private Map<Class<?>, List<Node>> createdNodeMap;
    private Node root;

    public CreationPlanImpl(final CreationOrder creationOrder) {
        this.createdNodeMap = new HashMap<Class<?>, List<Node>>();
        this.root = Node.newInstance();
        this.resultMap = ResultMapImpl.newInstance(root);
        this.creationOrder = creationOrder;
    }

    public Map<Class<?>, List<Node>> getCreatedNodeMap() {
        return createdNodeMap;
    }

    public Node getRoot() {
        return root;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> type) {
        return (T) createdNodeMap.get(type).get(0).getValue();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> type, final int index) {
        return (T) createdNodeMap.get(type).get(index).getValue();
    }

    public void print(final Printer printer) {
        root.print(printer);
    }

    public CreationOrder getCreationOrder() {
        return creationOrder;
    }

    public <T> void deleteItem(final Class<T> type, final int index) {
        final Node node = createdNodeMap.get(type).get(index);
        node.setValue(null);
        node.setChildNodes(new ArrayList<Node>());
    }
}
