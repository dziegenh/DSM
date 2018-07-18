package de.uos.se.prom.dsmproject.bl.sorting;

import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import de.uos.se.prom.dsmproject.entity.DsmSorting;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Sorts artifacts by type (first sort key) and by name (second sort key).
 *
 * @author dziegenhagen
 */
class ArtifactTypeSorter implements ISortAlgorithm {

    SortAlgorithmController algoControl;

    Comparator<Artifacttype> typeComparator = new Comparator<Artifacttype>() {

        @Override
        public int compare(Artifacttype a1, Artifacttype a2) {
            return a1.getName().compareToIgnoreCase(a2.getName());
        }

        @Override
        public boolean equals(Object obj) {
            return Objects.equals(this, obj);
        }

    };

    public ArtifactTypeSorter(SortAlgorithmController algoControl) {
        this.algoControl = algoControl;
    }

    @Override
    public List<Artifact> sort(List<Artifact> artifacts) {

        List<Artifact> result = new LinkedList<>();

        HashMap<Artifacttype, List<Artifact>> typesMap = new HashMap<>();

        for (Artifact artifact : artifacts) {
            Artifacttype type = artifact.getType();

            // get or create list of this type's artifacts
            List<Artifact> typeArtifacts = typesMap.get(type);
            if (null == typeArtifacts) {
                typeArtifacts = new LinkedList<>();
                typesMap.put(type, typeArtifacts);
            }

            // add artifact to type's list
            typeArtifacts.add(artifact);
        }

        // sort types by their name
        List<Artifacttype> typesList = new LinkedList<>(typesMap.keySet());
        Collections.sort(typesList, typeComparator);

        for (Artifacttype artifacttype : typesList) {
            List<Artifact> typeArtifacts = typesMap.get(artifacttype);
            typeArtifacts = algoControl.applyAlgorithm(typeArtifacts, DsmSorting.ARTIFACT_NAME);
            result.addAll(typeArtifacts);
        }

        return result;
    }

    @Override
    public DsmSorting getSorting() {
        return DsmSorting.ARTIFACT_TYPE;
    }
}
