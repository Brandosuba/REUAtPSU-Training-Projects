// Car driving on road network
// Has a current position & memory of visited cities
public class CarAgent {
    // Declare car agent properties
    private int x;
    private int y;
    private AgentState state;
    private Set<String> visitedCities;

    // Constructor for CarAgent object
    public CarAgent(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.visitedCities = new HashSet<>();
    } // CarAgent

    // Update a car agent's position
    public void updatePosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    } // updatePosition

    // Add a visited city to a car agent's memory of visited cities
    public void addCity (int cityX, int cityY) {
        visitedCities.add(cityX + ", " + cityY);
    } // addCity

    // Check if a car agent has visited a city
    public boolean hasVisited(int cityX, int cityY) {
        return visitedCities.contains(cityX + ", " + cityY);
    } // hasVisited
} // CarAgent