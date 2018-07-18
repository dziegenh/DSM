package de.uos.se.prom.dsmproject.bl;

import com.airhacks.afterburner.injection.Injector;
import de.uos.se.prom.dsmproject.bl.application.ExceptionHandler;
import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import de.uos.se.prom.dsmproject.entity.Dependency;
import de.uos.se.prom.dsmproject.entity.Project;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dziegenhagen
 */
public class ProjectEditorTest {

    private ProjectEditor instance;
    private Artifact artifact1;
    private Artifact artifact2;
    private Artifact artifact4;
    private Artifact artifact3;
    private Dependency dependency_1_2;
    private Dependency dependency_2_3;
    private Dependency dependency_2_4;

    Logger logger = Logger.getGlobal();

    public ProjectEditorTest() {
    }

    @Before
    public void setUp() {
        instance = Injector.instantiateModelOrService(ProjectEditor.class);

        instance.exceptionHandler = new ExceptionHandler() {
            @Override
            public void exceptionCatched(Exception exception) {
                logger.log(Level.WARNING, null, exception);
            }

        };

        // directly create a project to avoid the project controller
        instance.currentProject = new Project("Testproject", true);

        artifact1 = new Artifact(new Artifacttype(), "Test Art 1");
        artifact2 = new Artifact(new Artifacttype(), "Test Art 2");
        artifact3 = new Artifact(new Artifacttype(), "Test Art 3");
        artifact4 = new Artifact(new Artifacttype(), "Test Art 4");

        dependency_1_2 = new Dependency(artifact1, artifact2, 1);
        dependency_2_3 = new Dependency(artifact2, artifact3, 1);
        dependency_2_4 = new Dependency(artifact2, artifact4, 1);
    }

    @After
    public void tearDown() {
        Injector.forgetAll();
    }

    /**
     * Test of addArtifact method, of class ProjectEditor.
     */
    @Test
    public void testAddArtifact() {
        System.out.println("addArtifact");
        insertArtifacts();

        List<Artifact> artifacts = instance.getArtifacts();
        Assert.assertTrue("Artifact 1 missing", artifacts.contains(artifact1));
        Assert.assertTrue("Artifact 2 missing", artifacts.contains(artifact2));
        Assert.assertTrue("Artifact 3 missing", artifacts.contains(artifact3));
        Assert.assertTrue("Artifact 4 missing", artifacts.contains(artifact4));
    }

    /**
     * Test of addDependency method, of class ProjectEditor.
     */
    @Test
    public void testAddDependency() {
        System.out.println("addDependency");

        insertArtifacts();

        instance.addDependency(dependency_1_2);
        instance.addDependency(dependency_2_3);
        instance.addDependency(dependency_2_4);

        // TODO test result?
    }

    /**
     * Test of getCurrentProject method, of class ProjectEditor.
     */
    @Test
    public void testGetCurrentProject() {
        System.out.println("getCurrentProject");
        Project result = instance.getCurrentProject();

        // TODO test method with saving + loading a project
        Assert.assertTrue("Wrong project returned", result.equals(instance.currentProject));
    }

    /**
     * Test of getDependenciesFrom method, of class ProjectEditor.
     */
    @Test
    public void testGetDependenciesFrom() {
        System.out.println("getDependenciesFrom");
        List<Dependency> expResult = Arrays.asList(new Dependency[]{dependency_2_3, dependency_2_4});

        insertArtifacts();

        instance.addDependency(dependency_1_2);
        instance.addDependency(dependency_2_3);
        instance.addDependency(dependency_2_4);

        List<Dependency> result = instance.getDependenciesFrom(dependency_2_3.getSource());
        assertEquals(expResult, result);
    }

    /**
     * Test of getDependenciesTo method, of class ProjectEditor.
     */
    @Test
    public void testGetDependenciesTo() {
        System.out.println("getDependenciesTo");
        List<Dependency> expResult = Arrays.asList(new Dependency[]{dependency_2_4});

        insertArtifacts();

        instance.addDependency(dependency_1_2);
        instance.addDependency(dependency_2_3);
        instance.addDependency(dependency_2_4);

        List<Dependency> result = instance.getDependenciesTo(dependency_2_4.getTarget());
        assertEquals(expResult, result);
    }

    /**
     * Test of removeDepenendency method, of class ProjectEditor.
     */
    @Test
    public void testRemoveDepenendency() {
        System.out.println("removeDepenendency");

        insertArtifacts();

        instance.addDependency(dependency_1_2);
        instance.addDependency(dependency_2_3);
        instance.addDependency(dependency_2_4);

        instance.removeDepenendency(dependency_2_3);

        Collection<List<Dependency>> values = instance.artifactSourceDependencies.values();
        for (List<Dependency> depList : values) {
            assertFalse(depList.contains(dependency_2_3));
        }

        values = instance.artifactTargetDependencies.values();
        for (List<Dependency> depList : values) {
            assertFalse(depList.contains(dependency_2_3));
        }
    }

    /**
     * Test of removeArtifact method, of class ProjectEditor.
     */
    @Test
    public void testRemoveArtifact() {
        System.out.println("removeArtifact");

        insertArtifacts();
        instance.removeArtifact(artifact2);

        Set<Artifact> artifacts = instance.artifactSourceDependencies.keySet();
        assertFalse(artifacts.contains(artifact2));

        artifacts = instance.artifactTargetDependencies.keySet();
        assertFalse(artifacts.contains(artifact2));
    }

    private void insertArtifacts() {
        instance.addArtifact(artifact1);
        instance.addArtifact(artifact2);
        instance.addArtifact(artifact3);
        instance.addArtifact(artifact4);
    }

}
