package com.drillandblast.model;

/**
 * Created by Zachary on 4/6/2016.
 */
public class GridCoordinateInfo extends Entity {

    private double depth;
    private String comment;

    public GridCoordinateInfo() {
        super();
    }

    public GridCoordinateInfo(double depth, String comment) {
        this.depth = depth;
        this.comment = comment;
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
}
