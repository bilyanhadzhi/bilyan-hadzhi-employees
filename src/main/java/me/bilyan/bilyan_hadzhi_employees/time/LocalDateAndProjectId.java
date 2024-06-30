package me.bilyan.bilyan_hadzhi_employees.time;

import java.time.LocalDate;
import java.util.Objects;

public class LocalDateAndProjectId {
    private final LocalDate localDate;
    private final Integer projectId;

    public LocalDateAndProjectId(LocalDate localDate, Integer projectId) {
        this.localDate = localDate;
        this.projectId = projectId;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public Integer getProjectId() {
        return projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalDateAndProjectId that = (LocalDateAndProjectId) o;
        return Objects.equals(localDate, that.localDate) && Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localDate, projectId);
    }
}
