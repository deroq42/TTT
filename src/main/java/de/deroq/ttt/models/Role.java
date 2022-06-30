package de.deroq.ttt.models;

public enum Role {

    INNOCENT("§aInnocent"),
    DETECTIVE("§9Detective"),
    TRAITOR("§4Traitor");

    private final String text;

    Role(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
