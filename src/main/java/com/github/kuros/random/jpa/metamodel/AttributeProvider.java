package com.github.kuros.random.jpa.metamodel;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.metamodel.providers.Provider;

import javax.persistence.EntityManager;
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
public class AttributeProvider {

    private Provider provider;

    public static AttributeProvider getInstance(final EntityManager entityManager) {
        return new AttributeProvider(entityManager);
    }

    private AttributeProvider(final EntityManager entityManager) {
        this.provider = AttributeProviderFactory.getProvider(entityManager);
    }

    @VisibleForTesting
    AttributeProvider(final Provider provider) {
        this.provider = provider;
    }

    public EntityTableMapping get(final Class<?> type) {
        return provider.get(type);
    }

    public List<EntityTableMapping> get(final String tableName) {
        return provider.get(tableName);
    }
}
