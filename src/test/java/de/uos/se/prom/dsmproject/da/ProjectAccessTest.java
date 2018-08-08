package de.uos.se.prom.dsmproject.da;

import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Artifacttype;
import de.uos.se.prom.dsmproject.entity.Dependency;
import de.uos.se.prom.dsmproject.entity.Project;
import java.io.File;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author dziegenhagen
 */
public class ProjectAccessTest {

    private final String TESTFILE = "test.dsm";
    private static Project project;

    public ProjectAccessTest() {
    }

    @BeforeClass
    public static void setUpClass() {

        project = new Project("ProjectAccessTest", true);
        Artifacttype type1 = new Artifacttype("T1");
        Artifacttype type2 = new Artifacttype("T2");
        Artifact artifact1 = new Artifact(type1, "A1");
        Artifact artifact2 = new Artifact(type1, "A2");
        Artifact artifact3 = new Artifact(type2, "A3");
        Artifact artifact4 = new Artifact(type2, "A4");
        project.addArtifact(artifact1);
        project.addArtifact(artifact2);
        project.addArtifact(artifact3);
        project.addArtifact(artifact4);
        Dependency dependency1 = new Dependency(artifact1, artifact2, 0.1);
        Dependency dependency2 = new Dependency(artifact1, artifact3, 0.13);
        Dependency dependency3 = new Dependency(artifact2, artifact3, 0.21);
        Dependency dependency4 = new Dependency(artifact2, artifact4, 0.2);
        Dependency dependency5 = new Dependency(artifact3, artifact4, 0.2);
        project.addDependency(dependency1);
        project.addDependency(dependency2);
        project.addDependency(dependency3);
        project.addDependency(dependency4);
        project.addDependency(dependency5);
    }

    @After
    public void tearDown() {
        File testfile = new File(TESTFILE);
        if (testfile.exists()) {
            testfile.delete();
        }
    }

    /**
     * Test of loadProject method, of class ProjectAccess.
     */
    @Test
    public void testLoadProject() throws Exception {
        System.out.println("loadProject");

        // create file for testing
        saveProject();

        File file = new File(TESTFILE);
        ProjectAccess instance = new ProjectAccess();
        Project expResult = project;
        Project result = instance.loadProject(file);
        assertEquals(expResult, result);
    }

    /**
     * Test of saveProject method, of class ProjectAccess.
     */
    @Test
    public void testSaveProject() throws Exception {
        System.out.println("saveProject");
        File testfile = new File(TESTFILE);
        if (testfile.exists()) {
            testfile.delete();
        }

        saveProject();

        assertTrue("Testfile doesn't exist after saving!", testfile.exists());
    }

    private void saveProject() throws TransformerException, ParserConfigurationException {
        File file = new File(TESTFILE);
        ProjectAccess instance = new ProjectAccess();
        instance.saveProject(project, file);
    }

}
