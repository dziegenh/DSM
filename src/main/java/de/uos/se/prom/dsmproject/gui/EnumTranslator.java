package de.uos.se.prom.dsmproject.gui;

import de.uos.se.prom.dsmproject.entity.DsmSorting;

public class EnumTranslator {

    public String translateDsmSorting(DsmSorting sorting) {
        switch (sorting) {
            case ARTIFACT_NAME:
                return "Artifact name";
            case ARTIFACT_TYPE:
                return "Artifact type";
        }

        return "Unknown";
    }

}
