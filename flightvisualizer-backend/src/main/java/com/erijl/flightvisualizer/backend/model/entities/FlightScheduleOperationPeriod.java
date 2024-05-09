package com.erijl.flightvisualizer.backend.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity
@Table(name = "flight_schedule_operation_period")

@Getter
@Setter
public class FlightScheduleOperationPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operation_period_id")
    private Integer operationPeriodId;

    @Column(name = "start_date_utc")
    private Date startDateUtc;

    @Column(name = "end_date_utc")
    private Date endDateUtc;

    @Column(name = "operation_days_utc")
    private String operationDaysUtc;

    @Column(name = "start_date_lt")
    private Date startDateLt;

    @Column(name = "end_date_lt")
    private Date endDateLt;

    @Column(name = "operation_days_lt")
    private String operationDaysLt;

    public FlightScheduleOperationPeriod() {
    }

    public FlightScheduleOperationPeriod(Date startDateUtc, Date endDateUtc, String operationDaysUtc, Date startDateLt, Date endDateLt, String operationDaysLt) {
        this.startDateUtc = startDateUtc;
        this.endDateUtc = endDateUtc;
        this.operationDaysUtc = operationDaysUtc;
        this.startDateLt = startDateLt;
        this.endDateLt = endDateLt;
        this.operationDaysLt = operationDaysLt;
    }
}