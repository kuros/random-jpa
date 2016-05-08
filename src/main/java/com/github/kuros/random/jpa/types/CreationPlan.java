package com.github.kuros.random.jpa.types;

import javax.persistence.metamodel.Attribute;

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
public interface CreationPlan {
    @Deprecated
    <T> T get(Class<T> type);

    @Deprecated
    <T> T get(Class<T> type, int index);

    <T, V> void set(Attribute<T, V> attribute, V value);

    <T, V> void set(int index, Attribute<T, V> attribute, V value);

    void print(Printer printer);

    <T> void deleteItem(Class<T> type, int index);
}
