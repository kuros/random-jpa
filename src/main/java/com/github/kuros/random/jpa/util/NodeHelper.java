package com.github.kuros.random.jpa.util;

import com.github.kuros.random.jpa.types.ClassIndex;
import com.github.kuros.random.jpa.types.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public final class NodeHelper {

    public static List<ClassIndex> getClassIndexInOrder(final Node node, final Class<?> type, final int index) {
        final Stack<ClassIndex> stack = new Stack<ClassIndex>();
        final Node root = Node.newInstance();
        root.addChildNode(node);

        find(stack, root, type, index);

        final List<ClassIndex> result = new ArrayList<ClassIndex>();
        while (!stack.empty()) {
            result.add(stack.pop());
        }
        return result;
    }

    private static boolean find(final Stack<ClassIndex> stack, final Node root, final Class<?> type, final int index) {

        if (root.getType() == type && root.getIndex() == index) {
            stack.pop();
            return true;
        }

        boolean found = false;
        final List<Node> childNodes = root.getChildNodes();
        for (final Node childNode : childNodes) {
            stack.add(ClassIndex.newInstance(childNode.getType(), childNode.getIndex()));
            found = find(stack, childNode, type, index);
            if (found) {
                break;
            } else {
                stack.remove(ClassIndex.newInstance(childNode.getType(), childNode.getIndex()));
            }
        }

        return found;
    }
}
