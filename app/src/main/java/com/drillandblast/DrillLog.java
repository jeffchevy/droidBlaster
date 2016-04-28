package com.drillandblast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Zachary on 4/5/2016.
 */
public class DrillLog implements Serializable {

    private String drillerName;
    private double drillId;
    private Date drillDate;
    private ArrayList<GridCoordinate> gridCoordinates;

    public DrillLog() {
        super();
    }

    public DrillLog(String drillerName, double drillId, Date drillDate, ArrayList<GridCoordinate> gridCoordinates) {
        super();
        this.drillerName = drillerName;
        this.drillId = drillId;
        this.drillDate = drillDate;
        this.gridCoordinates = gridCoordinates;
    }

    public String getDrillerName() {
        return  drillerName;
    }

    public void setDrillerName(String drillerName) {
        this.drillerName = drillerName;
    }

    public double getDrillId() {
        return drillId;
    }

    public void setDrillId(double drillId) {
        this.drillId = drillId;
    }

    public Date getDrillDate() {
        return drillDate;
    }

    public void setDrillDate(Date drillDate) {
        this.drillDate = drillDate;
    }

    public ArrayList<GridCoordinate> getGridCoordinates() {
        return gridCoordinates;
    }

    public void setGridCoordinates(ArrayList<GridCoordinate> gridCoordinates) {
        this.gridCoordinates = gridCoordinates;
    }

    @Override
    public String toString() {
        return this.drillerName + " - " + this.drillId;
    }
}
