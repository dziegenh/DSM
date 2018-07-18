package de.uos.se.prom.dsmproject.entity;

import java.util.Objects;

public class Artifact {

    private Artifacttype type;

    private String name;

    public Artifact(Artifacttype type, String name) {
        this.type = type;
        this.name = name;
    }

    public Artifact(Artifact artifact) {
        this(artifact.type, artifact.name);
    }

    public String getName() {
        return name;
    }

    public void setType(Artifacttype type) {
        this.type = type;
    }

    public Artifacttype getType() {
        return type;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Artifact other = (Artifact) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Artifact{" + "type=" + type + ", name=" + name + '}';
    }

}
