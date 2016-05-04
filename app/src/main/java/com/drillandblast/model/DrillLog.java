package com.drillandblast.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Zachary on 4/5/2016.
 */
public class DrillLog implements Serializable {

    private String id;
    private String drillerName;
    private String name;
    private List<GridCoordinate> gridCoordinates;

    public DrillLog() {
        super();
    }

    public DrillLog(String id, String drillerName, String name) {
        super();
        this.id = id;
        this.drillerName = drillerName;
        this.name = name;
    }

    public String getDrillerName() {
        return  drillerName;
    }

    public void setDrillerName(String drillerName) {
        this.drillerName = drillerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GridCoordinate> getGridCoordinates() {
        return gridCoordinates;
    }

    public void setGridCoordinates(List<GridCoordinate> gridCoordinates) {
        this.gridCoordinates = gridCoordinates;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.drillerName + " - " + this.name;
    }
}
