package com.erijl.flightvisualizer.backend.model;

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
    private Integer operationDaysUtc;

    @Column(name = "start_date_lt")
    private Date startDateLt;

    @Column(name = "end_date_lt")
    private Date endDateLt;

    @Column(name = "operation_days_lt")
    private Integer operationDaysLt;
}