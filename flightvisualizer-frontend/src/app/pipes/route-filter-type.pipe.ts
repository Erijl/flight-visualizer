import { Pipe, PipeTransform } from '@angular/core';
import {RouteFilterType} from "../protos/filters";

@Pipe({
  name: 'routeFilterType'
})
export class RouteFilterTypePipe implements PipeTransform {

  transform(value: RouteFilterType): string {
    switch (value) {
      case RouteFilterType.DISTANCE:
        return 'km';
      case RouteFilterType.DURATION:
        return 'h';
      default:
        return '';
    }
  }

}
