package com.erijl.flightvisualizer.backend.model.entities;

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
    @Column(name = "id")
    private Integer dataElementId;

    @ManyToOne
    @JoinColumn(name = "flight_schedule_id", referencedColumnName = "id")
    private FlightSchedule flightSchedule;

    @Column(name = "start_leg_sequence_number")
    private Integer startLegSequenceNumber;

    @Column(name = "end_leg_sequence_number")
    private Integer endLegSequenceNumber;

    @Column(name = "ssim_code")
    private Integer ssimCode;

    @Column(name = "value")
    private String value;

    public FlightScheduleDataElement() {
    }

    public FlightScheduleDataElement(FlightSchedule flightSchedule, Integer startLegSequenceNumber, Integer endLegSequenceNumber, Integer ssimCode, String value) {
        this.flightSchedule = flightSchedule;
        this.startLegSequenceNumber = startLegSequenceNumber;
        this.endLegSequenceNumber = endLegSequenceNumber;
        this.ssimCode = ssimCode;
        this.value = value;
    }
}