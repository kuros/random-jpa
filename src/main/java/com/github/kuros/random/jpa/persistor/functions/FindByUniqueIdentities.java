package com.github.kuros.random.jpa.persistor.functions;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.log.LogFactory;
import com.github.kuros.random.jpa.log.Logger;
import com.github.kuros.random.jpa.persistor.hepler.Finder;
import com.github.kuros.random.jpa.provider.MultiplePrimaryKeyProvider;
import com.github.kuros.random.jpa.provider.UniqueConstraintProvider;

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
class FindByUniqueIdentities<T> implements Function<T> {

    private static final Logger LOGGER = LogFactory.getLogger(PersistFunction.class);
    private final UniqueConstraintProvider uniqueConstraintProvider;
    private final MultiplePrimaryKeyProvider multiplePrimaryKeyProvider;
    private Finder finder;

    FindByUniqueIdentities(final Cache cache) {
        this.uniqueConstraintProvider = cache.getUniqueConstraintProvider();
        this.multiplePrimaryKeyProvider = cache.getMultiplePrimaryKeyProvider();
        this.finder = new Finder(cache);
    }

    public T apply(final T typeObject) {
        final Class<?> tableClass = typeObject.getClass();
        final List<List<String>> attributeCombinations = getAttributeCombinations(tableClass);

        T t = null;
        for (List<String> attributeCombination : attributeCombinations) {
            final T found = find(typeObject, attributeCombination);
            if (found != null) {
                t = found;
                break;
            }
        }

        return t;
    }

    private List<List<String>> getAttributeCombinations(final Class<?> tableClass) {
        final List<List<String>> list = new ArrayList<List<String>>();
        list.add(multiplePrimaryKeyProvider.getMultiplePrimaryKeyAttributes(tableClass));
        list.add(uniqueConstraintProvider.getUniqueCombinationAttributes(tableClass));

        return list;
    }

    private T find(final T typeObject, final List<String> attributes) {
        return attributes != null ? finder.findByAttributes(typeObject, attributes) : null;
    }

    @VisibleForTesting
    void setFinder(final Finder finder) {
        this.finder = finder;
    }
}
