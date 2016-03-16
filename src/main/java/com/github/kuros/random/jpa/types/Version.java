package com.github.kuros.random.jpa.types;

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

    public boolean isSupported(final Version... versions) {
        for (Version version : versions) {
            if (version.getSeries() == getSeries()) {
                return true;
            }
        }
        return false;
    }
}
