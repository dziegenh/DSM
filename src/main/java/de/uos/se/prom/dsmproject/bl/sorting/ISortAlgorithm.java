package de.uos.se.prom.dsmproject.bl.sorting;

import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.DsmSorting;
import java.util.List;

public interface ISortAlgorithm {

    /**
     * Applys the sorting algorithm to the given artifacts.
     */
    List<Artifact> sort(List<Artifact> artifacts);

    DsmSorting getSorting();

}
