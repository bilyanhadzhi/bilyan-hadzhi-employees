package me.bilyan.bilyan_hadzhi_employees.time;

import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DateTimeFormatterService {
    public static final List<DateFormatAndExplanationDTO> FORMATS = List.of(
            new DateFormatAndExplanationDTO("yyyy-MM-dd", "ISO Local Date (2024-12-31)"),
            new DateFormatAndExplanationDTO("dd-MM-yyyy", "Europe with dashes (31-12-2024)"),
            new DateFormatAndExplanationDTO("dd.MM.yyyy", "Europe with dots (31.12.2024)"),
            new DateFormatAndExplanationDTO("dd/MM/yyyy", "Europe with slashes (31/12/2024)"),
            new DateFormatAndExplanationDTO("dd-MMM-yyyy", "Europe with dashes, short month word (31-Dec-2024)"),
            new DateFormatAndExplanationDTO("MM/dd/yyyy", "US (12/31/2024)"),
            new DateFormatAndExplanationDTO("MMM-dd-yyyy", "US with dashes, short month word (Dec-31-2024)")
    );

    private static final Map<String, DateTimeFormatter> FORMATTERS = FORMATS
            .stream()
            .collect(Collectors.toMap(
                    DateFormatAndExplanationDTO::getDateFormat,
                    dto -> DateTimeFormatter.ofPattern(dto.getDateFormat()))
            );

    public List<DateFormatAndExplanationDTO> getSupportedFormats() {
        return FORMATS;
    }

    public DateTimeFormatter getFormatter(String pattern) {
        if (!FORMATTERS.containsKey(pattern)) {
            throw new IllegalArgumentException("Unknown date pattern: " + pattern);
        }

        return FORMATTERS.get(pattern);
    }
}
