package com.drillandblast.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class GridCoordinate extends Entity {

    private String id;
    private int row;
    private int column;
    private double depth;
    private String comment;
    private String bitSize;
    private Date date;
    private Boolean isDrilled;

    public GridCoordinate() {
        super();
    }

    public GridCoordinate(String id, int row, int column, double depth, String comment, String bitSize, Date date, boolean isDrilled) {
        this.id = id;
        this.row = row;
        this.column = column;
        this.depth = depth;
        this.comment = comment;
        this.bitSize = bitSize;
        this.date = date;
        this.isDrilled = isDrilled;
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

    public String getBitSize() {
        return bitSize;
    }

    public void setBitSize(String bitSize) {
        this.bitSize = bitSize;
    }

    public Date getDate() { return date; }

    public void setDate(Date date){this.date = date;}

    public Boolean getIsDrilled() { return isDrilled; }

    public void setIsDrilled(boolean isDrilled) { this.isDrilled = isDrilled; }


    @Override
    public String toString() {
        return String.valueOf(this.depth);
    }
}
