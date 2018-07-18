package de.uos.se.prom.dsmproject.entity;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Project {

    private String name;

    private boolean directed;

    private boolean dependenciesWeighted;

    private final List<Artifact> artifacts = new LinkedList<>();

    private final List<Dependency> dependencies = new LinkedList<>();

    private final List<View> views = new LinkedList<>();

    private String activeViewName;

    public Project() {
    }

    public Project(String name, boolean directed) {
        this.name = name;
        this.directed = directed;
    }

    public void addArtifact(Artifact artifact) {
        this.artifacts.add(artifact);
    }

    public void removeArtifact(Artifact artifact) {
        this.artifacts.remove(artifact);
    }

    public void addDependency(Dependency dependency) {
        this.dependencies.add(dependency);
    }

    public void removeDependency(Dependency dependency) {
        this.dependencies.remove(dependency);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.name);
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
        final Project other = (Project) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    public void setDependenciesWeighted(boolean weighted) {
        this.dependenciesWeighted = weighted;
    }

    public boolean areDependenciesWeighted() {
        return dependenciesWeighted;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public boolean isDirected() {
        return directed;
    }

    public void addView(View view) {
        this.views.add(view);
    }

    public void removeView(View view) {
        this.views.remove(view);
    }

    public List<View> getViews() {
        return views;
    }

    public String getActiveViewName() {
        return activeViewName;
    }

    public void setActiveViewName(String activeViewName) {
        this.activeViewName = activeViewName;
    }

    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public View getActiveView() {
        for (View view : this.views) {
            if (view.getName().equals(this.activeViewName)) {
                return view;
            }
        }

        return null;
    }

}
