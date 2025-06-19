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
        //todo implement getgrid
//        GridCell[][] grid = environment.getGrid();
    }
}//
