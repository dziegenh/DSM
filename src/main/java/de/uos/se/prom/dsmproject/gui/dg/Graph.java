package de.uos.se.prom.dsmproject.gui.dg;

import com.sun.istack.internal.logging.Logger;
import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Dependency;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JPanel;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

/**
 * Graphical visualization of the model. Live updates are possible using the
 * event listener.
 *
 * @author dziegenhagen
 */
public class Graph {

    Logger logger = Logger.getLogger(Graph.class);

    private MultiGraph graph;

    private Viewer viewer;

    // CSS Reference: http://graphstream-project.org/doc/Advanced-Concepts/GraphStream-CSS-Reference/
    private String stylesheet = "graph { fill-color: #f0f000; }";//\n"
//            + "node { fill-color: #e0e0e030; size-mode: fit; text-size: 10; }\n"
//            + "node.userSelected { fill-color: #3030e030;}";

    /**
     * Default text size for all graph elements.
     */
    private final String cssTextSize = " text-size: 10; ";

    /**
     * Default size of port nodes.
     */
    private final String cssPortSize = " size: 20px, 20px; ";
    private final String cssComponentSize = " size-mode: fit; "; //  size: 40px, 22px;

    private ViewPanel view;

    private String defaultArtifactStyle = "fill-color: #e0e0e030; shape: box;" + cssComponentSize + cssTextSize;

    private String selectedArtifactStyle = " fill-color: #3030e030; ";
    private String hiddenArtifactStyle = " visibility-mode: hidden; fill-color: #f01010; ";

    protected final MouseManager mouseManager = new MouseManager();

    public Graph() {
        graph = new MultiGraph("DG");
        graph.setStrict(false);
        graph.addAttribute("ui.stylesheet", stylesheet);

        viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();
        view = viewer.addDefaultView(false);   // false indicates "no JFrame".

        mouseManager.addListener((node) -> {
            nodeInGraphSelected(node);
        });

        view.setMouseManager(mouseManager);
    }

    public JPanel getView() {
        return view;
    }

    void addArtifact(Artifact artifact) {
        final String name = artifact.getName();

        final String componentId = name;
        Node addNode = graph.addNode(componentId);
        addNode.addAttribute("ui.label", name);
        addNode.addAttribute("ui.style", defaultArtifactStyle);
    }

    void clear() {
        graph.clear();
        this.hiddenArtifactNodes.clear();
    }

    void addArtifacts(List<Artifact> artifacts) {
        viewer.disableAutoLayout();

        for (Artifact artifact : artifacts) {
            addArtifact(artifact);
        }

        viewer.enableAutoLayout();
    }

    void removeArtifact(Artifact artifact) {
        removeArtifactEdges(artifact);
        graph.removeNode(artifact.getName());
    }

    void addDependencies(List<Dependency> dependencies) {
        viewer.disableAutoLayout();

        for (Dependency dependency : dependencies) {
            addDependency(dependency);
        }

        viewer.enableAutoLayout();
    }

    boolean addDependency(Dependency dependency) {
        Artifact source = dependency.getSource();
        Artifact target = dependency.getTarget();

        return addDependency(source, target);
    }

    private boolean addDependency(Artifact source, Artifact target) {
        final String sourceName = source.getName();
        final String targetName = target.getName();

        String dependencyId = getDependencyId(sourceName, targetName);

        if (null != graph.getEdge(dependencyId)) {
            return false;
        }

        Node sourceNode = graph.getNode(sourceName);
        Node targetNode = graph.getNode(targetName);

        if (null == sourceNode || null == targetNode) {
            return false;
        }

        addEdge(dependencyId, sourceNode, targetNode);
        return true;
    }

    private void addEdge(String id, Node sourceNode, Node targetNode) {
        // TODO use "directed" property !!!
        Edge addEdge = graph.addEdge(id, sourceNode, targetNode, true);
        addEdge.addAttribute("ui.style", "fill-color: #3333;");
    }

    void removeDependency(Dependency dependency) {
        String dependencyId = getDependencyId(dependency);
        graph.removeEdge(dependencyId);
    }

    String getDependencyId(Dependency dependency) {
        final Artifact source = dependency.getSource();
        final Artifact target = dependency.getTarget();
        return getDependencyId(source.getName(), target.getName());
    }

    private String getDependencyId(String sourceName, String targetName) {
        return "DEP_" + sourceName + "_TO_" + targetName;
    }

    void updateArtifact(Artifact before, Artifact after) {
        // Only perform update if the name has changed
        if (before.getName().equals(after.getName())) {
            return;
        }

        viewer.disableAutoLayout();

        boolean isHidden = hiddenArtifactNodes.containsKey(before);
        Node oldNode = getNodeFor(before);

        addArtifact(after);
        final String newNodeName = after.getName();
        Node newNode = graph.getNode(newNodeName);

        boolean selfDependend = false;

        Collection<Edge> enteringEdges = new LinkedList<>(oldNode.getEnteringEdgeSet());
        for (Edge enteringEdge : enteringEdges) {
            Node sourceNode = enteringEdge.getSourceNode();
            if (sourceNode.getId().equals(before.getName())) {
                selfDependend = true;
            } else {
                String id = getDependencyId(sourceNode.getId(), newNodeName);
                addEdge(id, sourceNode, newNode);
            }
            graph.removeEdge(enteringEdge);
        }

        Collection<Edge> leavingEdgeSet = new LinkedList<>(oldNode.getLeavingEdgeSet());
        for (Edge leavingEdge : leavingEdgeSet) {
            Node targetNode = leavingEdge.getTargetNode();
            if (targetNode.getId().equals(before.getName())) {
                selfDependend = true;
            } else {
                String id = getDependencyId(newNodeName, targetNode.getId());
                addEdge(id, newNode, targetNode);
            }
            graph.removeEdge(leavingEdge);
        }

        if (selfDependend) {
            String id = getDependencyId(newNodeName, newNodeName);
            addEdge(id, newNode, newNode);
        }

        // handle hidden node
        if (isHidden) {
            hideArtifact(after);
        }

        viewer.enableAutoLayout();

        graph.removeNode(oldNode);

    }

    void setDependenciesDirected(boolean directed) {
        // TODO
    }

    void selectionChanged(List<Artifact> added, List<Artifact> removed) {
        for (Artifact artifact : removed) {
            Node node = getNodeFor(artifact);
            String style = node.getAttribute("ui.style");
            style = style.replace(this.selectedArtifactStyle, "");
            node.setAttribute("ui.style", style);
        }
        for (Artifact artifact : added) {
            Node node = getNodeFor(artifact);
            String style = node.getAttribute("ui.style");
            style += this.selectedArtifactStyle;
            node.setAttribute("ui.style", style);
        }
    }

    void showArtifacts(List<Artifact> artifacts, List<Dependency> dependencies) {

        viewer.disableAutoLayout();

        for (Artifact shownArtifact : artifacts) {

            Node node = this.hiddenArtifactNodes.get(shownArtifact);
            if (null != node) {
                Node addedNode = graph.addNode(node.getId());

                logger.log(Level.INFO, "Node added? " + (null != graph.getNode(node.getId())));

                Collection<String> attributeKeySet = node.getAttributeKeySet();
                HashMap<String, Object> attributes = new HashMap<>();
                for (String attributeKey : attributeKeySet) {
                    attributes.put(attributeKey, node.getAttribute(attributeKey));
                }
                addedNode.addAttributes(attributes);
            }

        }

        for (Dependency dependency : dependencies) {
            if (!addDependency(dependency)) {

                logger.log(Level.INFO, "can't add dep: " + dependency);
            }
        }

        viewer.enableAutoLayout();
    }

    void hideArtifacts(List<Artifact> hideArtifacts) {

        viewer.disableAutoLayout();

        for (Artifact hiddenArtifact : hideArtifacts) {

            hideArtifact(hiddenArtifact);
        }

        viewer.enableAutoLayout();
    }

    private void hideArtifact(Artifact hiddenArtifact) {
        if (!hiddenArtifactNodes.containsKey(hiddenArtifact)) {

            Node node = graph.getNode(hiddenArtifact.getName());
            this.hiddenArtifactNodes.put(hiddenArtifact, node);
            //            String style = node.getAttribute("ui.style");
            //            style += this.hiddenArtifactStyle;
            //            node.setAttribute("ui.style", style);
            removeArtifactEdges(hiddenArtifact);
            graph.removeNode(node);
        }
    }

    void close() {
        viewer.close();
    }

    HashMap<Artifact, Node> hiddenArtifactNodes = new HashMap<>();

    interface IGraphSelectionListener {

        void artifactSelected(String name);

        void selectionCleared();
    }

    List<IGraphSelectionListener> graphSelectionListeners = new LinkedList<>();

    void addGraphSelectionListener(IGraphSelectionListener listener) {
        this.graphSelectionListeners.add(listener);
    }

    private void nodeInGraphSelected(Node node) {

        if (null == node) {
            for (IGraphSelectionListener listener : graphSelectionListeners) {
                listener.selectionCleared();
            }
        } else {
            for (IGraphSelectionListener listener : graphSelectionListeners) {
                listener.artifactSelected(node.getId());
            }
        }
    }

    Node getNodeFor(Artifact artifact) {
        Node node = graph.getNode(artifact.getName());
        if (null != node) {
            return node;
        }

        return this.hiddenArtifactNodes.get(artifact);
    }

    void removeArtifactEdges(Artifact artifact) {
        Node node = graph.getNode(artifact.getName());
        Object[] edges = node.getEdgeSet().toArray();

        for (Object edge : edges) {
            graph.removeEdge((Edge) edge);
        }
    }
}
