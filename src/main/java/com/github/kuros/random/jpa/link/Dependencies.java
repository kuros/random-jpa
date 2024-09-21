package com.github.kuros.random.jpa.link;

import jakarta.persistence.metamodel.Attribute;
import java.util.ArrayList;
import java.util.Arrays;
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
public final class Dependencies {

    private final List<Link> links;
    private final List<Link> ignoreLinks;
    @SuppressWarnings("rawtypes")
    private final List<Attribute> ignoredAttributes;

    private Dependencies() {
        this.links = new ArrayList<>();
        this.ignoreLinks = new ArrayList<>();
        ignoredAttributes = new ArrayList<>();
    }

    public static Dependencies newInstance() {
        return new Dependencies();
    }

    public Dependencies withLink(final Link link) {
        this.links.add(link);
        return this;
    }

    public Dependencies withLink(final List<Link> allLinks) {
        this.links.addAll(allLinks);
        return this;
    }

    public Dependencies ignoreLink(final Link link) {
        this.ignoreLinks.add(link);
        return this;
    }

    public Dependencies ignoreLinks(final List<Link> allLinks) {
        this.ignoreLinks.addAll(allLinks);
        return this;
    }

    @SuppressWarnings("rawtypes")
    public Dependencies ignoreAttributes(final Attribute... attributes) {
        this.ignoredAttributes.addAll(Arrays.asList(attributes));
        return this;
    }

    public List<Link> getLinks() {
        return links;
    }

    public List<Link> getIgnoreLinks() {
        return ignoreLinks;
    }

    public List<Attribute> getIgnoredAttributes() {
        return ignoredAttributes;
    }
}
