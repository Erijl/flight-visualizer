import { Injectable } from '@angular/core';
import {catchError, filter, map, Observable, of} from "rxjs";
import {HttpClient, HttpEventType, HttpHeaders, HttpRequest} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {
  AirportDetails,
  AirportRender,
  AirportRenders, DetailedLegInformation,
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
import {ToastService} from "./toast.service";

@Injectable({
  providedIn: 'root'
})
export class DataService {

  private apiEndpoint = environment.apiEndpoint;
  constructor(private http: HttpClient, private toastService: ToastService) { }

  getAirports() {
    const headers = new HttpHeaders({ 'Accept': 'application/x-protobuf' });
    return this.http.get(this.apiEndpoint + 'airports', {
      headers,
      responseType: 'arraybuffer'
    }).pipe(
      map(arrayBuffer => {
        const airportRendersMessage = AirportRenders.decode(new Uint8Array(arrayBuffer));
        return airportRendersMessage.airports;
      }),
      catchError((error, caught) => {
        this.toastService.showToast('Error while requesting backend, please try again later.', 'error');
        console.error('Error fetching getAirports:', error);
        return of([AirportRender.create()]);
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
      }),
      catchError((error, caught) => {
        this.toastService.showToast('Error while requesting backend, please try again later.', 'error');
        console.error('Error fetching getFlightDateFrequencies:', error);
        return of(FlightDateFrequencies.create().frequencies);
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
      }),
      catchError((error, caught) => {
        this.toastService.showToast('Error while requesting backend, please try again later.', 'error');
        console.error('Error fetching getAllLegsForSpecificRoute', error);
        return of([DetailedLegInformation.create()]);
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
      }),
      catchError((error, caught) => {
        this.toastService.showToast('Error while requesting backend, please try again later.', 'error');
        console.error('Error fetching getDistinctFlightScheduleLegsForRendering:', error);
        return of(SandboxModeResponseObject.create());
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
      }),
      catchError((error, caught) => {
        this.toastService.showToast('Error while requesting backend, please try again later.', 'error');
        console.error('Error fetching getAirportDetails:', error);
        return of(AirportDetails.create());
      })
    );
  }
}
