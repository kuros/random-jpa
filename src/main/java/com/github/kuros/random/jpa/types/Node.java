package com.github.kuros.random.jpa.types;

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
public final class Node<T> {

    private final Class<T> type;
    private final int index;
    private T value;
    private List<Node> childNodes;
    private final int depth;

    private Node(final Class<T> type, final int depth, final int index) {
        this.type = type;
        this.depth = depth;
        this.index = index;
        this.childNodes = new ArrayList<Node>();
    }

    public static <T> Node<T> newInstance(final Class<T> type, final int depth, final int index) {
        return new Node<T>(type, depth, index);
    }

    @SuppressWarnings("unchecked")
    public static Node newInstance() {
        return new Node(null, 0, 0);
    }

    public Class<T> getType() {
        return type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(final T value) {
        this.value = value;
    }

    public void setChildNodes(final List<Node> childNodes) {
        this.childNodes = childNodes;
    }

    public List<Node> getChildNodes() {
        return childNodes;
    }

    public void addChildNode(final Node node) {
        childNodes.add(node);
    }

    public String print() {
        final StringBuilder stringBuilder = new StringBuilder();
        print(stringBuilder, "", true);
        return stringBuilder.toString();
    }

    private void print(final StringBuilder stringBuilder, final String prefix, final boolean isTail) {

        final String detail = type == null ? "*ROOT*" : "(h=" + depth + ") " + type.getName() + "|" + index;
        stringBuilder.append("\n").append(prefix).append(isTail ? "└── " : "├── ").append(detail);
        for (int i = 0; i < childNodes.size() - 1; i++) {
            childNodes.get(i).print(stringBuilder, prefix + (isTail ? "    " : "│   "), false);
        }
        if (childNodes.size() > 0) {
            childNodes.get(childNodes.size() - 1).print(stringBuilder, prefix + (isTail ? "    " : "│   "), true);
        }
    }
}
