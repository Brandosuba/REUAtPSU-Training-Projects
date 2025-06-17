// Properties of our grid in each cell (its type, how many agents occupy it, add/remove agents)
// Can ask user for input OR place constraints for each CellType's capacity ??/
public class GridCell {
    // Type of grid cell
    public enum CellType {
        CITY, 
        ROAD, 
        FOREST
    } // CellType enum

    // Declare grid cell properties
    private int x;
    private int y;
    private CellType type;
    private int capacity;
    private int numAgents; 

    // Constructor for GridCell object
    public GridCell(int x, int y, CellType type, int capacity) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.capacity = capacity;
        this.numAgents = 0;
    } // GridCell

    // Check if GridCell is full or not - can/cannot add an agent
    public boolean isNotFull() {
        return (numAgents < capacity) && (type != CellType.FOREST);
    } // isNotFull

     // Add an agent if GridCell is not full
     public boolean addAgent() {
        if(isNotFull()) {
            numAgents++;
            return true;
        } // if
        return false;
     } // addAgent

     // Remove an agent if number of agents is not none
     public boolean removeAgent() {
        if (numAgents > 0) {
            numAgents--;
            return true;
        }
        return false;
     } // removeAgent

} // GridCell