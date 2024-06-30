package me.bilyan.bilyan_hadzhi_employees.employees;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class EmployeePair {
    private final Set<Integer> employeeIds = new HashSet<>();

    private EmployeePair(Integer id1, Integer id2) {
        if (Objects.equals(id1, id2)) {
            throw new IllegalArgumentException("Employee pair cannot have the same employee twice");
        }

        this.employeeIds.add(id1);
        this.employeeIds.add(id2);
    }

    public Integer getEmployeeId1() {
        return employeeIds.stream().min(Integer::compare).orElse(null);
    }

    public Integer getEmployeeId2() {
        return employeeIds.stream().max(Integer::compare).orElse(null);
    }

    public static EmployeePair fromIds(Integer id1, Integer id2) {
        return new EmployeePair(id1, id2);
    }

    public Set<Integer> getEmployeeIds() {
        return Collections.unmodifiableSet(employeeIds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeePair that = (EmployeePair) o;
        return Objects.equals(employeeIds, that.employeeIds);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(employeeIds);
    }
}
