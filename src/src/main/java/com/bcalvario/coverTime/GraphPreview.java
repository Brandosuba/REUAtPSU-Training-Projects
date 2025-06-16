package com.bcalvario.coverTime;

import com.bcalvario.coverTime.NonBacktrackingRandomWalk;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxStylesheet;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A utility class for visualizing graphs using JGraphX.
 * This refactored version consolidates component creation and styling to reduce redundancy
 * and supports multiple layouts for comparison.
 *
 * @author Brandon Calvario
 */
public final class GraphPreview {

    private GraphPreview() {
    }

    /**
     * Displays a single, static graph in a new window.
     *
     * @param g     The graph to display.
     * @param title The title for the window.
     */
    public static void show(Graph<Integer, DefaultEdge> g, String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // Use the helper method to create the visual component
        mxGraphComponent comp = createVisualComponent(g);
        frame.getContentPane().add(comp);

        frame.setPreferredSize(new Dimension(800, 600));
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    /**
     * Creates a single window to display and animate two graph simulations side-by-side.
     *
     * @param g1     The first graph.
     * @param s1     The walk strategy for the first graph.
     * @param title1 The title for the first panel.
     * @param g2     The second graph.
     * @param s2     The walk strategy for the second graph.
     * @param title2 The title for the second panel.
     */
    public static void animateTwoWalksSideBySide(
            Graph<Integer, DefaultEdge> g1, WalkStrategy s1, String title1,
            Graph<Integer, DefaultEdge> g2, WalkStrategy s2, String title2) {

        JFrame mainFrame = new JFrame("Side-by-Side Comparison");
        mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        mainFrame.setLayout(new GridLayout(1, 2)); // 1 row, 2 columns

        //left panel
        mxGraphComponent comp1 = createVisualComponent(g1);
        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.add(new JLabel(title1, SwingConstants.CENTER), BorderLayout.NORTH);
        panel1.add(comp1, BorderLayout.CENTER);
        mainFrame.add(panel1);

        //right
        mxGraphComponent comp2 = createVisualComponent(g2);
        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.add(new JLabel(title2, SwingConstants.CENTER), BorderLayout.NORTH);
        panel2.add(comp2, BorderLayout.CENTER);
        mainFrame.add(panel2);

        //animation threads
        JGraphXAdapter<Integer, DefaultEdge> vis1 = (JGraphXAdapter<Integer, DefaultEdge>) comp1.getGraph();
        JGraphXAdapter<Integer, DefaultEdge> vis2 = (JGraphXAdapter<Integer, DefaultEdge>) comp2.getGraph();

        new Thread(() -> runAnimation(g1, s1, vis1, comp1)).start();
        new Thread(() -> runAnimation(g2, s2, vis2, comp2)).start();

        mainFrame.setPreferredSize(new Dimension(1600, 800));
        mainFrame.pack();
        mainFrame.setLocationByPlatform(true);
        mainFrame.setVisible(true);
    }

    /**
     * Creates a single window to display four graph simulations in a 2x2 grid.
     * This allows for a comprehensive comparison of two graph types and two strategies.
     */
    public static void animateFourGraphsInGrid(
            Graph<Integer, DefaultEdge> g1, WalkStrategy s1, String title1, // Top-left
            Graph<Integer, DefaultEdge> g2, WalkStrategy s2, String title2, // Top-right
            Graph<Integer, DefaultEdge> g3, WalkStrategy s3, String title3, // Bottom-left
            Graph<Integer, DefaultEdge> g4, WalkStrategy s4, String title4  // Bottom-right
    ) {
        JFrame mainFrame = new JFrame("2x2 Comparison of Graph Types and Walk Strategies");
        mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        mainFrame.setLayout(new GridLayout(2, 2)); // 2 rows, 2 columns

        //create/add the four panels
        Object[][] configs = {
                {g1, s1, title1}, {g2, s2, title2},
                {g3, s3, title3}, {g4, s4, title4}
        };

        for (Object[] config : configs) {
            Graph<Integer, DefaultEdge> g = (Graph<Integer, DefaultEdge>) config[0];
            WalkStrategy s = (WalkStrategy) config[1];
            String title = (String) config[2];

            mxGraphComponent comp = createVisualComponent(g);
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JLabel(title, SwingConstants.CENTER), BorderLayout.NORTH);
            panel.add(comp, BorderLayout.CENTER);
            mainFrame.add(panel);

            JGraphXAdapter<Integer, DefaultEdge> vis = (JGraphXAdapter<Integer, DefaultEdge>) comp.getGraph();
            new Thread(() -> runAnimation(g, s, vis, comp)).start();
        }

        mainFrame.setPreferredSize(new Dimension(1600, 1600));
        mainFrame.pack();
        mainFrame.setLocationByPlatform(true);
        mainFrame.setVisible(true);
    }

    /**
     * Creates and styles a visual graph component from a JGraphT graph.
     * This consolidates the adapter creation, styling, and layout logic.
     *
     * @param g The JGraphT graph.
     * @return A fully configured mxGraphComponent ready for display.
     */
    private static mxGraphComponent createVisualComponent(Graph<Integer, DefaultEdge> g) {
        JGraphXAdapter<Integer, DefaultEdge> visAdapter = new JGraphXAdapter<>(g);
        setupStyles(visAdapter, g);

        mxGraphComponent comp = new mxGraphComponent(visAdapter);
        comp.setConnectable(false);
        comp.getGraph().setAllowDanglingEdges(false);
        comp.getViewport().setBackground(Color.WHITE);
        return comp;
    }

    /**
     * Applies visual styles and layout to a JGraphX adapter.
     */
    private static void setupStyles(JGraphXAdapter<Integer, DefaultEdge> vis, Graph<Integer, DefaultEdge> g) {
        mxStylesheet stylesheet = vis.getStylesheet();
        Map<String, Object> vertexStyle = stylesheet.getDefaultVertexStyle();
        vertexStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        vertexStyle.put(mxConstants.STYLE_FONTSIZE, 14);
        vertexStyle.put(mxConstants.STYLE_FONTCOLOR, "#FFFFFF");
        vertexStyle.put(mxConstants.STYLE_FILLCOLOR, "#3498DB");
        vertexStyle.put(mxConstants.STYLE_STROKECOLOR, "#2E86C1");
        vertexStyle.put(mxConstants.STYLE_STROKEWIDTH, 2);

        Map<String, Object> edgeStyle = stylesheet.getDefaultEdgeStyle();
        edgeStyle.put(mxConstants.STYLE_EDGE, mxEdgeStyle.EntityRelation);
        edgeStyle.put(mxConstants.STYLE_ROUNDED, true);
        edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.NONE);
        edgeStyle.put(mxConstants.STYLE_STROKECOLOR, "#566573");

        mxCircleLayout layout = new mxCircleLayout(vis);
        int nodeCount = g.vertexSet().size();
        layout.setRadius(Math.max(100, nodeCount * 18));
        layout.setMoveCircle(true);
        layout.execute(vis.getDefaultParent());
    }

    /**
     * Helper method that runs the animation logic for a single graph panel.
     */
    private static void runAnimation(Graph<Integer, DefaultEdge> g, WalkStrategy strategy,
                                     JGraphXAdapter<Integer, DefaultEdge> vis, mxGraphComponent comp) {
        // Style Definitions for Animation
        mxStylesheet stylesheet = vis.getStylesheet();
        Map<String, Object> visitedStyle = new HashMap<>();
        visitedStyle.put(mxConstants.STYLE_FILLCOLOR, "#85C1E9"); // Light Blue for visited
        stylesheet.putCellStyle("VISITED", visitedStyle);
        Map<String, Object> currentStyle = new HashMap<>();
        currentStyle.put(mxConstants.STYLE_FILLCOLOR, "#F1C40F"); // Yellow for current
        stylesheet.putCellStyle("CURRENT", currentStyle);

        // Animation Logic
        Random rand = new Random();
        int n = g.vertexSet().size();
        if (n <= 1) return;

        Map<Integer, mxICell> vertexToCellMap = vis.getVertexToCellMap();
        boolean[] seen = new boolean[n];
        int visited = 0;
        int previous = -1;
        int current = rand.nextInt(n);

        while (visited < n) {
            if (!seen[current]) {
                seen[current] = true;
                visited++;
            }
            vis.setCellStyle("CURRENT", new Object[]{vertexToCellMap.get(current)});
            comp.refresh();
            try {
                Thread.sleep(Constants.ANIMATION_DELAY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            vis.setCellStyle("VISITED", new Object[]{vertexToCellMap.get(current)});

            List<Integer> neighbors = Graphs.neighborListOf(g, current);
            if (neighbors.isEmpty()) break;

            if (strategy instanceof NonBacktrackingRandomWalk && neighbors.size() > 1 && previous != -1) {
                neighbors.remove(Integer.valueOf(previous));
            }
            previous = current;
            current = neighbors.get(rand.nextInt(neighbors.size()));
        }

        // Final styling to show completion
        if (vertexToCellMap.get(previous) != null) {
            vis.setCellStyle("VISITED", new Object[]{vertexToCellMap.get(previous)});
        }
        if (vertexToCellMap.get(current) != null) {
            vis.setCellStyle("CURRENT", new Object[]{vertexToCellMap.get(current)});
        }
        comp.refresh();
    }
}