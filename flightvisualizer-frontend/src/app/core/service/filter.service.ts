import {Injectable} from '@angular/core';
import {FlightScheduleRouteDto} from "../dto/airport";
import {RouteFilter, SelectedAirportFilter, TimeFilter} from "../../protos/filters";
import {AircraftTimeFilterType, RouteFilterType} from "../../protos/enums";
import {AirportRender} from "../../protos/objects";

@Injectable({
  providedIn: 'root'
})
export class FilterService {

  constructor() {
  }

  getAllAirportsPresentInFlightScheduleRouteDtos(allFlightScheduleRouteDtos: FlightScheduleRouteDto[]): AirportRender[] {
    const allAirports: any = [];
    allFlightScheduleRouteDtos.forEach((flightScheduleRouteDto: FlightScheduleRouteDto) => {
      allAirports.push(flightScheduleRouteDto.originAirport);
      allAirports.push(flightScheduleRouteDto.destinationAirport);
    });
    return allAirports;
  }

  getFlightScheduleRouteDtosByAirport(allFlightScheduleRouteDtos: FlightScheduleRouteDto[], selectedAirportFilter: SelectedAirportFilter, incomingRoutes: boolean, outgoingRoutes: boolean): FlightScheduleRouteDto[] {
    const allFlightScheduleRouteDtosByAirport: any = [];
    allFlightScheduleRouteDtos.forEach((flightScheduleRouteDto: FlightScheduleRouteDto) => {

      if (incomingRoutes && flightScheduleRouteDto.destinationAirport.iataAirportCode == selectedAirportFilter.iataCode) {
        allFlightScheduleRouteDtosByAirport.push(flightScheduleRouteDto);
      } else if (outgoingRoutes && flightScheduleRouteDto.originAirport.iataAirportCode == selectedAirportFilter.iataCode) {
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

  calculateFlightDurationInMinutes(flightScheduleRouteDto: FlightScheduleRouteDto): number {
    if(flightScheduleRouteDto.aircraftArrivalTimeDateDiffUtc >= 1) {

      //TODO check MBA -> ZNZ connection again
      if(flightScheduleRouteDto.originAirport.iataAirportCode == 'MBA' && flightScheduleRouteDto.destinationAirport.iataAirportCode == 'ZNZ') {
        return 50;
      }
      return (Math.floor(1439 - flightScheduleRouteDto.aircraftDepartureTimeUtc) + flightScheduleRouteDto.aircraftArrivalTimeUtc);
    } else {
      return Math.floor(flightScheduleRouteDto.aircraftArrivalTimeUtc - flightScheduleRouteDto.aircraftDepartureTimeUtc);
    }
  }
}
