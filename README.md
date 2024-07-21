[![Flight Visualizer](./assets/flight-visualizer_banner.png)](https://flight-visualizer.com)

<div align="center">

![Frontend Deployment](https://img.shields.io/github/actions/workflow/status/erijl/flight-visualizer/main.yml)
![Backend Deployment](https://img.shields.io/github/actions/workflow/status/erijl/flight-visualizer/publish_backend-docker-image.yml)

</div>

Flight Visualizer is an open-source web application that lets you explore Lufthansa's global flight network.
Track flights, discover intricate details and enjoy the data. [Check it out here!](https://flight-visualizer.com)

## Technical Highlights

Some technical highlights, not necessarily unique but definitely uncommon and technically interesting

### Protocol Buffers as SSoT data model with REST API

I needed a shared data model between client and server, from a single source of truth (SSoT). So, instead of going the
usual route, I chose Protocol Buffers in combination with a standard Rest API. Not too common, but this not only assures
that the data model is consistent between client and server but also allows for a more efficient data transfer and better
performance than typical methods. [Implementation](./api-models)

### Theoretical real time flight visualisation

The 'theoretical' flight visualisation utilizes parts of the Haversine formula to theoretically calculate the position
of an aircraft at any given time based on the departure and arrival times. Paring this with a sped up interval and some
speed modifiers, you got yourself a real-time flight visualisation. But sadly, due to performance issues, this had to be implemented into the frontend.
[Implementation](./flightvisualizer-frontend/src/app/core/services/geo.service.ts)

### Automatic CI/CD pipeline with GitHub Actions

The project is set up with a CI/CD pipeline that automatically builds and deploys the frontend to my FTP server.
Additionally, the backend is built with all the necessary environment variables and deployed as a private Docker image to the
Docker Hub. [Implementation](./.github/workflows)

## Getting Started / Contributing

There is currently no local development setup guide planned since it involves many dependencies, like a Mapbox
account, which in of itself needs a credit card to be set up. If you however are still interested, open an Issue and
I will create a fully comprehensive guide for local development including sample data for the database.

## Project Structure

The Project is divided into four main directories, some containing its own readme for more detailed information:

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
