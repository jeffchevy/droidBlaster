package com.drillandblast.model;

import com.drillandblast.model.DailyLog;
import com.drillandblast.model.DrillLog;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Zachary on 3/31/2016.
 */
public class Project extends Entity {

    private String id;
    private String projectName;
    private String contractorName;
    private Date startDate;
    private Double shotNumber;
    private String drillerName;
    private Double bitSize;
    private List<DrillLog> drillLogs;
    private List<DailyLog> dailyLogs;

    public Project() {
        super();
    }

    public Project(String id,String projectName, String contractorName, Date startDate, Double shotNumber, String drillerName, Double bitSize, List<DrillLog> drillLogs ,List<DailyLog> dailyLogs ){
        super();
        this.id = id;
        this.projectName = projectName;
        this.contractorName = contractorName;
        this.startDate = startDate;
        this.shotNumber = shotNumber;
        this.drillerName = drillerName;
        this.bitSize = bitSize;
        this.drillLogs = drillLogs;
        this.dailyLogs = dailyLogs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public String getContractorName() {
        return contractorName;
    }
    public void setContractorName(String contractorName) {
        this.contractorName = contractorName;
    }
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Double getShotNumber() {
        return shotNumber;
    }
    public void setShotNumber(Double shotNumber) {
        this.shotNumber = shotNumber;
    }

    public String getDrillerName() {
        return drillerName;
    }

    public void setDrillerName(String drillerName){
        this.drillerName = drillerName;
    }

    public double getBitSize(){
        return bitSize;
    }
    public void setBitSiZe(Double bitSiZe){
        this.bitSize=bitSiZe;
    }

    public List<DrillLog> getDrillLogs() {
        return drillLogs;
    }

    public void setDrillLogs(List<DrillLog> drillLogs) {
        this.drillLogs = drillLogs;
    }

    public void addDrillLog(DrillLog drillLog) {
        drillLogs.add(drillLog);
    }

    @Override
    public String toString() {
        return this.projectName;
    }

    public List<DailyLog> getDailyLogs() {
        return dailyLogs;
    }

    public void setDailyLogs(List<DailyLog> drillLogs) {
        this.dailyLogs = dailyLogs;
    }

    public void addDailyLog(DailyLog dailyLog) {
        dailyLogs.add(dailyLog);
    }



}
