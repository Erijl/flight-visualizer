package com.erijl.flightvisualizer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "flight_schedule_data_element")

@Getter
@Setter
public class FlightScheduleDataElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_element_id")
    private Integer dataElementId;

    @ManyToOne
    @JoinColumn(name = "flight_schedule_id", referencedColumnName = "flight_schedule_id")
    private FlightSchedule flightSchedule;

    @Column(name = "start_leg_sequence_number")
    private Integer startLegSequenceNumber;

    @Column(name = "end_leg_sequence_number")
    private Integer endLegSequenceNumber;

    @Column(name = "ssim_code")
    private String ssimCode;

    @Column(name = "value")
    private String value;
}