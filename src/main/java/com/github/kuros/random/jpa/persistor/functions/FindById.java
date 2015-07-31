package com.github.kuros.random.jpa.persistor.functions;

import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.persistor.hepler.Finder;

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
public class FindById<T> implements Function<T> {

    private final AttributeProvider attributeProvider;

    public FindById() {
        this.attributeProvider = AttributeProvider.getInstance();
    }

    public T apply(final T type) {
        final Class<?> tableClass = type.getClass();
        final EntityTableMapping entityTableMapping = attributeProvider.get(tableClass);

        final Finder finder = new Finder();
        return finder.findByAttributes(type, entityTableMapping.getAttributeIds());
    }


}
