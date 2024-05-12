import { Injectable } from '@angular/core';
import {catchError, map, Observable, of, tap} from "rxjs";
import {
  Airport,
  FlightDateFrequencyDto,
  FlightSchedule,
  FlightScheduleRouteDto,
  DateRange
} from "../dto/airport";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {LegRender} from "../../protos/objects";

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

  getDistinctFlightScheduleLegsForRendering(dateRange: DateRange): Observable<LegRender[]> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/x-protobuf',
      'Accept': 'application/x-protobuf'
    });
    return this.http.get(this.apiEndpoint + 'flightScheduleLeg/distinct', { headers, responseType: 'text' }).pipe(
      map(response => {
        let legRenders: LegRender[] = [];
        const base64Strings = response.split("==");
        base64Strings.forEach(base64String => {
          const binaryString = atob(base64String);
          const len = binaryString.length;
          const bytes = new Uint8Array(len);
          for (let i = 0; i < len; i++) {
            bytes[i] = binaryString.charCodeAt(i);
          }
          const legRender = LegRender.decode(bytes);
          legRenders.push(legRender);
        });
        return legRenders;
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
