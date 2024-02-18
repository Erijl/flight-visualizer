CREATE TABLE airline
(
    iata_airline_code VARCHAR(3) PRIMARY KEY,
    icao_airline_code VARCHAR(4) NOT NULL,
    airline_name      VARCHAR(255)
);

CREATE TABLE airport
(
    iata_airport_code VARCHAR(3) PRIMARY KEY,
    airport_name      VARCHAR(255)                                     NOT NULL,
    longitude         NUMERIC(9, 6)                                    NOT NULL,
    latitude          NUMERIC(9, 6)                                    NOT NULL,
    iata_city_code    VARCHAR(3)                                       NOT NULL,
    iso_country_code  VARCHAR(2)                                       NOT NULL,
    location_type     ENUM ('Airport', 'RailwayStation', 'BusStation') NOT NULL,
    offset_utc        INT                                              NOT NULL,
    timezone_id       VARCHAR(255)                                     NOT NULL
);

CREATE TABLE aircraft
(
    iata_aircraft_code VARCHAR(3) PRIMARY KEY,
    aircraft_name      VARCHAR(255) NOT NULL
);

CREATE TABLE flight_schedule_operation_period
(
    operation_period_id INT AUTO_INCREMENT PRIMARY KEY,
    start_date_utc      DATE NOT NULL,
    end_date_utc        DATE NOT NULL,
    operation_days_utc  INT  NOT NULL,
    start_date_lt       DATE NOT NULL,
    end_date_lt         DATE NOT NULL,
    operation_days_lt   INT  NOT NULL
);

CREATE TABLE flight_schedule
(
    flight_schedule_id  INT AUTO_INCREMENT PRIMARY KEY,
    airline_code        VARCHAR(3)   ,
    operation_period_id INT          NOT NULL,
    flight_number       INT          ,
    suffix              VARCHAR(255) ,
    FOREIGN KEY (airline_code) REFERENCES airline (iata_airline_code),
    FOREIGN KEY (operation_period_id) REFERENCES flight_schedule_operation_period (operation_period_id)
);

CREATE TABLE flight_schedule_data_element
(
    data_element_id           INT AUTO_INCREMENT PRIMARY KEY,
    flight_schedule_id        INT          NOT NULL,
    start_leg_sequence_number INT          NOT NULL,
    end_leg_sequence_number   INT          NOT NULL,
    ssim_code                 VARCHAR(255) NOT NULL,
    value                     VARCHAR(255) ,
    FOREIGN KEY (flight_schedule_id) REFERENCES flight_schedule (flight_schedule_id)
);

CREATE TABLE flight_schedule_leg
(
    leg_id                                INT AUTO_INCREMENT PRIMARY KEY,
    flight_schedule_id                    INT          NOT NULL,
    leg_sequence_number                   INT          NOT NULL,
    origin_airport                        VARCHAR(3)   NOT NULL,
    destination_airport                   VARCHAR(3)   NOT NULL,
    iata_service_type_code                VARCHAR(3)   NOT NULL,
    aircraft_owner_airline_code           VARCHAR(3)   ,
    aircraft_code                         VARCHAR(3)   NOT NULL,
    aircraft_configuration_version        VARCHAR(255) ,
    registration                          VARCHAR(255) ,
    op                                    BOOLEAN      ,
    aircraft_departure_time_utc           TIME         ,
    aircraft_departure_time_date_diff_utc INT          ,
    aircraft_departure_time_lt            TIME         ,
    aircraft_departure_time_diff_lt       INT          ,
    aircraft_departure_time_variation     VARCHAR(255) ,
    aircraft_arrival_time_utc             TIME         ,
    aircraft_arrival_time_date_diff_utc   INT          ,
    aircraft_arrival_time_lt              TIME         ,
    aircraft_arrival_time_diff_lt         INT          ,
    aircraft_arrival_time_variation       VARCHAR(255) ,

    FOREIGN KEY (flight_schedule_id) REFERENCES flight_schedule (flight_schedule_id),
    FOREIGN KEY (origin_airport) REFERENCES airport (iata_airport_code),
    FOREIGN KEY (destination_airport) REFERENCES airport (iata_airport_code),
    FOREIGN KEY (aircraft_owner_airline_code) REFERENCES airline (iata_airline_code),
    FOREIGN KEY (aircraft_code) REFERENCES aircraft (iata_aircraft_code)
);