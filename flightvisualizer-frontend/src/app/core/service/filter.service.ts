import {Injectable} from '@angular/core';
import {FlightScheduleRouteDto} from "../dto/airport";
import {RouteFilter, SelectedAirportFilter, TimeFilter} from "../../protos/filters";
import {AircraftTimeFilterType, RouteFilterType} from "../../protos/enums";
import {AirportRender, LegRender} from "../../protos/objects";

@Injectable({
  providedIn: 'root'
})
export class FilterService {

  constructor() {
  }

  getAllAirportsPresentInLegRenders(allLegRenders: LegRender[], allAirports: AirportRender[]): AirportRender[] {
    const filteredAirports: AirportRender[] = [];
    allLegRenders.forEach((legRender: LegRender) => {
      const originAirportRender = allAirports.find(airport => airport.iataCode == legRender.originAirportIataCode);
      const destinationAirportRender = allAirports.find(airport => airport.iataCode == legRender.destinationAirportIataCode);
      if(originAirportRender) {
        filteredAirports.push(originAirportRender);
      }
      if(destinationAirportRender) {
        filteredAirports.push(destinationAirportRender);
      }
    });
    return filteredAirports;
  }
}
