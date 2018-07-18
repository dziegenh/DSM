package de.uos.se.prom.dsmproject.bl.sorting;

import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.DsmSorting;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;

public class SortAlgorithmController {

    /**
     * Available sorting algorithms.
     */
    private final HashMap<DsmSorting, ISortAlgorithm> algorithms = new HashMap<>();

    @PostConstruct
    public void initialize() {
        algorithms.put(DsmSorting.ARTIFACT_NAME, new ArtifactNameSorter());
        algorithms.put(DsmSorting.ARTIFACT_TYPE, new ArtifactTypeSorter(this));
    }

    public List<Artifact> applyAlgorithm(List<Artifact> artifacts, DsmSorting algorithm) {
        LinkedList<Artifact> listCopy = new LinkedList<>(artifacts);
        return this.algorithms.get(algorithm).sort(listCopy);
    }

}
