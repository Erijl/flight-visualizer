
export enum AirportDisplayType {
  ALL = "All",
  WITHROUTES = "With Routes",
  NONE = "None"
}

export enum RouteDisplayType {
  ALL = "All",
  SPECIFICAIRPORT = "Specific Airport",
  ONLYWITHINSAMECOUNTRY = "Only within same country",
  WITHINSAMEREGION = "Within same region",
  WITHINSAMETIMEZONE = "Within same timezone",
  //TODO add for specifc country
}

export enum RouteFilterType {
  DURATION = "Duration",
  DISTANCE = "Distance",
  ROUTECOUNT = "Route Count", //TODO AIRPORTROUTECOUNT(?)
}

export enum AircraftTimeFilterType {
  ARRIVALANDDEPARTURE = "Arrival and Departure in period",
  DEPARTURE = "Departure in period",
  ARRIVAL = "Arrival in period",
}
