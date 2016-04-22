package com.drillandblast;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jacoxty on 4/12/2016.
 */
public class DailyLog {

    private int drillNum;
    private double gallonsFuel;
    private Date date;
    private int meterStart;
    private int meterEnd;
    private String bulkTankPumpedFrom;
    private String percussionTime;



    public DailyLog() {
        super();
    }

    public DailyLog(int drillNum, double gallonsFuel, Date date, int meterStart, int meterEnd, String bulkTankPumpedFrom, String percussionTime) {
        super();
        this.drillNum = drillNum;
        this.gallonsFuel = gallonsFuel;
        this.date = date;
        this.meterStart = meterStart;
        this.meterEnd = meterEnd;
        this.bulkTankPumpedFrom = bulkTankPumpedFrom;
        this.percussionTime = percussionTime;

    }

    public int getDrillNum() {
        return drillNum;
    }

    public void setDrillNum(int drillNum) {
        this.drillNum = drillNum;
    }

    public double getGallonsFuel() {
        return gallonsFuel;
    }

    public void getGallonsFuel(double gallonsFuel) {
        this.gallonsFuel = gallonsFuel;
    }

    public Date getDate() {
        return date;
    }

    public void setStartDate(Date date) {
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



}
