package com.github.kuros.random.jpa.persistor.functions;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.cache.TriggerCache;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.link.Link;
import com.github.kuros.random.jpa.persistor.hepler.Finder;
import com.github.kuros.random.jpa.types.Trigger;
import com.github.kuros.random.jpa.util.AttributeHelper;
import com.github.kuros.random.jpa.util.Util;

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
public class TriggerFunction<T> implements Function<T> {

    private final Finder finder;

    public TriggerFunction(final Cache cache) {
        this.finder = new Finder(cache);
    }

    public T apply(final T object) {
        final Class<?> type = object.getClass();
        final TriggerCache triggerCache = TriggerCache.getInstance();

        if (triggerCache.contains(type)) {
            final Trigger<?> trigger = triggerCache.getTrigger(type);
            final List<String> attributeNames = getAttributeNames(trigger);

            final T found = finder.findByAttributes(object, attributeNames);
            if (found == null) {
                throw new RandomJPAException("Not able to load entity: "
                        + type.getName()
                        + Util.printValues(object) + " by values: " + attributeNames);
            }

            return found;
        }

        return null;
    }

    private List<String> getAttributeNames(final Trigger<?> trigger) {
        final List<Link> links = trigger.getLinks();

        final List<String> attributeNames = new ArrayList<String>();
        for (Link link : links) {
            final String name = AttributeHelper.getName(link.getFrom());
            attributeNames.add(name);
        }
        return attributeNames;
    }
}
