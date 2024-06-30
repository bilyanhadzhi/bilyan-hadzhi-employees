package me.bilyan.bilyan_hadzhi_employees.time;

public class DateFormatAndExplanationDTO {
    private final String dateFormat;
    private final String explanation;

    public DateFormatAndExplanationDTO(String dateFormat, String explanation) {
        this.dateFormat = dateFormat;
        this.explanation = explanation;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public String getExplanation() {
        return explanation;
    }
}
