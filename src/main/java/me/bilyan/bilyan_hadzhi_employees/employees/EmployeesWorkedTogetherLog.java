package me.bilyan.bilyan_hadzhi_employees.employees;

public class EmployeesWorkedTogetherLog implements Comparable<EmployeesWorkedTogetherLog> {
    private final Integer employeeId1;
    private final Integer employeeId2;
    private final Integer projectId;
    private final Integer daysWorkedTogether;

    public EmployeesWorkedTogetherLog(Integer employeeId1, Integer employeeId2,
                                      Integer projectId, Integer daysWorkedTogether) {
        this.employeeId1 = employeeId1;
        this.employeeId2 = employeeId2;
        this.projectId = projectId;
        this.daysWorkedTogether = daysWorkedTogether;
    }

    public Integer getEmployeeId1() {
        return employeeId1;
    }

    public Integer getEmployeeId2() {
        return employeeId2;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public Integer getDaysWorkedTogether() {
        return daysWorkedTogether;
    }

    @Override
    public int compareTo(EmployeesWorkedTogetherLog other) {
        return Integer.compare(this.daysWorkedTogether, other.daysWorkedTogether);
    }
}
