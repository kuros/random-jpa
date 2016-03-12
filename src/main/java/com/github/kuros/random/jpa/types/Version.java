package com.github.kuros.random.jpa.types;

import java.util.List;

public enum Version {
    V1(1),
    V2(2);

    private int series;

    Version(final int id) {
        this.series = id;
    }

    private int getSeries() {
        return series;
    }

    public boolean isSupported(final Version currentVersion) {
        return currentVersion.getSeries() <= getSeries();
    }
}
