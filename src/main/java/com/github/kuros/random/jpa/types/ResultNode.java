package com.github.kuros.random.jpa.types;

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
public final class ResultNode<T> {

    private final Class<T> type;
    private final int index;
    private T value;
    private List<ResultNode> childNodes;

    private ResultNode(final Class<T> type, final int index) {
        this.type = type;
        this.index = index;
        this.childNodes = new ArrayList<ResultNode>();
    }

    public static <T> ResultNode<T> newInstance(final Class<T> type, final int index) {
        return new ResultNode<T>(type, index);
    }

    @SuppressWarnings("unchecked")
    public static ResultNode newInstance() {
        return new ResultNode(null, 0);
    }

    public Class<T> getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }

    public T getValue() {
        return value;
    }

    public void setValue(final T value) {
        this.value = value;
    }

    public void setChildNodes(final List<ResultNode> childNodes) {
        this.childNodes = childNodes;
    }

    public List<ResultNode> getChildNodes() {
        return childNodes;
    }

    public void addChildNode(final ResultNode node) {
        childNodes.add(node);
    }

    public void print(final Printer printer) {
        print(printer, "", true);
    }

    private void print(final Printer printer, final String prefix, final boolean isTail) {

        final String detail = type == null ? "*ROOT*" : type.getName() + "|" + index + " " + Util.printEntityId(value);
        printer.print(prefix + (isTail ? "└── " : "├── ") + detail);
        for (int i = 0; i < childNodes.size() - 1; i++) {
            childNodes.get(i).print(printer, prefix + (isTail ? "    " : "│   "), false);
        }
        if (childNodes.size() > 0) {
            childNodes.get(childNodes.size() - 1).print(printer, prefix + (isTail ? "    " : "│   "), true);
        }
    }
}
