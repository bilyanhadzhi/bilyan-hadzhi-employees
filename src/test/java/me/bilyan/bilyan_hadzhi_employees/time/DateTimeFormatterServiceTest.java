package me.bilyan.bilyan_hadzhi_employees.time;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateTimeFormatterServiceTest {
    DateTimeFormatterService service = new DateTimeFormatterService();

    @Test
    void getFormatter_UnknownFormat_ThrowsException() {
        assertThrows(Exception.class, () -> service.getFormatter("MMM/dd/yyyy"));
    }

    @Test
    void getFormatter_KnownFormat_ReturnsFormatter() {
        assertDoesNotThrow(() -> service.getFormatter("yyyy-MM-dd"));
    }
}