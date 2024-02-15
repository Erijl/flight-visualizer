CREATE TABLE airline
(
    airline_id        INT AUTO_INCREMENT PRIMARY KEY,
    iata_airline_code VARCHAR(3) NOT NULL,
    icao_airline_code VARCHAR(4) NOT NULL,
    airline_name      VARCHAR(255)
);

CREATE TABLE airport
(
    airport_id        INT AUTO_INCREMENT PRIMARY KEY,
    airport_name      VARCHAR(255)                                     NOT NULL,
    iata_airport_code VARCHAR(3)                                       NOT NULL,
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
    aircraft_id        INT AUTO_INCREMENT PRIMARY KEY,
    iata_aircraft_code VARCHAR(3)   NOT NULL,
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
    airline_id          INT          NOT NULL,
    operation_period_id INT          NOT NULL,
    flight_number       INT          NOT NULL,
    suffix              VARCHAR(255) NOT NULL,
    FOREIGN KEY (airline_id) REFERENCES airline (airline_id),
    FOREIGN KEY (operation_period_id) REFERENCES flight_schedule_operation_period (operation_period_id)
);

CREATE TABLE flight_schedule_data_element
(
    data_element_id          INT AUTO_INCREMENT PRIMARY KEY,
    flight_schedule_id       INT          NOT NULL,
    start_leg_sequence_number INT          NOT NULL,
    end_leg_sequence_number  INT          NOT NULL,
    ssim_code                VARCHAR(255) NOT NULL,
    value                    VARCHAR(255) NOT NULL,
    FOREIGN KEY (flight_schedule_id) REFERENCES flight_schedule (flight_schedule_id)
);

CREATE TABLE flight_scheudle_leg
(
    leg_id              INT AUTO_INCREMENT PRIMARY KEY,
    flight_schedule_id  INT          NOT NULL,
    leg_sequence_number INT          NOT NULL,
    origin_airport_id   INT          NOT NULL,
    destination_airport_id INT       NOT NULL,
    iata_service_type_code VARCHAR(3) NOT NULL, --
    aircraft_owner_airline_id INT      NOT NULL, --
    aircraft_type_id    INT          NOT NULL, --
    aircraft_configuration_version  VARCHAR(255) NOT NULL,
    registration    VARCHAR(255) NOT NULL,
    op BOOL NOT NULL,
    aircraft_departure_time_utc TIME NOT NULL,
    aircraft_departure_time_date_diff_utc INT NOT NULL,
    aircraft_departure_time_lt TIME NOT NULL,
    aircraft_departure_time_diff_lt INT NOT NULL,
    aircraft_departure_time_variation VARCHAR(255) NOT NULL,
    aircraft_arrival_time_utc TIME NOT NULL,
    aircraft_arrival_time_date_diff_utc INT NOT NULL,
    aircraft_arrival_time_lt TIME NOT NULL,
    aircraft_arrival_time_diff_lt INT NOT NULL,
    aircraft_arrival_time_variation VARCHAR(255) NOT NULL,

    FOREIGN KEY (flight_schedule_id) REFERENCES flight_schedule (flight_schedule_id),
    FOREIGN KEY (origin_airport_id) REFERENCES airport (airport_id),
    FOREIGN KEY (destination_airport_id) REFERENCES airport (airport_id),
    FOREIGN KEY (aircraft_owner_airline_id) REFERENCES airline (airline_id),
    FOREIGN KEY (aircraft_type_id) REFERENCES aircraft (aircraft_id)
);