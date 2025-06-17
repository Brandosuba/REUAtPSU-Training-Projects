package com.bcalvario.coverTime.agent;

import javax.swing.*;
import java.awt.*;

public class EnvironmentPanel extends JPanel {
    private final Environment environment;
    private final int cellSize = 20;
    public EnvironmentPanel(Environment environment) {
        this.environment = environment;
        int width = 0;
        int height = 0;
        setPreferredSize(new Dimension(width, height));
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GridCell[][] grid = environment.getGrid();
        for(int i = 0; i < environment.getWidth(); i++) {
            for(int j = 0; j < environment.getHeight(); j++) {
                switch (grid[i][j].getType()){
                    case FOREST -> g.setColor(Color.RED);
                    case ROAD -> g.setColor(Color.GREEN);
                    case CITY -> g.setColor(Color.BLUE);
                }
                g.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
            }
        }
        g.setColor(Color.BLACK);
        for(CarAgent agent : environment.getAgents()){
            int agentX = agent.getLocation().x * cellSize;
            int agenty = agent.getLocation().y * cellSize;
            g.fillOval(agentX, agenty, cellSize, cellSize);
        }
    }
}//
