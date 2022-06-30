package de.deroq.ttt.game.models;

import java.util.List;

public class GameMap {

    private String muid;
    private List<String> builders;
    private List<String> spawnLocations;
    private String testerLocation;
    private String rightTesterLightLocation;
    private String leftTesterLightLocation;
    private String spectatorLocation;

    //Public constructor due to pojo exceptions.
    public GameMap() {

    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    public List<String> getBuilders() {
        return builders;
    }

    public void setBuilders(List<String> builders) {
        this.builders = builders;
    }

    public List<String> getSpawnLocations() {
        return spawnLocations;
    }

    public void setSpawnLocations(List<String> spawnLocations) {
        this.spawnLocations = spawnLocations;
    }

    public String getTesterLocation() {
        return testerLocation;
    }

    public void setTesterLocation(String testerLocation) {
        this.testerLocation = testerLocation;
    }

    public String getRightTesterLightLocation() {
        return rightTesterLightLocation;
    }

    public void setRightTesterLightLocation(String rightTesterLightLocation) {
        this.rightTesterLightLocation = rightTesterLightLocation;
    }

    public String getLeftTesterLightLocation() {
        return leftTesterLightLocation;
    }

    public void setLeftTesterLightLocation(String leftTesterLightLocation) {
        this.leftTesterLightLocation = leftTesterLightLocation;
    }

    public String getSpectatorLocation() {
        return spectatorLocation;
    }

    public void setSpectatorLocation(String spectatorLocation) {
        this.spectatorLocation = spectatorLocation;
    }

    public static GameMap create(String muid) {
        GameMap gameMap = new GameMap();
        gameMap.setMuid(muid);
        return gameMap;
    }
}
