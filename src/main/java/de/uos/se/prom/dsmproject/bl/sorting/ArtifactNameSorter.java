package de.uos.se.prom.dsmproject.bl.sorting;

import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.DsmSorting;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

class ArtifactNameSorter implements ISortAlgorithm {

    Comparator<Artifact> nameComparator = new Comparator<Artifact>() {

//        StringComp
        @Override
        public int compare(Artifact a1, Artifact a2) {
            return a1.getName().compareToIgnoreCase(a2.getName());
        }

        @Override
        public boolean equals(Object obj) {
            return Objects.equals(this, obj);
        }

    };

    @Override
    public List<Artifact> sort(List<Artifact> artifacts) {
        Collections.sort(artifacts, nameComparator);
        return artifacts;
    }

    @Override
    public DsmSorting getSorting() {
        return DsmSorting.ARTIFACT_NAME;
    }

}
