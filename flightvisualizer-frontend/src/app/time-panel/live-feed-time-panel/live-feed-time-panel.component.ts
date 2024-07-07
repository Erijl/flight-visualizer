import { Component } from '@angular/core';
import {LiveFeedSpeedModifier} from "../../core/enum";

@Component({
  selector: 'app-live-feed-time-panel',
  templateUrl: './live-feed-time-panel.component.html',
  styleUrl: './live-feed-time-panel.component.css'
})
export class LiveFeedTimePanelComponent {

  selectionType: LiveFeedSpeedModifier = LiveFeedSpeedModifier.ONE_X;

  onSpeedModifierChange() {

  }
}
