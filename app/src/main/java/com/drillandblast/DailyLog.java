package com.drillandblast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jacoxty on 4/12/2016.
 */
public class DailyLog {

    private String drillNum;
    private double gallonsFuel;
    private String date;
    private int meterStart;
    private int meterEnd;
    private String bulkTankPumpedFrom;
    private String percussionTime;



    public DailyLog() {
        super();
    }

    public DailyLog(String drillNum, double gallonsFuel, String date, int meterStart, int meterEnd, String bulkTankPumpedFrom, String percussionTime) {
        super();
        this.drillNum = drillNum;
        this.gallonsFuel = gallonsFuel;
        this.date = date;
        this.meterStart = meterStart;
        this.meterEnd = meterEnd;
        this.bulkTankPumpedFrom = bulkTankPumpedFrom;
        this.percussionTime = percussionTime;

    }

    public String getDrillNum() {
        return drillNum;
    }

    public void setDrillNum(String drillNum) {
        this.drillNum = drillNum;
    }

    public double getGallonsFuel() {
        return gallonsFuel;
    }

    public void getGallonsFuel(double gallonsFuel) {
        this.gallonsFuel = gallonsFuel;
    }

    public String getDate() {
        return date;
    }

    public void setStartDate(String date) {
        this.date = date;
    }

    public int getMeterStart() {
        return meterStart;
    }

    public void setShotNumber(int meterStart) {
        this.meterStart = meterStart;
    }

    public int getMeterEnd() {
        return meterEnd;
    }

    public void setMeterEnd(int meterEnd) {
        this.meterEnd = meterEnd;
    }

    public String getBulkTankPumpedFrom() {
        return bulkTankPumpedFrom;
    }

    public void setBulkTankPumpedFrom(String bulkTankPumpedFrom) {
        this.bulkTankPumpedFrom = bulkTankPumpedFrom;
    }

    public String getPercussionTime() {
        return percussionTime;
    }

    public void setPercussionTime(Date PercussionTime) {
        this.percussionTime = percussionTime;
    }

    public String toString() {
//        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//        return formatter.format(this.getDate())+"- Drill#: "+this.drillNum;
        return this.getDate()+"- Drill#: "+this.drillNum;
    }


}
