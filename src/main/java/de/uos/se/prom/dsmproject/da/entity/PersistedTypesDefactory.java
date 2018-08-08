package de.uos.se.prom.dsmproject.da.entity;

import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import de.uos.se.prom.dsmproject.entity.Dependency;
import de.uos.se.prom.dsmproject.entity.DsmSorting;
import de.uos.se.prom.dsmproject.entity.Project;
import de.uos.se.prom.dsmproject.entity.View;
import java.util.HashMap;
import org.apache.commons.text.StringEscapeUtils;

/**
 *
 * @author dziegenhagen
 */
public class PersistedTypesDefactory {

    private HashMap<String, Artifacttype> nameToTypes;
    private HashMap<String, Artifact> nameToArtifact;
    private HashMap<String, DsmSorting> nameToSorting;

    private final Artifacttype unknownType = new Artifacttype("?");
    private final Artifact unkownArtifact = new Artifact(unknownType, "?");

    public Project create(PersistedProject pProject) {
        nameToTypes = new HashMap<>();
        nameToArtifact = new HashMap<>();
        nameToSorting = new HashMap<>();

        for (DsmSorting sorting : DsmSorting.values()) {
            this.nameToSorting.put(sorting.name(), sorting);
        }

        Project project = new Project(decodeString(pProject.name), pProject.directed);
        project.setActiveViewName(decodeString(pProject.activeViewName));
        project.setDependenciesWeighted(pProject.dependenciesWeighted);

        if (null != pProject.artifacttypes) {
            for (PersistedArtifacttype pArtifacttype : pProject.artifacttypes) {
                Artifacttype type = create(pArtifacttype);
                nameToTypes.put(type.getName(), type);
            }
        }

        if (null != pProject.artifacts) {
            for (PersistedArtifact pArtifact : pProject.artifacts) {
                Artifact artifact = create(pArtifact);
                nameToArtifact.put(artifact.getName(), artifact);
                project.addArtifact(artifact);
            }
        }

        if (null != pProject.dependencies) {
            for (PersistedDependency pDependency : pProject.dependencies) {
                Dependency dependency = create(pDependency);
                project.addDependency(dependency);
            }
        }

        if (null != pProject.views) {
            for (PersistedView pView : pProject.views) {
                View view = create(pView);
                project.addView(view);
            }
        }

        nameToTypes.clear();
        nameToArtifact.clear();
        nameToSorting.clear();

        return project;
    }

    public String decodeString(String string) {
        return StringEscapeUtils.unescapeXml(string);
    }

    private Artifacttype create(PersistedArtifacttype pArtifacttype) {
        return new Artifacttype(decodeString(pArtifacttype.name));
    }

    private Artifact create(PersistedArtifact pArtifact) {
        Artifacttype artifacttype = this.nameToTypes.get(pArtifact.typeName);

        if (null == artifacttype) {
            artifacttype = unknownType;
        }

        return new Artifact(artifacttype, decodeString(pArtifact.name));
    }

    private Dependency create(PersistedDependency pDependency) {
        final String sourceArtifactName = decodeString(pDependency.sourceArtifactName);
        final String targetArtifactName = decodeString(pDependency.targetArtifactName);
        Artifact source = nameToArtifact.get(sourceArtifactName);
        Artifact target = nameToArtifact.get(targetArtifactName);

        if (null == source) {
            source = unkownArtifact;
            // TODO log error?!
        }
        if (null == target) {
            target = unkownArtifact;
            // TODO log error?!
        }

        return new Dependency(source, target, pDependency.weight);
    }

    /**
     * If the persisted view doesn't contain a DsmSorting, the first DsmSorting
     * enum item will be used.
     *
     * @param pView
     * @return
     */
    private View create(PersistedView pView) {
        DsmSorting sorting = nameToSorting.get(pView.dsmSortingName);
        if (null == sorting) {
            sorting = DsmSorting.values()[0];
        }

        View view = new View();
        view.setName(decodeString(pView.name));
        view.setSorting(sorting);

        return view;
    }

}
