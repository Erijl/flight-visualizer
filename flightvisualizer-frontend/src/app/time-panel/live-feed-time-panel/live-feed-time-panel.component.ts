import { Component } from '@angular/core';
import {LiveFeedSpeedModifier} from "../../core/enum";
// @ts-ignore
import Tick from '@pqina/flip';
@Component({
  selector: 'app-live-feed-time-panel',
  templateUrl: './live-feed-time-panel.component.html',
  styleUrl: './live-feed-time-panel.component.css'
})
export class LiveFeedTimePanelComponent {

  selectionType: LiveFeedSpeedModifier = LiveFeedSpeedModifier.ONE_X;


  handleTickInit(tick: { value: number; }) {

    // update the value every 5 seconds
    var interval = Tick.helper.duration(5, 'seconds');

    // value to add each interval
    var valuePerInterval = 5;

    // offset is a fixed date in the past
    var dateOffset = Tick.helper.date('2019-01-01');

    // value to start with, the value of the counter at the offset date
    var valueOffset = 0;

    // uncomment lines below (and comment line above) if you want offset be set to the first time the user visited the page
    // var offset = parseInt(localStorage.getItem('tick-value-counter-offset') || Date.now(), 10);
    // localStorage.setItem('tick-value-counter-offset', offset);

    // start updating the counter each second
    Tick.helper.interval(function () {

      // current time in milliseconds
      var now = Date.now();

      // difference with offset time in milliseconds
      var diff = now - dateOffset;

      // total time since offset divide by interval gives us the amount of loops since offset
      var loops = diff / interval;

      // this will make sure we only count completed loops.
      loops = Math.floor(loops);

      // multiply that by the value per interval and you have your final value
      tick.value = valueOffset + (loops * valuePerInterval);

    }, 1000);
  }

  onSpeedModifierChange() {

  }
}
