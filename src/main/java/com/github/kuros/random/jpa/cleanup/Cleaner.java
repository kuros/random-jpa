package com.github.kuros.random.jpa.cleanup;

import com.github.kuros.random.jpa.types.DeletionOrder;

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
public interface Cleaner {

    <T, V> DeletionOrder getDeletionOrder(Class<T> type, V... ids);

    void delete(DeletionOrder deletionOrder);

    void truncate(Class<?> type);

    void truncateAll();

    <T, V> void delete(Class<T> type, V... ids);
}
