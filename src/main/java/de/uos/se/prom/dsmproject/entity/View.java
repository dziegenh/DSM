package de.uos.se.prom.dsmproject.entity;

import java.util.LinkedList;
import java.util.List;

public class View {

    /**
     * List of artifacttypes which are not visible in this view.
     */
    private final List<Artifacttype> hiddenTypes = new LinkedList<>();

    private DsmSorting sorting;

    private String name;

    public View() {
    }

    public View(View original) {
        this.name = original.name;
        this.sorting = original.sorting;

        for (Artifacttype hiddenType : original.hiddenTypes) {
            this.hiddenTypes.add(hiddenType);
        }
    }

    public final String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public final DsmSorting getSorting() {
        return sorting;
    }

    public void setSorting(DsmSorting value) {
        sorting = value;
    }

    public List<Artifacttype> getHiddenTypes() {
        return new LinkedList<>(hiddenTypes);
    }

    public void setHiddenTypes(List<Artifacttype> hiddenTypes) {
        this.hiddenTypes.clear();
        this.hiddenTypes.addAll(hiddenTypes);
    }

}
