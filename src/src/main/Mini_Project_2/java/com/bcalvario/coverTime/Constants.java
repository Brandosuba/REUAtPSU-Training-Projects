package com.bcalvario.coverTime;
/*
Brandon Calvario
 */

public final class Constants {
    private Constants() {}
    //number of times coniguration is ran for each configuration of n/c graph type and strategy.
    //more runs == more accurate results
    public static final int RUNS_PER_CONFIG = 10;
    //flag to enable and disable preview
    public static final boolean SHOW_GRAPHS = true;
    // max number of nodes
    public static final int PREVIEW_MAX = 12;

    public static final int ANIMATION_DELAY = 400;
}
