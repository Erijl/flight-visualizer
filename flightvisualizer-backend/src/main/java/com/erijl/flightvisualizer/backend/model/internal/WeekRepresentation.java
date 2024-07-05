package com.erijl.flightvisualizer.backend.model.internal;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;

@Getter
@Setter
public class WeekRepresentation {

    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;

    /**
     * Creating a new {@link WeekRepresentation} instance based on a daysOfOperation String
     *
     * @param daysOfOperation String representation of the days of operation (weekdays) in the format 'fffffff' with whitespace padding
     */
    public WeekRepresentation(String daysOfOperation) {
        this.monday = daysOfOperation.contains("1");
        this.tuesday = daysOfOperation.contains("2");
        this.wednesday = daysOfOperation.contains("3");
        this.thursday = daysOfOperation.contains("4");
        this.friday = daysOfOperation.contains("5");
        this.saturday = daysOfOperation.contains("6");
        this.sunday = daysOfOperation.contains("7");
    }

    /**
     * Creating a new {@link WeekRepresentation} instance based on a Date object
     *
     * @param date Date object representing the current date
     */
    public WeekRepresentation(LocalDate date) {
        DayOfWeek currentDayOfWeek = date.getDayOfWeek();

        this.monday = currentDayOfWeek == DayOfWeek.MONDAY;
        this.tuesday = currentDayOfWeek == DayOfWeek.TUESDAY;
        this.wednesday = currentDayOfWeek == DayOfWeek.WEDNESDAY;
        this.thursday = currentDayOfWeek == DayOfWeek.THURSDAY;
        this.friday = currentDayOfWeek == DayOfWeek.FRIDAY;
        this.saturday = currentDayOfWeek == DayOfWeek.SATURDAY;
        this.sunday = currentDayOfWeek == DayOfWeek.SUNDAY;
    }

    public WeekRepresentation() {
        this.monday = false;
        this.tuesday = false;
        this.wednesday = false;
        this.thursday = false;
        this.friday = false;
        this.saturday = false;
        this.sunday = false;
    }

    public String toDaysOfOperationString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(this.monday ? "1" : " ");
        stringBuilder.append(this.tuesday ? "2" : " ");
        stringBuilder.append(this.wednesday ? "3" : " ");
        stringBuilder.append(this.thursday ? "4" : " ");
        stringBuilder.append(this.friday ? "5" : " ");
        stringBuilder.append(this.saturday ? "6" : " ");
        stringBuilder.append(this.sunday ? "7" : " ");

        return stringBuilder.toString();
    }
}
