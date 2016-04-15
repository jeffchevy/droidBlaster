package com.drillandblast;

import java.util.ArrayList;

/**
 * Created by Zachary on 4/6/2016.
 */
public class GridCoordinate {

    private double row;
    private double column;
    private double depth;
    private String comment;
    private double bitSize;

    public GridCoordinate() {
        super();
    }

    public GridCoordinate(double row, double column, double depth, String comment, double bitSize) {
        this.row = row;
        this.column = column;
        this.depth = depth;
        this.comment = comment;
        this.bitSize = bitSize;
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

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getBitSize() {
        return bitSize;
    }

    public void setBitSize(double bitSize) {
        this.bitSize = bitSize;
    }
}
