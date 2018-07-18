package de.uos.se.prom.dsmproject.bl;

import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import de.uos.se.prom.dsmproject.entity.Dependency;
import de.uos.se.prom.dsmproject.entity.DsmSorting;
import de.uos.se.prom.dsmproject.entity.Project;
import de.uos.se.prom.dsmproject.entity.View;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author dziegenhagen
 */
public class DummyProjectFactory {

    /**
     * creates a mock / dummy project for development!!
     */
    Project DEV_createDummyProject() {

        final String projectName = "DUMMY_" + System.currentTimeMillis();
        Project project = new Project(projectName, true);

        Artifacttype type1 = new Artifacttype("Type1");
        Artifacttype type2 = new Artifacttype("Type2");

        Artifact artifact1 = new Artifact(type1, "Test Art 1");
        Artifact artifact2 = new Artifact(type1, "Test Art 2");
        Artifact artifact3 = new Artifact(type2, "Test Art 3");
        Artifact artifact4 = new Artifact(type2, "Test Art 4");

        Dependency dependency_1_2 = new Dependency(artifact1, artifact2, 1);
        Dependency dependency_2_3 = new Dependency(artifact2, artifact3, 1);
        Dependency dependency_2_4 = new Dependency(artifact2, artifact4, 1);

        project.addArtifact(artifact1);
        project.addArtifact(artifact2);
        project.addArtifact(artifact3);
        project.addArtifact(artifact4);

        project.addDependency(dependency_1_2);
        project.addDependency(dependency_2_3);
        project.addDependency(dependency_2_4);

        addView(project);

        return project;
    }

    private void addView(Project project) {
        View view = new View();
        view.setName("View 1");
        view.setSorting(DsmSorting.ARTIFACT_NAME);

        project.addView(view);
        project.setActiveViewName(view.getName());
    }

    private final int NUM_TYPES = 5;
    private final int NUM_ARTIFACTS = 7;
    private final int NUM_MAX_DEPENDENCIES = 30;

    Project DEV_createRandomDummyProject() {

        Random random = new Random(System.currentTimeMillis());

        final String projectName = "RAND_DUMMY_" + System.currentTimeMillis();
        Project project = new Project(projectName, true);

        List<Artifacttype> types = new LinkedList<>();
        for (int i = 0; i < NUM_TYPES; i++) {
            types.add(new Artifacttype("Type" + i));
        }

        List<Artifact> artifacts = new LinkedList<>();
        for (int i = 0; i < NUM_ARTIFACTS; i++) {
            int typeIdx = random.nextInt(types.size());
            Artifacttype type = types.get(typeIdx);
            String name = "Artifact " + i;

            final Artifact artifact = new Artifact(type, name);
            artifacts.add(artifact);
            project.addArtifact(artifact);
        }

        List<Dependency> dependencies = new LinkedList<>();
        for (int i = 0; i < NUM_MAX_DEPENDENCIES; i++) {
            int sourceIdx = random.nextInt(artifacts.size());
            int targetIdx = random.nextInt(artifacts.size());

            Artifact source = artifacts.get(sourceIdx);
            Artifact target = artifacts.get(targetIdx);

            Dependency dependency = new Dependency(source, target, 1);

            if (!dependencies.contains(dependency)) {
                project.addDependency(dependency);
            }
        }

        addView(project);

        return project;
    }

}
