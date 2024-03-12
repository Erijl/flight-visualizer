import {Injectable} from '@angular/core';
import {Airport, FlightScheduleRouteDto, SelectedTimeRange} from "../dto/airport";

@Injectable({
  providedIn: 'root'
})
export class FilterService {

  constructor() { }

  getAllAirportsPresentInFlightScheduleRouteDtos(allFlightScheduleRouteDtos: FlightScheduleRouteDto[]): Airport[] {
    const allAirports: any = [];
    allFlightScheduleRouteDtos.forEach((flightScheduleRouteDto: FlightScheduleRouteDto) => {
      allAirports.push(flightScheduleRouteDto.originAirport);
      allAirports.push(flightScheduleRouteDto.destinationAirport);
    });
    return allAirports;
  }

  getFlightScheduleRouteDtosByAirport(allFlightScheduleRouteDtos: FlightScheduleRouteDto[], airport: Airport, incomingRoutes: boolean, outgoingRoutes: boolean): FlightScheduleRouteDto[] {
    const allFlightScheduleRouteDtosByAirport: any = [];
    allFlightScheduleRouteDtos.forEach((flightScheduleRouteDto: FlightScheduleRouteDto) => {

      if (incomingRoutes && flightScheduleRouteDto.destinationAirport.iataAirportCode == airport.iataAirportCode) {
        allFlightScheduleRouteDtosByAirport.push(flightScheduleRouteDto);
      } else if (outgoingRoutes && flightScheduleRouteDto.originAirport.iataAirportCode == airport.iataAirportCode) {
        allFlightScheduleRouteDtosByAirport.push(flightScheduleRouteDto);
      }
    });
    return allFlightScheduleRouteDtosByAirport;
  }

  getCleanedFlightScheduleRouteDtos(flightScheduleRouteDtos: FlightScheduleRouteDto[]): FlightScheduleRouteDto[] {
    let cleanedRoutes: FlightScheduleRouteDto[] = [];

    flightScheduleRouteDtos.forEach(route => {
      if (!cleanedRoutes.find(cleanedRoute => this.compareFlightScheduleRouteDtos(route, cleanedRoute))) {
        cleanedRoutes.push(route);
      }
    })
    return cleanedRoutes;
  }

  getFlightRoutesInTimeFrame(flightScheduleRouteDtos: FlightScheduleRouteDto[], timeRange: SelectedTimeRange) {
    let filteredRoutes: FlightScheduleRouteDto[] = [];

    flightScheduleRouteDtos.forEach(route => {
      if(timeRange.start <= route.aircraftDepartureTimeUtc && route.aircraftDepartureTimeUtc <= timeRange.end) {
        if(route.aircraftArrivalTimeUtc < route.aircraftDepartureTimeUtc) {
          if(timeRange.start <= route.aircraftArrivalTimeUtc || route.aircraftArrivalTimeUtc <= timeRange.end) {
            filteredRoutes.push(route);
          }
        } else {
          if(timeRange.start <= route.aircraftArrivalTimeUtc && route.aircraftArrivalTimeUtc <= timeRange.end) {
            filteredRoutes.push(route);
          }
        }
      }
    })

    return filteredRoutes;
  }

  compareFlightScheduleRouteDtos(originalFlightRoute: FlightScheduleRouteDto, alteredFlightRoute: FlightScheduleRouteDto): boolean {
    return (
        originalFlightRoute.originAirport.iataAirportCode == alteredFlightRoute.originAirport.iataAirportCode &&
        originalFlightRoute.destinationAirport.iataAirportCode == alteredFlightRoute.destinationAirport.iataAirportCode
      )
      ||
      (
        originalFlightRoute.originAirport.iataAirportCode == alteredFlightRoute.destinationAirport.iataAirportCode &&
        originalFlightRoute.destinationAirport.iataAirportCode == alteredFlightRoute.originAirport.iataAirportCode
      );
  }
}
