package com.github.kuros.random.jpa.persistor.hepler;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.util.Util;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
public class Finder {

    private EntityManager entityManager;

    public Finder(final Cache cache) {
        this.entityManager = cache.getEntityManager();
    }

    @SuppressWarnings("unchecked")
    public  <T> T findByAttributes(final T typeObject, final List<String> attributes) {
        final Class<?> tableClass = typeObject.getClass();
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery q = criteriaBuilder.createQuery(tableClass);

        final Root<?> from = q.from(tableClass);

        q.select(from);

        final Predicate[] predicates = new Predicate[attributes.size()];

        for (int i = 0; i < attributes.size(); i++) {
            final String attribute = attributes.get(i);
            try {
                final Field declaredField = Util.getField(tableClass, attribute);
                declaredField.setAccessible(true);
                predicates[i] = criteriaBuilder.equal(from.get(attribute), declaredField.get(typeObject));
            } catch (final Exception e) {
                throw new RandomJPAException(e);
            }
        }

        q.where(predicates);

        final TypedQuery typedQuery = entityManager.createQuery(q);
        final List resultList = typedQuery.getResultList();

        return resultList.size() == 0 ? null : (T) resultList.get(0);
    }

    @SuppressWarnings("unchecked")
    public  <T> List<T> findByAttributes(final Class<T> type, final Map<String, Object> attributeValues) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery q = criteriaBuilder.createQuery(type);

        final Root<?> from = q.from(type);

        q.select(from);

        final Predicate[] predicates = new Predicate[attributeValues.keySet().size()];

        final List<Predicate> predicateList = new ArrayList<Predicate>();
        for (String s : attributeValues.keySet()) {
            predicateList.add(criteriaBuilder.equal(from.get(s), attributeValues.get(s)));
        }

        q.where(predicateList.toArray(predicates));

        q.where(predicates);

        final TypedQuery typedQuery = entityManager.createQuery(q);
        final List resultList = typedQuery.getResultList();

        return resultList;
    }
}
