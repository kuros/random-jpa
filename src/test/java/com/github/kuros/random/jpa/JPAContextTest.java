package com.github.kuros.random.jpa;

import com.github.kuros.random.jpa.testUtil.MockEntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.MockRelationshipProvider;
import org.junit.Test;

import javax.persistence.EntityManager;

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
public class JPAContextTest {

    @Test
    public void should() {
        final MockEntityManagerProvider mockEntityManagerProvider = MockEntityManagerProvider.createMockEntityManager();
        final EntityManager entityManager = mockEntityManagerProvider.getEntityManager();
        MockRelationshipProvider.addMockRelationship(entityManager);

//        JPAContext jpaContext = JPAContext.create(entityManager);
//        jpaContext.create();
    }
}
