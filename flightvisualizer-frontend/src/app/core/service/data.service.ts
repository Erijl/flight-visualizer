import { Injectable } from '@angular/core';
import {catchError, Observable, of, tap} from "rxjs";
import {Airport, FlightScheduleLeg} from "../dto/airport";
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

  getFlightScheduleLegRoutes() {
    return this.http.get<FlightScheduleLeg[]>(this.apiEndpoint + 'flightScheduleLegs')
      .pipe(
        tap(_ => this.log('fetched getFlightScheduleLegRoutes')),
        catchError(this.handleError<FlightScheduleLeg[]>('getFlightScheduleLegRoutes', []))
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
}
