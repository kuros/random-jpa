package com.github.kuros.random.jpa.persistor.model;

import com.github.kuros.random.jpa.types.Printer;

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
public final class ResultMapImpl implements ResultMap {

    private final Map<Class<?>, List<Object>> resultMap;
    private final String constructionTree;

    private ResultMapImpl(final Map<Class<?>, List<Object>> resultMap, final String constructionTree) {
        this.resultMap = resultMap;
        this.constructionTree = constructionTree;
    }

    public static ResultMapImpl newInstance(final ResultNodeTree nodeTree) {
        return new ResultMapImpl(nodeTree.getCreatedEntities(), nodeTree.getConstructionTree());
    }

    public <T> T get(final Class<T> type) {
        return get(type, 0);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> type, final int index) {
        final List<?> objects = resultMap.get(type);
        return objects == null ? null : (T) objects.get(index);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(final Class<T> type) {
        return (List<T>) resultMap.get(type);
    }

    public void print(final Printer printer) {
        printer.print(constructionTree);
    }
}
