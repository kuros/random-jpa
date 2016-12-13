package com.github.kuros.random.jpa.persistor.functions;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.exception.RandomJPAException;

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
public class FunctionProcessor<T> {

    private Cache cache;

    public FunctionProcessor(final Cache cache) {
        this.cache = cache;
    }

    @SuppressWarnings("unchecked")
    public T findOrSave(final T object) {
        final List<Function> functions = getFunctions();

        T persistedObject = null;

        for (Function<T> function : functions) {
            try {
                persistedObject = function.apply(object);
                if (persistedObject != null) {
                    break;
                }
            } catch (final RandomJPAException e) {
                // Do nothing
            }
        }

        if (persistedObject == null) {
            throw new RandomJPAException("unable to save: " + object.getClass());
        }

        return persistedObject;
    }

    List<Function> getFunctions() {
        final List<Function> functions = new ArrayList<Function>();
        functions.add(new TriggerFunction(cache));
        functions.add(new FindByUniqueIdentities(cache));
        functions.add(new FindById(cache));
        functions.add(new PersistFunction(cache));
        return functions;
    }
}
