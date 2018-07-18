package de.uos.se.prom.dsmproject.entity;

import java.util.Objects;

public class Dependency {

    private Artifact source;

    private Artifact target;

    private double weight;

    public Dependency() {
    }

    public Dependency(Artifact source, Artifact target, double weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    public Artifact getSource() {
        return source;
    }

    public Artifact getTarget() {
        return target;
    }

    public void setSource(Artifact source) {
        this.source = source;
    }

    public void setTarget(Artifact target) {
        this.target = target;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.source);
        hash = 97 * hash + Objects.hashCode(this.target);
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
        final Dependency other = (Dependency) obj;
        if (!Objects.equals(this.source, other.source)) {
            return false;
        }
        if (!Objects.equals(this.target, other.target)) {
            return false;
        }
        return true;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Dependency{" + "source=" + source + ", target=" + target + ", weight=" + weight + '}';
    }

}
