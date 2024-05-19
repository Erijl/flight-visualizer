import { Injectable } from '@angular/core';
import {catchError, filter, map, Observable, of, tap} from "rxjs";
import {
  FlightDateFrequencyDto,
  FlightSchedule,
  FlightScheduleRouteDto
} from "../dto/airport";
import {HttpClient, HttpEventType, HttpHeaders, HttpRequest} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {AirportRenders, DateRange} from "../../protos/objects";
import {
  CombinedFilterRequest,
  GeneralFilter,
  RouteFilter,
  SelectedAirportFilter,
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

  getFlightScheduleLegRoutes(dateRange: DateRange) {
    return this.http.get<FlightScheduleRouteDto[]>(this.apiEndpoint + 'flightScheduleLeg/distance?startDate=' + this.convertDateToUTCString(dateRange.start?.toUTCString()) + '&endDate=' + this.convertDateToUTCString(dateRange.end?.toUTCString()))
      .pipe(
        tap(_ => this.log('fetched getFlightScheduleLegRoutes')),
        catchError(this.handleError<FlightScheduleRouteDto[]>('getFlightScheduleLegRoutes', []))
      );
  }

  getFlightDateFrequencies() {
    return this.http.get<FlightDateFrequencyDto[]>(this.apiEndpoint + 'flightdatefrequency')
      .pipe(
        tap(_ => this.log('fetched getFlightDateFrequencies')),
        catchError(this.handleError<FlightDateFrequencyDto[]>('getFlightDateFrequencies', []))
      );
  }

  getFlightScheduleById(id: number) {
    return this.http.get<FlightSchedule>(this.apiEndpoint + 'flightschedule?id=' + id)
      .pipe(
        tap(_ => this.log('fetched getFlightScheduleById')),
        catchError(this.handleError<FlightSchedule>('getFlightScheduleById', new FlightSchedule()))
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
      filter(event => event.type === HttpEventType.Response), // Filter for the response event
      map((event) => {
        console.log('event', event);
        // @ts-ignore
        return SandboxModeResponseObject.decode(new Uint8Array(event.body));
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
