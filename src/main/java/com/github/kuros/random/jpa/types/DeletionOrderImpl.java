package com.github.kuros.random.jpa.types;

import java.util.List;

public final class DeletionOrderImpl implements DeletionOrder {
    private List<Object> deletionOrder;

    private DeletionOrderImpl(final List<Object> deletionOrder) {
        this.deletionOrder = deletionOrder;
    }

    public static DeletionOrder create(List<Object> deletionOrder) {
        return new DeletionOrderImpl(deletionOrder);
    }

    public List<Object> getDeletionOrders() {
        return deletionOrder;
    }
}
