
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
}

//export enum RouteFilterType {
//  DURATION = "Duration",
//  DISTANCE = "Distance",
//  //possibly add AIRPORTROUTECOUNT(?) in the future
//}

//export enum AircraftTimeFilterType {
//  ARRIVALANDDEPARTURE = "Arrival and Departure in period",
//  DEPARTURE = "Departure in period",
//  ARRIVAL = "Arrival in period",
//}

export enum DetailSelectionType {
  AIRPORT = "airport",
  ROUTE = "route",
}

export enum LayerType {
  AIRPORTLAYER = "airportLayer",
  ROUTELAYER = "routeLayer",
  AIRPORTHIGHLIGHTLAYER = "airportHighlightLayer",
  ROUTEHIGHLIGHTLAYER = "routeHighlightLayer",
}

export enum SourceType {
  AIRPORTSOURCE = "airportSource",
  ROUTESOURCE = "routeSource",
  AIRPORTHIGHLIGHTSOURCE = "airportHighlightSource",
  ROUTEHIGHLIGHTSOURCE = "routeHighlightSource",
}

export enum MapEventType {
  CLICK = "click",
  MOUSEENTER = "mouseenter",
  MOUSELEAVE = "mouseleave",
}

export enum CursorStyles {
  POINTER = "pointer",
  DEFAULT = "",
}
