import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import {DataStoreService} from "../core/service/data-store.service";
import {DefaultTimeFilter} from "../core/dto/airport";
import {state, style, trigger} from "@angular/animations";
import {Subscription} from "rxjs";
import {AircraftTimeFilterType} from "../protos/enums";
import {TimeFilter} from "../protos/filters";
import {AircraftTimeFilterTypeLabels} from "../core/enum";

@Component({
  selector: 'app-time-panel',
  animations: [
    trigger('openClose', [
      // ...
      state('open', style({
        transition: 'transform 0.5s',
        transform: 'rotate(0deg)'
      })),
      state('closed', style({
        transition: 'transform 0.5s',
        transform: 'rotate(180deg)'
      })),
    ]),
  ],
  templateUrl: './time-panel.component.html',
  styleUrl: './time-panel.component.css'
})
export class TimePanelComponent {

  expanded: boolean = true;
}
