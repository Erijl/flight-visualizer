import { Injectable } from '@angular/core';
import {catchError, filter, map, mergeMap, Observable, of, tap} from "rxjs";
import {
  Airport,
  FlightDateFrequencyDto,
  FlightSchedule,
  FlightScheduleRouteDto
} from "../dto/airport";
import {HttpClient, HttpEventType, HttpHeaders, HttpRequest} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {DateRange, LegRender, LegRenders, TimeFilter} from "../../protos/objects";
import {RouteFilter} from "../../protos/filters";
import {RouteFilterType} from "../../protos/enums";

@Injectable({
  providedIn: 'root'
})
export class DataService {

  private apiEndpoint = environment.apiEndpoint;
  constructor(private http: HttpClient) { }

  getAirports() {
    return this.http.get<Airport[]>(this.apiEndpoint + 'airports')
        .pipe(
            tap(_ => this.log('fetched Airports')),
            catchError(this.handleError<Airport[]>('getAirports', []))
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

  getDistinctFlightScheduleLegsForRendering(timeFilter: TimeFilter): Observable<LegRender[]> {
    const encodedTimeFilter = TimeFilter.encode(timeFilter).finish();
    const blob = new Blob([encodedTimeFilter], { type: 'application/x-protobuf' });

    const req = new HttpRequest('POST', this.apiEndpoint + 'flightScheduleLeg/distinct', blob, {
      headers: new HttpHeaders({ 'Accept': 'application/x-protobuf' }),
      reportProgress: true, // Optionally report upload progress
      responseType: 'arraybuffer'
    });

    return this.http.request(req).pipe(
      filter(event => event.type === HttpEventType.Response), // Filter for the response event
      map((event) => {
        console.log('event', event);
        // @ts-ignore
        const legRendersMessage = LegRenders.decode(new Uint8Array(event.body));
        return legRendersMessage.legs;
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
