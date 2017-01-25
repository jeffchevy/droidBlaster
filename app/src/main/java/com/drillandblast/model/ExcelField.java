package com.drillandblast.model;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Zachary on 1/23/2017.
 */
public class ExcelField extends EditText {

    private String id;
    private double depth;
    private int row;
    private int column;


    public ExcelField(Context context, String id, double depth, int row, int column) {
        super(context);
        this.id = id;
        this.depth = depth;
        this.row = row;
        this.column = column;
    }

    public ExcelField(Context context, AttributeSet attrs) {
        super(context, attrs);

    }
    public ExcelField(Context context) {
        super(context);

    }

    public ExcelField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
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

    public String getExcelId() {
        return id;
    }

    public void setId() {
        this.id = id;
    }

    public String toString() {
        return "Depth: " + this.depth + " Row: " + this.row + " Column: " + this.column;
    }
}
