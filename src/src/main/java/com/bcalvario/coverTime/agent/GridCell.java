package com.bcalvario.coverTime.agent;

import java.util.ArrayList;
import java.util.List;

public class GridCell {
    private CellType type;
    private final int capacity;
    private final List<CarAgent> occupants;

    public GridCell(CellType type) {
        this.type = type;
        // Freeways and roads can hold more agents
        this.capacity = (type == CellType.ROAD || type == CellType.FREEWAY || type == CellType.CITY) ? 5 : 1;
        this.occupants = new ArrayList<>();
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public CellType getType() {
        return type;
    }
}