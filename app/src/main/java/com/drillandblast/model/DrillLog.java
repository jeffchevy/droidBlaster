package com.drillandblast.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private Map managerGridCoordinates;
    private String supervisorSignature;
    private String supervisorSignatureName;
    private Date supervisorSignatureDate;
    private String customerSignature;
    private String customerSignatureName;
    private Date customerSignatureDate;

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

    public Map getManagerGridCoordinates() {
        return managerGridCoordinates;
    }

    public void setManagerGridCoordinates(Map managerGridCoordinates) {
        this.managerGridCoordinates = managerGridCoordinates;
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

    public String getSupervisorSignatureName() {
        return supervisorSignatureName;
    }

    public void setSupervisorSignatureName(String supervisorSignatureName) {
        this.supervisorSignatureName = supervisorSignatureName;
    }

    public Date getSupervisorSignatureDate() {
        return supervisorSignatureDate;
    }

    public void setSupervisorSignatureDate(Date supervisorSignatureDate) {
        this.supervisorSignatureDate = supervisorSignatureDate;
    }

    public String getCustomerSignatureName() {
        return customerSignatureName;
    }

    public void setCustomerSignatureName(String customerSignatureName) {
        this.customerSignatureName = customerSignatureName;
    }

    public Date getCustomerSignatureDate() {
        return customerSignatureDate;
    }

    public void setCustomerSignatureDate(Date customerSignatureDate) {
        this.customerSignatureDate = customerSignatureDate;
    }

    @Override
    public String toString() {
        return this.drillerName + " - " + this.name;
    }
}
