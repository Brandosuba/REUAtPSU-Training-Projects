# Mini Project 2/3: Extending a Random Network Model to a Car-Road ABM. 
The random network represents a road network connecting cities where nodes are cities and roads are links.<br> 
The virtual environment in the ABM is composed of a set of roads, cities, and empty space (forest). <br>
An agent in the ABM is a car that drives on the road (cannot drive in forest).  <br> 

**Understanding the ABM** <br>
We used a grid-world approach: the environment is split up into a # of grid cells. <br>
Agents (cars) can only move from one grid cell to the next. <br>
Grid cells have a limit on how many cars/people can occupy them. <br>
We also keep track of which cities cars have visited. <br>
