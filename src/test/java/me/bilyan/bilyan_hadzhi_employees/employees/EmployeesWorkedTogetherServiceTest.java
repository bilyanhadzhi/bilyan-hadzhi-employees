package me.bilyan.bilyan_hadzhi_employees.employees;

import me.bilyan.bilyan_hadzhi_employees.time.DateTimeFormatterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmployeesWorkedTogetherServiceTest {
    private static final String FILE_NAME_DEFAULT = "test.csv";
    private static final String FILE_TYPE_CSV = "text/csv";
    private static final DateTimeFormatter FORMATTER_DEFAULT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Mock
    DateTimeFormatterService dateTimeFormatterService;

    @InjectMocks
    EmployeesWhoWorkedTogetherService service;

    @Test
    void readFileData_CannotGetInputStream_ThrowsException() {
        var file = new MockMultipartFile(
                FILE_NAME_DEFAULT,
                FILE_NAME_DEFAULT,
                "image/png",
                new byte[] {}
        );

        assertThrows(RuntimeException.class, () -> service.readFileData(file, FORMATTER_DEFAULT));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "a, 1, 2000-01-01, 2000-01-01",
            "1, b, 2000-01-01, 2000-01-01",
            "1, 1, null, 2000-01-01",
    })
    void readFileData_CannotConstructDataFromRow_ThrowsException(String data) {
        var file = new MockMultipartFile(
                FILE_NAME_DEFAULT,
                FILE_NAME_DEFAULT,
                FILE_TYPE_CSV,
                data.getBytes()
        );

        assertThrows(RuntimeException.class, () -> service.readFileData(file, FORMATTER_DEFAULT));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
            1, 10, 2024-01-01, 2024-01-31,
            2, 10, 2024-01-01, 2024-02-01
            """
    })
    void readFileData_HappyPath_ReturnsExpectedResult(String data) {
        var file = new MockMultipartFile(
                FILE_NAME_DEFAULT,
                FILE_NAME_DEFAULT,
                FILE_TYPE_CSV,
                data.getBytes()
        );

        var result = service.readFileData(file, FORMATTER_DEFAULT);

        assertEquals(2, result.size());

        var firstRow = result.get(0);
        var secondRow = result.get(1);

        assertEquals(1, firstRow.getEmployeeId());
        assertEquals(10, firstRow.getProjectId());
        assertEquals(LocalDate.of(2024, 1, 1), firstRow.getDateFrom());
        assertEquals(LocalDate.of(2024, 1, 31), firstRow.getDateTo());

        assertEquals(2, secondRow.getEmployeeId());
        assertEquals(10, secondRow.getProjectId());
        assertEquals(LocalDate.of(2024, 1, 1), secondRow.getDateFrom());
        assertEquals(LocalDate.of(2024, 2, 1), secondRow.getDateTo());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            """
            1, 10, 2024-01-01, NULL
            2, 10, 2023-01-01, 2023-12-31
            3, 11, 2024-01-01, NULL
            """
    })
    void getEmployeesWhoWorkedTogether_NoMatchingPairs_ReturnsEmptyResult() {
        var data = new ArrayList<>(List.of(
                new EmployeeProjectLog(1, 10, LocalDate.of(2024, 1, 1), null),
                new EmployeeProjectLog(2, 10, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31)),
                new EmployeeProjectLog(3, 11, LocalDate.of(2023, 1, 1), null)
        ));

        var result = service.getEmployeesWhoWorkedTogether(data);

        assertTrue(result.isEmpty());
    }

    @Test
    void getEmployeesWhoWorkedTogether_TwoPeopleOnSameProject_ReturnsExpectedResult() {
        var data = new ArrayList<>(List.of(
                new EmployeeProjectLog(1, 10, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31)),
                new EmployeeProjectLog(2, 10, LocalDate.of(2023, 1, 1), LocalDate.of(2024, 2, 1)),
                new EmployeeProjectLog(3, 11, LocalDate.of(2023, 1, 1), null)
        ));

        var result = service.getEmployeesWhoWorkedTogether(data);

        assertEquals(1, result.size());

        var pair = result.get(0);

        assertEquals(1, pair.getEmployeeId1());
        assertEquals(2, pair.getEmployeeId2());
        assertEquals(10, pair.getProjectId());
        assertEquals(31, pair.getDaysWorkedTogether());
    }

    @Test
    void getEmployeesWhoWorkedTogether_MoreThanTwoPeopleOnSameProject_ReturnsExpectedResult() {
        var data = new ArrayList<>(List.of(
                new EmployeeProjectLog(1, 10, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31)),
                new EmployeeProjectLog(2, 10, LocalDate.of(2023, 1, 1), LocalDate.of(2024, 2, 1)),
                new EmployeeProjectLog(3, 10, LocalDate.of(2024, 1, 30), LocalDate.of(2024, 3, 1))
        ));

        var result = service.getEmployeesWhoWorkedTogether(data);

        assertEquals(3, result.size());

        var pair1 = result.get(0);
        assertEquals(1, pair1.getEmployeeId1());
        assertEquals(2, pair1.getEmployeeId2());
        assertEquals(10, pair1.getProjectId());
        assertEquals(31, pair1.getDaysWorkedTogether());

        var pair2 = result.get(1);
        assertEquals(2, pair2.getEmployeeId1());
        assertEquals(3, pair2.getEmployeeId2());
        assertEquals(10, pair2.getProjectId());
        assertEquals(3, pair2.getDaysWorkedTogether());

        var pair3 = result.get(2);
        assertEquals(1, pair3.getEmployeeId1());
        assertEquals(3, pair3.getEmployeeId2());
        assertEquals(10, pair3.getProjectId());
        assertEquals(2, pair3.getDaysWorkedTogether());
    }

    @Test
    void getEmployeesWhoWorkedTogether_TwoPeopleOnMoreThanOneProject_ReturnsExpectedResult() {
        var data = new ArrayList<>(List.of(
                new EmployeeProjectLog(1, 10, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31)),
                new EmployeeProjectLog(2, 10, LocalDate.of(2023, 1, 1), LocalDate.of(2024, 2, 1)),
                new EmployeeProjectLog(1, 11, LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 29)),
                new EmployeeProjectLog(2, 11, LocalDate.of(2024, 2, 2), LocalDate.of(2024, 2, 29))
        ));

        var result = service.getEmployeesWhoWorkedTogether(data);

        assertEquals(2, result.size());

        var pair1 = result.get(0);
        assertEquals(1, pair1.getEmployeeId1());
        assertEquals(2, pair1.getEmployeeId2());
        assertEquals(10, pair1.getProjectId());
        assertEquals(31, pair1.getDaysWorkedTogether());

        var pair2 = result.get(1);
        assertEquals(1, pair2.getEmployeeId1());
        assertEquals(2, pair2.getEmployeeId2());
        assertEquals(11, pair2.getProjectId());
        assertEquals(28, pair2.getDaysWorkedTogether());
    }
}