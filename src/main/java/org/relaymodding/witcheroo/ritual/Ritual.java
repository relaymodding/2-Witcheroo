package org.relaymodding.witcheroo.ritual;

public enum Ritual {
    STAFF_FROM_TREE(100);

    private final int maxTime;

    Ritual(int maxTime) {
        this.maxTime = maxTime;
    }

    public int maxTime() {
        return maxTime;
    }
}
