package com.drillandblast.model;

import com.drillandblast.model.Project;

import java.util.Comparator;

public class ProjectNameComparator implements Comparator<Project> {
    @Override
    public int compare(Project project1, Project project2)   {
        return  project1.getProjectName().compareTo(project2.getProjectName());
    }
}
