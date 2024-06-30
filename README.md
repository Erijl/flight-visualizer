![Flight Visualizer](./assets/flight-visualizer_banner.png)

Flight Visualizer is an open-source web application that lets you explore Lufthansa's global flight network in a
visually stunning 3D environment. Track flights, discover airport details, and uncover the hidden patterns of air travel
through interactive data visualization.

## Table of Contents

- [Key Features](#key-features)
- [Screenshots](#screenshots)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Technical Details](#technical-details)
- [Project Structure](#project-structure)
- [Data Sources](#data-sources)
- [Contributing](#contributing)
- [License](#license)
- [Acknowledgments](#acknowledgments)
- [Contact](#contact)

## To come

## Highlights
Some technical highlights, not necessarily unique but definitely uncommon:

### Protocol Buffers as SSoT data model with REST API
I needed a way to have a shared data model between the client and server, from a single source of truth. And for
that I chose Protocol Buffers in combination with a standard Rest API. Quite the unique combination but this not
only assures that the data model is consistent between client and server but also allows for a more efficient data
transfer than typical methods. [More Details](./api-models/README.md)

### Theoretical Realtime flight visualisation
The 'theoretical' flight visalisation utilizes parts of the Haversine formula to theoretically calculate the position
of an aircraft at any given time based on the departure and arrival times as well as an approximated distance time 
function of an average airplane. [More Details](./flightvisualizer-backend/README.md)

### Graph Theory visualisation
The graph theory visualisation is a unique way to visualize the flight network of Lufthansa. It uses a force directed
graph layout to show the connections between airports and the number of flights between them. [More Details](./flightvisualizer-frontend/README.md)



## Getting Started

## Technical Details

## Project Structure

The Project is divided into the following main directories,
please note that each main directory contains it's own readme for more detailed information:

    /api-models - Contains the Protocol Buffer (.proto) files defining the data models.
    /flightvisualizer-backend - Spring Boot backend code
    /flightvisualizer-frontend - Angular frontend code
    /flightvisualizer-database - Scripts for setting up the database
    /assets - Static assets for the readme
    ...

## Contributors

> <img src="https://avatars.githubusercontent.com/erijl" height="60px" title="Justus M." alt="Portrait"/> | <a href="https://github.com/erijl" target="_blank">@erijl</a>

## License

This project is licensed under the [MIT License](LICENSE).

