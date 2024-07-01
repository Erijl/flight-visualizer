[![Flight Visualizer](./assets/flight-visualizer_banner.png)](https://flight-visualizer.com)

<div align="center">

![Frontend Deployment](https://img.shields.io/github/actions/workflow/status/erijl/flight-visualizer/main.yml)
![Backend Deployment](https://img.shields.io/github/actions/workflow/status/erijl/flight-visualizer/publish_backend-docker-image.yml)

</div>

Flight Visualizer is an open-source web application that lets you explore Lufthansa's global flight network.
Track flights, discover intricate details and enjoy the data. [Check it out here!](https://flight-visualizer.com)

## Highlights

Some technical highlights, not necessarily unique but definitely uncommon and technically interesting

### Protocol Buffers as SSoT data model with REST API

I needed a shared data model between client and server, from a single source of truth. Instead of going the usual route,
I chose Protocol Buffers in combination with a standard Rest API. Not to common, but this not only assures that the
data model is consistent between client and server but also allows for a more efficient data transfer and better
performance than typical methods. [More Details](./api-models/README.md)

### Theoretical Realtime flight visualisation

The 'theoretical' flight visalisation utilizes parts of the Haversine formula to theoretically calculate the position
of an aircraft at any given time based on the departure and arrival times as well as an approximated distance time
function of an average airplane. [More Details](./flightvisualizer-backend/README.md)

### Graph Theory visualisation

The graph theory visualisation is a unique way to visualize the flight network of Lufthansa. It uses a force directed
graph layout to show the connections between airports and the number of flights between
them. [More Details](./flightvisualizer-frontend/README.md)

## Getting Started / Contributing

There is currently no local development setup guide planned since it involves many dependencies, like a Mapbox
account, which in of itself needs a credit card to be set up. If you however are still interested, open an Issue and
I will create a fully comprehensive guide for local development including sample data for the database.

## Project Structure

The Project is divided into the following main directories,
please note that each main directory contains its own readme for more detailed information:

    /api-models - Protocol Buffer files for the SSoT data models
    /flightvisualizer-backend - Spring Boot backend code
    /flightvisualizer-frontend - Angular frontend code
    /flightvisualizer-database - Scripts for setting up the database
    /assets - Static assets for the readme
    ...

## Contributors

> <img src="https://avatars.githubusercontent.com/erijl" height="60px" title="Justus M." alt="Portrait"/> | <a href="https://github.com/erijl" target="_blank">@erijl</a>

## License

This project is licensed under the [MIT License](LICENSE).

Favicon & Icon used with slight color alteration:
Globe-trotterderivative work: LtPowers, CC BY-SA 3.0 <https://creativecommons.org/licenses/by-sa/3.0>, via Wikimedia
Commons
