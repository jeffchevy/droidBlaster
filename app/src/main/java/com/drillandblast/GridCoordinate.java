package com.drillandblast;

import java.util.ArrayList;

/**
 * Created by Zachary on 4/6/2016.
 */
public class GridCoordinate {

    private double row;
    private double column;
    private GridCoordinateInfo gridCoordinateInfo;

    public GridCoordinate() {
        super();
    }

    public GridCoordinate(double row, double column, GridCoordinateInfo gridCoordinateInfo) {
        this.row = row;
        this.column = column;
        this.gridCoordinateInfo = gridCoordinateInfo;
    }

    public double getRow() {
        return row;
    }

    public void setRow(double row) {
        this.row = row;
    }

    public double getColumn() {
        return column;
    }

    public void setColumn(double column) {
        this.column = column;
    }

    public GridCoordinateInfo getGridCoordinateInfo() {
        return gridCoordinateInfo;
    }

    public void setGridCoordinateInfo(GridCoordinateInfo gridCoordinateInfo) {
        this.gridCoordinateInfo = gridCoordinateInfo;
    }
}
