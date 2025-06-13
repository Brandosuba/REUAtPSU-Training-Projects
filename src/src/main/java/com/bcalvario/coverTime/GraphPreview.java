package com.bcalvario.coverTime;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxStylesheet;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
/*
Brandon Calvario
 */
public final class GraphPreview {
    private GraphPreview() {
    }

    public static void show(Graph<Integer, DefaultEdge> g, String title) {
        JGraphXAdapter<Integer, DefaultEdge> vis = new JGraphXAdapter<>(g);
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

        mxGraphComponent comp = new mxGraphComponent(vis);
        comp.setConnectable(false);
        comp.getGraph().setAllowDanglingEdges(false);

        comp.getViewport().setBackground(Color.WHITE);

        JFrame f = new JFrame(title);
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        f.getContentPane().add(comp);
        f.setPreferredSize(new Dimension(800, 600));
        f.pack();
        f.setLocationByPlatform(true);
        f.setVisible(true);
    }
}