package com.drillandblast.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Zachary on 4/5/2016.
 */
public class DrillLog extends Entity {

    private String id;
    private String drillerName;
    private String name;
    private Date date;
    private String pattern;
    private Integer shotNumber;
    private String bitSize;
    private List<GridCoordinate> gridCoordinates;
    private String supervisorSignature;
    private String customerSignature;

    public DrillLog() {
        super();
    }

    public DrillLog(String id, String drillerName, String name, String pattern, Integer shotNumber, String bitSize) {
        this.id = id;
        this.drillerName = drillerName;
        this.name = name;
        this.pattern = pattern;
        this.shotNumber = shotNumber;
        this.bitSize = bitSize;
    }

    public String getDrillerName() {
        return  drillerName;
    }

    public void setDrillerName(String drillerName) {
        this.drillerName = drillerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GridCoordinate> getGridCoordinates() {
        return gridCoordinates;
    }

    public void setGridCoordinates(List<GridCoordinate> gridCoordinates) {
        this.gridCoordinates = gridCoordinates;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Integer getShotNumber() {
        return shotNumber;
    }

    public void setShotNumber(Integer shotNumber) {
        this.shotNumber = shotNumber;
    }

    public String getBitSize() {
        return bitSize;
    }

    public void setBitSize(String bitSize) {
        this.bitSize = bitSize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSupervisorSignature() {
        return supervisorSignature;
    }

    public void setSupervisorSignature(String supervisorSignature) {
        this.supervisorSignature = supervisorSignature;
    }

    public String getCustomerSignature() {
        return customerSignature;
    }

    public void setCustomerSignature(String customerSignature) {
        this.customerSignature = customerSignature;
    }

    @Override
    public String toString() {
        return this.drillerName + " - " + this.name;
    }
}
