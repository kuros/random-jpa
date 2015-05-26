package com.kuro.random.jpa.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kumar Rohit on 5/25/15.
 */
public final class Node<T> {

    private final Class<T> type;
    private final int index;
    private T value;
    private List<Node> childNodes;

    private Node(final Class<T> type, final int index) {
        this.type = type;
        this.index = index;
        this.childNodes = new ArrayList<Node>();
    }

    public static <T> Node<T> newInstance(final Class<T> type, final int index) {
        return new Node<T>(type, index);
    }

    public static Node newInstance() {
        return new Node(null, 0);
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

    public void setChildNodes(final List<Node> childNodes) {
        this.childNodes = childNodes;
    }

    public List<Node> getChildNodes() {
        return childNodes;
    }

    public void addChildNode(final Node node) {
        childNodes.add(node);
    }

    public void print(final Printer printer) {
        print(printer, "", true);
    }

    private void print(final Printer printer, final String prefix, final boolean isTail) {

        final String detail = type == null ? "*ROOT*" : type.getName() + "|" + index;
        printer.print(prefix + (isTail ? "└── " : "├── ") + detail);
        for (int i = 0; i < childNodes.size() - 1; i++) {
            childNodes.get(i).print(printer, prefix + (isTail ? "    " : "│   "), false);
        }
        if (childNodes.size() > 0) {
            childNodes.get(childNodes.size() - 1).print(printer, prefix + (isTail ? "    " : "│   "), true);
        }
    }
}
