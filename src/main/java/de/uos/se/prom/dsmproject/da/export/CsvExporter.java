package de.uos.se.prom.dsmproject.da.export;

import de.uos.se.prom.dsmproject.entity.Artifact;
import de.uos.se.prom.dsmproject.entity.Dependency;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

/**
 *
 * @author dziegenhagen
 */
public class CsvExporter {

    public interface IDependenciesProvider {

        Dependency getDependency(Artifact source, Artifact target);
    }

    public boolean export(File file, List<Artifact> artifacts, IDependenciesProvider dependenciesProvider, boolean weighted) throws FileNotFoundException, UnsupportedEncodingException, IOException {

        if (!file.exists()) {
            if (!file.createNewFile()) {
                return false;
            }
        }

        if (!file.canWrite()) {
            return false;
        }

        OutputStream out = new FileOutputStream(file);
        try (Writer w = new OutputStreamWriter(out, "UTF-8")) {

            // column labels for type and artifact name
            writeCsvValue(w, "Artifacttype");
            writeCsvValue(w, "Artifactname");

            for (Artifact artifact : artifacts) {
                writeCsvValue(w, artifact.getName());
            }
            endCsvLine(w);

            for (Artifact artifact : artifacts) {
                writeCsvValue(w, artifact.getType().getName());
                writeCsvValue(w, artifact.getName());

                String depValue;
                for (Artifact target : artifacts) {
                    Dependency dependency = dependenciesProvider.getDependency(artifact, target);
                    if (null != dependency) {
                        if (weighted) {
                            depValue = "" + dependency.getWeight();
                        } else {
                            depValue = "X";
                        }
                    } else {
                        depValue = "";
                    }

                    writeCsvValue(w, depValue);
                }

                endCsvLine(w);
            }

        }

        return true;
    }

    // TODO make seperation/encapsulation chars configurable (e.g. properties)
    private final String valueEncapsulation = "\"";
    private final String valueSeparation = ",";

    private void writeCsvValue(Writer w, String value) throws IOException {
        w.write(valueEncapsulation);
        w.write(value);
        w.write(valueEncapsulation);
        w.write(valueSeparation);
    }

    private void endCsvLine(Writer w) throws IOException {
        w.write("\r\n");
    }

}
