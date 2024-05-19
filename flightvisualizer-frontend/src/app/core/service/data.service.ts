import { Injectable } from '@angular/core';
import { filter, map, Observable, of } from "rxjs";
import {HttpClient, HttpEventType, HttpHeaders, HttpRequest} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {
  AirportDetails,
  AirportRender,
  AirportRenders,
  DetailedLegInformations,
  FlightDateFrequencies,
  LegRender
} from "../../protos/objects";
import {
  CombinedFilterRequest,
  GeneralFilter,
  RouteFilter,
  SelectedAirportFilter, SpecificRouteFilterRequest,
  TimeFilter
} from "../../protos/filters";
import {SandboxModeResponseObject} from "../../protos/dtos";

@Injectable({
  providedIn: 'root'
})
export class DataService {

  private apiEndpoint = environment.apiEndpoint;
  constructor(private http: HttpClient) { }

  getAirports() {
    const headers = new HttpHeaders({ 'Accept': 'application/x-protobuf' });
    return this.http.get(this.apiEndpoint + 'airports', {
      headers,
      responseType: 'arraybuffer'
    }).pipe(
      map(arrayBuffer => {
        const airportRendersMessage = AirportRenders.decode(new Uint8Array(arrayBuffer));
        return airportRendersMessage.airports;
      })
    );
  }

  getFlightDateFrequencies() {
    const headers = new HttpHeaders({ 'Accept': 'application/x-protobuf' });
    return this.http.get(this.apiEndpoint + 'flightdatefrequency', {
      headers,
      responseType: 'arraybuffer'
    }).pipe(
      map(arrayBuffer => {
        const flightDateFrequencies = FlightDateFrequencies.decode(new Uint8Array(arrayBuffer));
        return flightDateFrequencies.frequencies;
      })
    );
  }

  getAllLegsForSpecificRoute(leg: LegRender, timeFilter: TimeFilter) {
    const request = SpecificRouteFilterRequest.create({legRender: leg, timeFilter: timeFilter});
    const blob = new Blob([SpecificRouteFilterRequest.encode(request).finish()], { type: 'application/x-protobuf' });

    const req = new HttpRequest('POST', this.apiEndpoint + 'flightScheduleLeg/routedetail', blob, {
      headers: new HttpHeaders({ 'Accept': 'application/x-protobuf' }),
      reportProgress: true,
      responseType: 'arraybuffer'
    });

    return this.http.request(req).pipe(
      filter(event => event.type === HttpEventType.Response),
      map((event) => {
        // @ts-ignore
        return DetailedLegInformations.decode(new Uint8Array(event.body)).detailedLegs;
      })
    );
  }

  getDistinctFlightScheduleLegsForRendering(timeFilter: TimeFilter, generalFilter: GeneralFilter, routeFilter: RouteFilter, selectedAirportFilter: SelectedAirportFilter): Observable<SandboxModeResponseObject> {
    const combinedFilter = CombinedFilterRequest.create({timeFilter: timeFilter, generalFilter: generalFilter, routeFilter: routeFilter, selectedAirportFilter: selectedAirportFilter});
    const blob = new Blob([CombinedFilterRequest.encode(combinedFilter).finish()], { type: 'application/x-protobuf' });

    const req = new HttpRequest('POST', this.apiEndpoint + 'flightScheduleLeg/distinct', blob, {
      headers: new HttpHeaders({ 'Accept': 'application/x-protobuf' }),
      reportProgress: true,
      responseType: 'arraybuffer'
    });

    return this.http.request(req).pipe(
      filter(event => event.type === HttpEventType.Response),
      map((event) => {
        // @ts-ignore
        return SandboxModeResponseObject.decode(new Uint8Array(event.body));
      })
    );
  }

  getAirportDetails(airportRender: AirportRender): Observable<AirportDetails> {
    const blob = new Blob([AirportRender.encode(airportRender).finish()], { type: 'application/x-protobuf' });

    const req = new HttpRequest('POST', this.apiEndpoint + 'airport/detail', blob, {
      headers: new HttpHeaders({ 'Accept': 'application/x-protobuf' }),
      reportProgress: true,
      responseType: 'arraybuffer'
    });

    return this.http.request(req).pipe(
      filter(event => event.type === HttpEventType.Response),
      map((event) => {
        // @ts-ignore
        return AirportDetails.decode(new Uint8Array(event.body));
      })
    );
  }



  /**
   * Handle a Http operation that failed, without crashing the app.
   *
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO refactor
      console.error(error); // log to console instead

      // TODO refactor
      this.log(`${operation} failed: ${error.message}`);

      return of(result as T);
    };
  }

  private log(message: string) {
    console.log(`DEBUG: ${message}`);
  }

  private convertDateToUTCString(date: string | undefined) {
    if(!date) return '';
    const utcDate = new Date(date);
    return utcDate ? utcDate.getUTCFullYear() + '-' + ((utcDate.getUTCMonth() + 1) > 9 ? (utcDate.getUTCMonth() + 1) : '0' + (utcDate.getUTCMonth() + 1)) + '-' + (utcDate.getUTCDate() > 9 ? (utcDate.getUTCDate()) : '0' + (utcDate.getUTCDate())) : '';
  }
}
