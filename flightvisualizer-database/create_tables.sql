CREATE TABLE airline
(
    id VARCHAR(3) PRIMARY KEY,
    icao_airline_code VARCHAR(4),
    airline_name      VARCHAR(255)
);

CREATE TABLE airport
(
    id VARCHAR(3) PRIMARY KEY,
    airport_name      VARCHAR(255),
    longitude         NUMERIC(9, 6),
    latitude          NUMERIC(9, 6),
    iata_city_code    VARCHAR(3),
    iso_country_code  VARCHAR(2),
    location_type     VARCHAR(254),
    offset_utc        VARCHAR(10),
    timezone_id       VARCHAR(255)
);

CREATE TABLE aircraft
(
    id VARCHAR(3) PRIMARY KEY,
    aircraft_name      VARCHAR(255) NOT NULL
);

CREATE TABLE flight_schedule_operation_period
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    start_date_utc      DATE        NOT NULL,
    end_date_utc        DATE        NOT NULL,
    operation_days_utc  VARCHAR(10) NOT NULL,
    start_date_lt       DATE        NOT NULL,
    end_date_lt         DATE        NOT NULL,
    operation_days_lt   VARCHAR(10) NOT NULL
);

CREATE TABLE flight_schedule
(
    id  INT AUTO_INCREMENT PRIMARY KEY,
    airline_code        VARCHAR(3),
    operation_period_id INT NOT NULL,
    flight_number       INT,
    suffix              VARCHAR(255),
    FOREIGN KEY (airline_code) REFERENCES airline (id),
    FOREIGN KEY (operation_period_id) REFERENCES flight_schedule_operation_period (id)
);

CREATE TABLE flight_schedule_data_element
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    flight_schedule_id        INT NOT NULL,
    start_leg_sequence_number INT NOT NULL,
    end_leg_sequence_number   INT NOT NULL,
    ssim_code                 INT NOT NULL,
    value                     VARCHAR(255),
    FOREIGN KEY (flight_schedule_id) REFERENCES flight_schedule (id)
);

CREATE TABLE flight_schedule_leg
(
    id                                INT AUTO_INCREMENT PRIMARY KEY,
    flight_schedule_id                    INT        NOT NULL,
    leg_sequence_number                   INT        NOT NULL,
    origin_airport                        VARCHAR(3) NOT NULL,
    destination_airport                   VARCHAR(3) NOT NULL,
    iata_service_type_code                VARCHAR(3) NOT NULL,
    aircraft_owner_airline_code           VARCHAR(3),
    aircraft_code                         VARCHAR(3) NOT NULL,
    aircraft_configuration_version        VARCHAR(255),
    registration                          VARCHAR(255),
    op                                    BOOLEAN,
    aircraft_departure_time_utc           INT,
    aircraft_departure_time_date_diff_utc INT,
    aircraft_departure_time_lt            INT,
    aircraft_departure_time_diff_lt       INT,
    aircraft_departure_time_variation     INT,
    aircraft_arrival_time_utc             INT,
    aircraft_arrival_time_date_diff_utc   INT,
    aircraft_arrival_time_lt              INT,
    aircraft_arrival_time_diff_lt         INT,
    aircraft_arrival_time_variation       INT,
    duration_minutes                      INT,
    distance_kilometers                   INT,
    drawable_origin_longitude             NUMERIC(9, 6),
    drawable_destination_longitude        NUMERIC(9, 6),

    FOREIGN KEY (flight_schedule_id) REFERENCES flight_schedule (id),
    FOREIGN KEY (origin_airport) REFERENCES airport (id),
    FOREIGN KEY (destination_airport) REFERENCES airport (id),
    FOREIGN KEY (aircraft_owner_airline_code) REFERENCES airline (id),
    FOREIGN KEY (aircraft_code) REFERENCES aircraft (id)
);

CREATE TABLE flight_schedule_cron_run
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    cron_run_date_utc     VARCHAR(10) NOT NULL,
    cron_run_finish       TIMESTAMP,
    aircraft_count        INT,
    airline_count         INT,
    airport_count         INT,
    flight_schedule_count INT
);

CREATE TABLE k163243_flightvisualizer.flight_schedule_data_element_hibernate_sequences
(
    sequence_name VARCHAR(255) NOT NULL,
    next_val      BIGINT,
    PRIMARY KEY (sequence_name)
);

INSERT INTO flight_schedule_data_element_hibernate_sequences (sequence_name, next_val)
VALUES ('flight_schedule_data_element', 1400000);

CREATE TABLE k163243_flightvisualizer.flight_schedule_leg_hibernate_sequences
(
    sequence_name VARCHAR(255) NOT NULL,
    next_val      BIGINT,
    PRIMARY KEY (sequence_name)
);

INSERT INTO flight_schedule_leg_hibernate_sequences (sequence_name, next_val)
VALUES ('flight_schedule_leg', 200000);

CREATE TABLE k163243_flightvisualizer.flight_schedule_hibernate_sequences
(
    sequence_name VARCHAR(255) NOT NULL,
    next_val      BIGINT,
    PRIMARY KEY (sequence_name)
);

INSERT INTO flight_schedule_hibernate_sequences (sequence_name, next_val)
VALUES ('flight_schedule', 200000);