package me.bilyan.bilyan_hadzhi_employees.employees;

import java.util.Objects;

public class EmployeePairOnProject {
    private final EmployeePair employeePair;
    private final Integer projectId;

    public EmployeePairOnProject(Integer employeeId1, Integer employeeId2, Integer projectId) {
        this.employeePair = EmployeePair.fromIds(employeeId1, employeeId2);
        this.projectId = projectId;
    }

    public EmployeePair getEmployeePair() {
        return employeePair;
    }

    public Integer getProjectId() {
        return projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeePairOnProject that = (EmployeePairOnProject) o;
        return Objects.equals(employeePair, that.employeePair) && Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeePair, projectId);
    }
}
