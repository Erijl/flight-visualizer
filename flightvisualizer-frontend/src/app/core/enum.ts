import {AircraftTimeFilterType, AirportDisplayType, RouteDisplayType, RouteFilterType} from "../protos/enums";

export const RouteFilterTypeLabels: { [key in RouteFilterType]: string } = {
  [RouteFilterType.DURATION]: 'Duration',
  [RouteFilterType.DISTANCE]: 'Distance',
  [RouteFilterType.UNRECOGNIZED]: 'TECHNICAL VALUE'
};

export const AirportDisplayTypeLabels: { [key in AirportDisplayType]: string } = {
  [AirportDisplayType.AIRPORTDISPLAYTYPE_ALL]: 'All',
  [AirportDisplayType.AIRPORTDISPLAYTYPE_WITHROUTES]: 'With Routes',
  [AirportDisplayType.AIRPORTDISPLAYTYPE_NONE]: 'None',
  [RouteFilterType.UNRECOGNIZED]: 'TECHNICAL VALUE'
};

export const RouteDisplayTypeLabels: { [key in RouteDisplayType]: string } = {
  [RouteDisplayType.ROUTEDISPLAYTYPE_ALL]: 'All',
  [RouteDisplayType.ROUTEDISPLAYTYPE_SPECIFICAIRPORT]: 'Specific Airport',
  [RouteDisplayType.ROUTEDISPLAYTYPE_ONLYWITHINSAMECOUNTRY]: 'Only within same country',
  [RouteDisplayType.ROUTEDISPLAYTYPE_WITHINSAMEREGION]: 'Within same region',
  [RouteDisplayType.ROUTEDISPLAYTYPE_WITHINSAMETIMEZONE]: 'Within same timezone',
  [RouteDisplayType.UNRECOGNIZED]: 'TECHNICAL VALUE'
};

export const AircraftTimeFilterTypeLabels: { [key in AircraftTimeFilterType]: string } = {
  [AircraftTimeFilterType.ARRIVALANDDEPARTURE]: 'Arrival and Departure in period',
  [AircraftTimeFilterType.DEPARTURE]: 'Departure in period',
  [AircraftTimeFilterType.ARRIVAL]: 'Arrival in period',
  [AircraftTimeFilterType.UNRECOGNIZED]: 'TECHNICAL VALUE'
};

export enum ModeSelection {
  NONE,
  SANDBOX,
  LIVE_FEED,
  GRAPH_THEORY
}

export enum LiveFeedSpeedMultiplierString {
  ONE_X = "oneX",
  EIGHT_X = "eightX",
  SIXTEEN_X = "sixteenX",
  THIRTYTWO_X = "thirtytwoX",
  SIXTYFOUR_X = "sixtyfourX",
  NINETYSIX_X = "ninetysixX",
}

export const LiveFeedSpeedMultiplier: { [key in LiveFeedSpeedMultiplierString]: string } = {
  [LiveFeedSpeedMultiplierString.ONE_X]: '1',
  [LiveFeedSpeedMultiplierString.EIGHT_X]: '8',
  [LiveFeedSpeedMultiplierString.SIXTEEN_X]: '16',
  [LiveFeedSpeedMultiplierString.THIRTYTWO_X]: '32',
  [LiveFeedSpeedMultiplierString.SIXTYFOUR_X]: '64',
  [LiveFeedSpeedMultiplierString.NINETYSIX_X]: '96'
};

export enum DetailSelectionType {
  AIRPORT = "airport",
  ROUTE = "route",
  AIRPLANE = "airplane",
}

export enum LayerType {
  AIRPORTLAYER = "airportLayer",
  ROUTELAYER = "routeLayer",
  AIRPLANELAYER = "airplaneLayer",
  AIRPORTHIGHLIGHTLAYER = "airportHighlightLayer",
  ROUTEHIGHLIGHTLAYER = "routeHighlightLayer",
}

export enum SourceType {
  AIRPORTSOURCE = "airportSource",
  ROUTESOURCE = "routeSource",
  AIRPLANESOURCE = "airplaneSource",
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
