package com.drillandblast;

import java.io.Serializable;
import java.util.ArrayList;

public class GridCoordinate implements Serializable {

    private String id;
    private int row;
    private int column;
    private double depth;
    private String comment;
    private double bitSize;

    public GridCoordinate() {
        super();
    }

    public GridCoordinate(String id, int row, int column, double depth, String comment, double bitSize) {
        this.id = id;
        this.row = row;
        this.column = column;
        this.depth = depth;
        this.comment = comment;
        this.bitSize = bitSize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
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

    @Override
    public String toString() {
        return String.valueOf(this.depth);
    }
}
