package me.bilyan.bilyan_hadzhi_employees.employees;

import me.bilyan.bilyan_hadzhi_employees.time.DateTimeFormatterService;
import me.bilyan.bilyan_hadzhi_employees.time.LocalDateAndProjectId;
import me.bilyan.bilyan_hadzhi_employees.time.LocalDateRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class EmployeesWhoWorkedTogetherService {
    private static final String CONTENT_TYPE_CSV = "text/csv";

    private final DateTimeFormatterService dateTimeFormatterService;

    @Autowired
    public EmployeesWhoWorkedTogetherService(DateTimeFormatterService dateTimeFormatterService) {
        this.dateTimeFormatterService = dateTimeFormatterService;
    }

    public List<EmployeesWorkedTogetherLog> getEmployeesWhoWorkedTogetherFromFile(MultipartFile file,
                                                                                  String dateFormat) {
        DateTimeFormatter formatter = dateTimeFormatterService.getFormatter(dateFormat);
        List<EmployeeProjectLog> data = readFileData(file, formatter);

        return getEmployeesWhoWorkedTogether(data);
    }

    public List<EmployeesWorkedTogetherLog> getEmployeesWhoWorkedTogether(List<EmployeeProjectLog> data) {
        sortDataByDateFrom(data);
        var totalDateRange = getTotalDateRange(data);

        Map<LocalDateAndProjectId, List<Integer>> projectIdEmployeesLog =
                groupEmployeesByDateAndProjectId(data, totalDateRange);

        Map<EmployeePairOnProject, Integer> pairsWhoWorkedTogether =
                calculateDaysWorkedTogetherForEachEmployeePairAndProjectId(projectIdEmployeesLog);

        return pairsWhoWorkedTogether.entrySet().stream()
                .map(this::toLogDTO)
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    public List<EmployeeProjectLog> readFileData(MultipartFile file, DateTimeFormatter formatter) {
        validateFileType(file);
        List<EmployeeProjectLog> data = new ArrayList<>();

        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                List<String> columns = Arrays.stream(line.split(","))
                        .map(String::trim)
                        .toList();

                try {
                    var dto = EmployeeProjectLog.fromPropertyListWithDateFormat(columns, formatter);
                    data.add(dto);
                } catch (Exception e) {
                    var message = String.format("There was an error while trying to read line number %d, please check your file.", lineNumber);
                    throw new IllegalArgumentException(message, e);
                }

                lineNumber++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return data;
    }

    private void validateFileType(MultipartFile file) {
        if (!CONTENT_TYPE_CSV.equals(file.getContentType())) {
            throw new IllegalArgumentException("Expected file of csv format");
        }
    }

    private void recordEmployeesWhoHaveWorkedOnGivenDate(LocalDate date,
                                                         List<EmployeeProjectLog> data,
                                                         Map<LocalDateAndProjectId, List<Integer>> projectIdEmployeesLog) {
        for (EmployeeProjectLog log : data) {
            var dateTo = log.getDateTo() == null ? LocalDate.now() : log.getDateTo();

            // because data is sorted by date-from, we can skip the rest
            if (log.getDateFrom().isAfter(date)) {
                break;
            }

            if (!dateTo.isBefore(date)) {
                var key = new LocalDateAndProjectId(date, log.getProjectId());
                projectIdEmployeesLog.putIfAbsent(key, new ArrayList<>());
                projectIdEmployeesLog.get(key).add(log.getEmployeeId());
            }
        }
    }

    private Map<EmployeePairOnProject, Integer> calculateDaysWorkedTogetherForEachEmployeePairAndProjectId(Map<LocalDateAndProjectId, List<Integer>> projectIdEmployeesLog) {
        Map<EmployeePairOnProject, Integer> pairsWhoWorkedTogether = new HashMap<>();

        projectIdEmployeesLog.forEach((key, employeesWhoHaveWorkedTogetherOnDay) -> {
            for (int i = 0; i < employeesWhoHaveWorkedTogetherOnDay.size() - 1; i++) {
                for (int j = i + 1; j < employeesWhoHaveWorkedTogetherOnDay.size(); j++) {
                    var employeeId1 = employeesWhoHaveWorkedTogetherOnDay.get(i);
                    var employeeId2 = employeesWhoHaveWorkedTogetherOnDay.get(j);
                    var employeePairOnProject = new EmployeePairOnProject(employeeId1, employeeId2, key.getProjectId());

                    pairsWhoWorkedTogether.putIfAbsent(employeePairOnProject, 0);
                    pairsWhoWorkedTogether.put(employeePairOnProject, pairsWhoWorkedTogether.get(employeePairOnProject) + 1);
                }
            }
        });

        return pairsWhoWorkedTogether;
    }

    private Map<LocalDateAndProjectId, List<Integer>> groupEmployeesByDateAndProjectId(List<EmployeeProjectLog> data, LocalDateRange totalDateRange) {
        Map<LocalDateAndProjectId, List<Integer>> projectIdEmployeesLog = new HashMap<>();
        totalDateRange.getStartDate()
                .datesUntil(totalDateRange.getEndDate().plusDays(1))
                .forEach(date -> recordEmployeesWhoHaveWorkedOnGivenDate(date, data, projectIdEmployeesLog));
        return projectIdEmployeesLog;
    }

    private void sortDataByDateFrom(List<EmployeeProjectLog> data) {
        data.sort(Comparator.comparing(EmployeeProjectLog::getDateFrom));
    }

    private LocalDateRange getTotalDateRange(List<EmployeeProjectLog> employeeProjectLogs) {
        var startDate = LocalDate.MAX;
        var endDate = LocalDate.MIN;

        for (EmployeeProjectLog log : employeeProjectLogs) {
            var dateFrom = log.getDateFrom();
            var dateTo = log.getDateTo() == null ? LocalDate.now() : log.getDateTo();

            if (dateFrom.isBefore(startDate)) {
                startDate = dateFrom;
            }

            if (dateTo.isAfter(endDate)) {
                endDate = dateTo;
            }
        }

        if (startDate.isAfter(endDate)) {
            return new LocalDateRange(LocalDate.now(), LocalDate.now());
        }

        return new LocalDateRange(startDate, endDate);
    }

    private EmployeesWorkedTogetherLog toLogDTO(Map.Entry<EmployeePairOnProject, Integer> entry) {
        return new EmployeesWorkedTogetherLog(
                entry.getKey().getEmployeePair().getEmployeeId1(),
                entry.getKey().getEmployeePair().getEmployeeId2(),
                entry.getKey().getProjectId(),
                entry.getValue());
    }
}
