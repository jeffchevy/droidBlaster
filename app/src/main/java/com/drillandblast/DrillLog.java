package com.drillandblast;

/**
 * Created by Zachary on 4/5/2016.
 */
public class DrillLog {

    private String drillerName;
    private double drillId;

    public DrillLog() {
        super();
    }

    public DrillLog(String drillerName, double drillId) {
        super();
        this.drillerName = drillerName;
        this.drillId = drillId;
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

    @Override
    public String toString() {
        return this.drillerName;
    }
}
