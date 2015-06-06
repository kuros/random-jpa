package com.github.kuros.random.jpa.link;

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
public final class Dependencies {

    private List<Link> links;

    private Dependencies() {
        this.links = new ArrayList<Link>();
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

    public List<Link> getLinks() {
        return links;
    }
}
