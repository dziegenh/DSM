package de.uos.se.prom.dsmproject.bl.export;

import de.uos.se.prom.dsmproject.bl.ProjectEditor;
import de.uos.se.prom.dsmproject.bl.SortController;
import de.uos.se.prom.dsmproject.da.export.CsvExporter;
import de.uos.se.prom.dsmproject.da.export.CsvExporter.IDependenciesProvider;
import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Dependency;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.inject.Inject;

/**
 *
 * @author dziegenhagen
 */
public class Exporter {

    @Inject
    SortController sortController;

    @Inject
    ProjectEditor projectEditor;

    public boolean exportAsCsv(File file) throws FileNotFoundException, UnsupportedEncodingException, IOException {

        IDependenciesProvider dependenciesProvider = new IDependenciesProvider() {
            @Override
            public Dependency getDependency(Artifact source, Artifact target) {
                return projectEditor.getDependency(source, target);
            }
        };

        List<Artifact> artifacts = sortController.getSortedArtifacts();

        CsvExporter csvExporter = new CsvExporter();
        return csvExporter.export(file, artifacts, dependenciesProvider, true);
    }

}
