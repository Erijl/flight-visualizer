import { Injectable } from '@angular/core';
import {catchError, Observable, of, tap} from "rxjs";
import {
  Airport,
  FlightDateFrequencyDto,
  FlightSchedule,
  FlightScheduleRouteDto,
  DateRange
} from "../dto/airport";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class DataService {

  private apiEndpoint = 'http://localhost:8080/';
  constructor(private http: HttpClient) { }

  getAirports() {
    return this.http.get<Airport[]>(this.apiEndpoint + 'airports')
        .pipe(
            tap(_ => this.log('fetched Airports')),
            catchError(this.handleError<Airport[]>('getAirports', []))
        );
  }

  getFlightScheduleLegRoutes(dateRange: DateRange) {
    return this.http.get<FlightScheduleRouteDto[]>(this.apiEndpoint + 'flightScheduleLeg/distance?startDate=' + this.convertDateToUTCString(dateRange.start) + '&endDate=' + this.convertDateToUTCString(dateRange.end))
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

  private convertDateToUTCString(date: Date | null) {
    return date ? date.getUTCFullYear() + '-' + ((date.getUTCMonth() + 1) > 9 ? (date.getUTCMonth() + 1) : '0' + (date.getUTCMonth() + 1)) + '-' + (date.getUTCDate() > 9 ? (date.getUTCDate()) : '0' + (date.getUTCDate())) : '';
  }
}
