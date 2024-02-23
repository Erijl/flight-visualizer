import { Injectable } from '@angular/core';
import {Airport, FlightScheduleRouteDto} from "../dto/airport";

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

      if(incomingRoutes && flightScheduleRouteDto.destinationAirport.iataAirportCode == airport.iataAirportCode) {
        allFlightScheduleRouteDtosByAirport.push(flightScheduleRouteDto);
      } else if(outgoingRoutes && flightScheduleRouteDto.originAirport.iataAirportCode == airport.iataAirportCode) {
        allFlightScheduleRouteDtosByAirport.push(flightScheduleRouteDto);
      }
    });
    return allFlightScheduleRouteDtosByAirport;
  }
}
