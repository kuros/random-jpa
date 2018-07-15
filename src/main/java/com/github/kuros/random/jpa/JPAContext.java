package com.github.kuros.random.jpa;

import com.github.kuros.random.jpa.persistor.model.ResultMap;
import com.github.kuros.random.jpa.types.CreationPlan;
import com.github.kuros.random.jpa.types.DeletionOrder;
import com.github.kuros.random.jpa.types.Entity;
import com.github.kuros.random.jpa.types.Plan;

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
public interface JPAContext {

    CreationPlan create(Entity... entities);

    ResultMap persist(CreationPlan creationPlan);

    ResultMap createAndPersist(Entity... entities);

    <T, V> DeletionOrder getDeletionOrder(Class<T> type, V... ids);

    void remove(DeletionOrder deletionOrder);

    void remove(Class<?> type);

    void removeAll();

    <T, V> void remove(Class<T> type, V... ids);
}
