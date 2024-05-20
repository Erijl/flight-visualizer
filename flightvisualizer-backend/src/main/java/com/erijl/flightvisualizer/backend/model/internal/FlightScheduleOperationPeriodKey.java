package com.erijl.flightvisualizer.backend.model.internal;

import com.erijl.flightvisualizer.backend.model.entities.WeekRepresentation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightScheduleOperationPeriodKey  {

    private String startDateUtc;

    private String endDateUtc;

    private WeekRepresentation operationDaysUtc;

    private String startDateLt;

    private String endDateLt;

    private WeekRepresentation operationDaysLt;

    public FlightScheduleOperationPeriodKey () {
    }

    public FlightScheduleOperationPeriodKey (String startDateUtc, String endDateUtc, WeekRepresentation operationDaysUtc, String startDateLt, String endDateLt, WeekRepresentation operationDaysLt) {
        this.startDateUtc = startDateUtc;
        this.endDateUtc = endDateUtc;
        this.operationDaysUtc = operationDaysUtc;
        this.startDateLt = startDateLt;
        this.endDateLt = endDateLt;
        this.operationDaysLt = operationDaysLt;
    }

    @Override
    public String toString() {
        return "FlightScheduleOperationPeriodKey{" +
                "startDateUtc='" + startDateUtc + '\'' +
                ", endDateUtc='" + endDateUtc + '\'' +
                ", operationDaysUtc=" + operationDaysUtc.toDaysOfOperationString() +
                ", startDateLt='" + startDateLt + '\'' +
                ", endDateLt='" + endDateLt + '\'' +
                ", operationDaysLt=" + operationDaysLt.toDaysOfOperationString() +
                '}';
    }
}
