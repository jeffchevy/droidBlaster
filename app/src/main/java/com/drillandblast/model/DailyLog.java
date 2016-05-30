package com.drillandblast.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jacoxty on 4/12/2016.
 */
public class DailyLog extends Entity {

    private String id;
    private String drillNum;
    private Double gallonsFuel;
    private Date date;
    private Double meterStart;
    private Double meterEnd;
    private String bulkTankPumpedFrom;
    private Double percussionTime;

    public DailyLog() {
        super();
    }

    public DailyLog(String id, String drillNum, Double gallonsFuel, Date date, Double meterStart, Double meterEnd, String bulkTankPumpedFrom, Double percussionTime) {
        super();
        this.id = id;
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

    public Double getGallonsFuel() {

        return gallonsFuel;
    }

    public void setGallonsFuel(Double gallonsFuel) {

        this.gallonsFuel = gallonsFuel;
    }

    public Date getDate() {
        return date;
    }

    public void setStartDate(Date date) {

        this.date = date;
    }

    public Double getMeterStart() {

        return meterStart;
    }
    public void setMeterStart(Double meterStart) {

        this.meterStart = meterStart;
    }

    public Double getMeterEnd() {

        return meterEnd;
    }

    public void setMeterEnd(Double meterEnd) {

        this.meterEnd = meterEnd;
    }

    public String getBulkTankPumpedFrom() {

        return bulkTankPumpedFrom;
    }

    public void setBulkTankPumpedFrom(String bulkTankPumpedFrom) {
        this.bulkTankPumpedFrom = bulkTankPumpedFrom;
    }

    public Double getPercussionTime() {

        return percussionTime;
    }

    public void setPercussionTime(Double percussionTime) {
        this.percussionTime = percussionTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toString() {
        String result = "";
        if (this.getDate() != null) {
            DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
            result = formatter.format(this.getDate())+"- Drill#: "+this.drillNum;
        }
        return result;
    }


}
