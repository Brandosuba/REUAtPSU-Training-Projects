package com.bcalvario.coverTime.agent;

import javax.swing.*;
import java.awt.*;

public class EnvironmentPanel extends JPanel {
    private final Environment environment;
    private final int cellSize = 10;

    public EnvironmentPanel(Environment environment) {
        this.environment = environment;
        int panelWidth = environment.getWidth() * cellSize;
        int panelHeight = environment.getHeight() * cellSize;
        setPreferredSize(new Dimension(panelWidth, panelHeight));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        GridCell[][] grid = environment.getGrid();

        if (grid == null) return;

        // Draw the grid terrain
        for (int x = 0; x < environment.getWidth(); x++) {
            for (int y = 0; y < environment.getHeight(); y++) {
                if (grid[x][y] != null) {
                    switch (grid[x][y].getType()) {
                        case FOREST  -> g2d.setColor(new Color(34, 139, 34));
                        case ROAD    -> g2d.setColor(Color.DARK_GRAY);
                        case FREEWAY -> g2d.setColor(Color.ORANGE); // Color for freeways
                        case CITY    -> g2d.setColor(Color.BLUE);
                    }
                    g2d.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }

        // Draw the agents
        for (CarAgent agent : environment.getAgents()) {
            Point loc = agent.getLocation();
            if (loc != null) {
                // Use the agent's specific color
                g2d.setColor(agent.getColor());
                int agentX = loc.x * cellSize;
                int agentY = loc.y * cellSize;
                g2d.fillOval(agentX, agentY, cellSize, cellSize);
            }
        }
    }
}
