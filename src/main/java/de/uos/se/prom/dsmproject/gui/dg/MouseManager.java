package de.uos.se.prom.dsmproject.gui.dg;

import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.view.util.DefaultMouseManager;

/**
 *
 * @author dziegenhagen
 */
public class MouseManager extends DefaultMouseManager {

    interface INodeSelectedListener {

        void nodeSelected(Node node);
    }

    boolean hasSelectedNode = false;

    List<INodeSelectedListener> nodeSelectedListeners = new LinkedList<>();

    void addListener(INodeSelectedListener listener) {
        this.nodeSelectedListeners.add(listener);
    }

    @Override
    protected void mouseButtonPressOnElement(GraphicElement element, MouseEvent event) {
        super.mouseButtonPressOnElement(element, event);

        if (!(element instanceof GraphicNode)) {
            return;
        }
        GraphicNode nodeElement = (GraphicNode) element;

        if (nodeElement.hasAttribute("ui.selected")) {
            fireNodeSelected(nodeElement);
        }
    }

    @Override
    protected void mouseButtonPress(MouseEvent event) {
        super.mouseButtonPress(event);

        if (hasSelectedNode) {
            fireNodeSelected(null);
        }
    }

    protected void fireNodeSelected(Node node) {
        hasSelectedNode = null != node;

        for (INodeSelectedListener listener : nodeSelectedListeners) {
            listener.nodeSelected(node);
        }
    }

}
