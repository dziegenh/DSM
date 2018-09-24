package de.uos.se.prom.dsmproject.da.entity;

import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import de.uos.se.prom.dsmproject.entity.Dependency;
import de.uos.se.prom.dsmproject.entity.Project;
import de.uos.se.prom.dsmproject.entity.View;
import java.util.LinkedList;
import org.apache.commons.text.StringEscapeUtils;

/**
 *
 * @author dziegenhagen
 */
public class PersistedTypesFactory {

    public PersistedProject create(Project project) {

        PersistedProject pProject = new PersistedProject();
        pProject.activeViewName = encodeString(project.getActiveViewName());
        pProject.name = encodeString(project.getName());
        pProject.directed = project.isDirected();
        pProject.dependenciesWeighted = project.areDependenciesWeighted();

        pProject.artifacttypes = new LinkedList<>();
        pProject.artifacts = new LinkedList<>();
        for (Artifact artifact : project.getArtifacts()) {
            PersistedArtifact pArtifact = create(artifact);
            pProject.artifacts.add(pArtifact);

            PersistedArtifacttype pArtifacttype = create(artifact.getType());

            if (!pProject.artifacttypes.contains(pArtifacttype)) {
                pProject.artifacttypes.add(pArtifacttype);
            }
        }

        pProject.dependencies = new LinkedList<>();
        for (Dependency dependency : project.getDependencies()) {
            PersistedDependency pDependency = create(dependency);
            pProject.dependencies.add(pDependency);
        }

        pProject.views = new LinkedList<>();
        for (View view : project.getViews()) {
            PersistedView pView = create(view);
            pProject.views.add(pView);
        }

        return pProject;
    }
    
    
    
    

    public PersistedArtifact create(Artifact artifact) {
        PersistedArtifact pArtifact = new PersistedArtifact();

        pArtifact.name = encodeString(artifact.getName());
        pArtifact.typeName = encodeString(artifact.getType().getName());

        return pArtifact;
    }

    public PersistedArtifacttype create(Artifacttype artifacttype) {
        PersistedArtifacttype pArtifacttype = new PersistedArtifacttype();

        pArtifacttype.name = encodeString(artifacttype.getName());

        return pArtifacttype;
    }

    public PersistedDependency create(Dependency dependency) {
        PersistedDependency pDependency = new PersistedDependency();

        pDependency.sourceArtifactName = encodeString(dependency.getSource().getName());
        pDependency.targetArtifactName = encodeString(dependency.getTarget().getName());
        pDependency.weight = dependency.getWeight();

        return pDependency;
    }

    public PersistedView create(View view) {
        PersistedView pView = new PersistedView();

        pView.name = encodeString(view.getName());
        pView.dsmSortingName = encodeString(view.getSorting().name());

        pView.hiddenTypesNames = new LinkedList<>();
        for (Artifacttype hiddenType : view.getHiddenTypes()) {
            pView.hiddenTypesNames.add(hiddenType.getName());
        }

        return pView;
    }

    public String encodeString(String string) {
        return StringEscapeUtils.escapeXml11(string);
    }
}
