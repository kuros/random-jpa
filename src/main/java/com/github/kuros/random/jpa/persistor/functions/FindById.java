package com.github.kuros.random.jpa.persistor.functions;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.persistor.hepler.Finder;
import com.github.kuros.random.jpa.util.NumberUtil;
import com.github.kuros.random.jpa.util.Util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

    private final Cache cache;
    private final AttributeProvider attributeProvider;
    private static final List<Class> PRIMITIVE_ID_CLASSES = new ArrayList<Class>();

    static {
        PRIMITIVE_ID_CLASSES.add(Double.TYPE);
        PRIMITIVE_ID_CLASSES.add(Float.TYPE);
        PRIMITIVE_ID_CLASSES.add(Integer.TYPE);
        PRIMITIVE_ID_CLASSES.add(Long.TYPE);
        PRIMITIVE_ID_CLASSES.add(Short.TYPE);
    }

    public FindById(final Cache cache) {
        this.cache = cache;
        this.attributeProvider = cache.getAttributeProvider();
    }

    public T apply(final T type) {
        final Class<?> tableClass = type.getClass();
        final EntityTableMapping entityTableMapping = attributeProvider.get(tableClass);

        final List<String> attributeIds = entityTableMapping.getAttributeIds();
        for (String attributeId : attributeIds) {
            try {
                final Field field = Util.getField(tableClass, attributeId);
                field.setAccessible(true);
                final Object o = field.get(type);
                if (o == null
                        || (PRIMITIVE_ID_CLASSES.contains(field.getType())
                            && NumberUtil.castNumber(Integer.class, o) == 0)) {
                    return null;
                }
            } catch (final Exception e) {
                throw new RandomJPAException(e);
            }
        }

        final Finder finder = new Finder(cache);
        return finder.findByAttributes(type, attributeIds);
    }

}
