package com.bcalvario.coverTime.agent;

import java.util.ArrayList;
import java.util.List;

// Properties of our grid in each cell (its type, how many agents occupy it, add/remove agents)
// Can ask user for input OR place constraints for each CellType's capacity ??/
public class GridCell {
    // Declare grid cell properties
    private int x;
    private int y;
    private CellType type;
    private int capacity;
    private int numAgents;
    private final List<CarAgent> occupants;

    // Constructor for GridCell object
    public GridCell(int x, int y, CellType type, int capacity, List<CarAgent> occupants) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.capacity = capacity;
        this.occupants = new ArrayList<>();
        this.numAgents = 0;
    } // GridCell
    public GridCell(CellType type) {
        this.type = type;
        this.capacity = (type == CellType.ROAD ||type == CellType.CITY ? 5 : 1);
        this.occupants = new ArrayList<>();
    }

    // Check if GridCell is full or not - can/cannot add an agent
    public boolean isFull() {
        return occupants.size() >= capacity;
    }
    public void setType(CellType type) {
        this.type = type;
    }

     // Add an agent if GridCell is not full
//     public void addAgent(CarAgent agent) {
//        if(!isFull()){
//            occupants.add(agent);
//        }
//     } // addAgent
//
//     // Remove an agent if number of agents is not none
//     public boolean removeAgent(CarAgent agent) {
//        occupants.remove(agent);
//     } // removeAgent
    public CellType getType() {
        return type;
    }

} // GridCell