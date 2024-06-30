package me.bilyan.bilyan_hadzhi_employees.employees;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmployeeProjectLog {
    private static final int PROPERTY_COUNT = 4;
    private static final String NULL_STRING = "NULL";

    private Integer employeeId;
    private Integer projectId;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    private EmployeeProjectLog() {
        // empty
    }

    public EmployeeProjectLog(Integer employeeId, Integer projectId, LocalDate dateFrom, LocalDate dateTo) {
        this.employeeId = employeeId;
        this.projectId = projectId;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public static EmployeeProjectLog fromPropertyListWithDateFormat(List<String> properties,
                                                                    DateTimeFormatter formatter) {
        if (PROPERTY_COUNT != properties.size()) {
            throw new IllegalArgumentException("Unexpected property count");
        }

        var result = new EmployeeProjectLog();
        result.setEmployeeId(Integer.valueOf(properties.get(0)));
        result.setProjectId(Integer.valueOf(properties.get(1)));
        result.setDateFrom(LocalDate.parse(properties.get(2), formatter));

        LocalDate dateTo = properties.get(3).equalsIgnoreCase(NULL_STRING) ?
                null : LocalDate.parse(properties.get(3), formatter);
        result.setDateTo(dateTo);

        return result;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    @Override
    public String toString() {
        return "EmployeeProjectLog{" +
                "employeeId=" + employeeId +
                ", projectId=" + projectId +
                ", dateFrom=" + dateFrom +
                ", dateTo=" + dateTo +
                '}';
    }
}
