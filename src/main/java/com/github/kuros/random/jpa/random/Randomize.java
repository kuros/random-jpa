package com.github.kuros.random.jpa.random;

import com.github.kuros.random.jpa.types.Version;

import java.lang.reflect.Field;
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
public interface Randomize {

    <T> T createRandom(Class<T> type);

    <T> T populateRandomFields(T t, final int index);

    boolean isValueProvided(final Field field, final int index);

    void addFieldValue(Map<Field, Object> fieldValueMap);

    void addFieldValue(final Field field, final int index, final Object value);

    void addFieldValue(final Field field, final Object value);

    Version getVersion();
}
