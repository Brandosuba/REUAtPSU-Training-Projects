package com.bcalvario.coverTime.agent;

import javax.swing.*;
import java.awt.*;

public class EnvironmentPanel extends JPanel {
    private final Environment environment;
    private final int cellSize = 10;

    public EnvironmentPanel(Environment environment) {
        this.environment = environment;
        // This must be calculated to give the window a size.
        int panelWidth = environment.getWidth() * cellSize;
        int panelHeight = environment.getHeight() * cellSize;
        setPreferredSize(new Dimension(panelWidth, panelHeight));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // This logic is required to draw the cells.
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GridCell[][] grid = environment.getGrid();

        if (grid == null) {
            return;
        }

        // Draw the grid terrain
        for (int x = 0; x < environment.getWidth(); x++) {
            for (int y = 0; y < environment.getHeight(); y++) {
                if (grid[x][y] != null) {
                    switch (grid[x][y].getType()) {
                        case FOREST -> g2d.setColor(new Color(34, 139, 34));
                        case ROAD   -> g2d.setColor(Color.DARK_GRAY);
                        case CITY   -> g2d.setColor(Color.BLUE); // This draws the cities
                    }
                    g2d.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }

        // Draw the agents
        g2d.setColor(Color.YELLOW);
        for (CarAgent agent : environment.getAgents()) {
            if (agent.getLocation() != null) {
                int agentX = agent.getLocation().x * cellSize;
                int agentY = agent.getLocation().y * cellSize;
                g2d.fillOval(agentX, agentY, cellSize, cellSize);
            }
        }
    }
}

