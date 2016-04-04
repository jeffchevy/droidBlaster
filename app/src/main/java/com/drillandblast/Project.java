package com.drillandblast;

import java.util.Date;
/**
 * Created by Zachary on 3/31/2016.
 */
public class Project {

    private String projectName;
    private String contractorName;
    private Date startDate;
    private double shotNumber;

    public Project() {
        super();
    }

    public Project(String projectName, String contractorName, Date startDate, double shotNumber) {
        super();
        this.projectName = projectName;
        this.contractorName = contractorName;
        this.startDate = startDate;
        this.shotNumber = shotNumber;
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
    public double getShotNumber() {
        return shotNumber;
    }
    public void setShotNumber(double shotNumber) {
        this.shotNumber = shotNumber;
    }

    @Override
    public String toString() {
        return this.projectName;
    }

}
