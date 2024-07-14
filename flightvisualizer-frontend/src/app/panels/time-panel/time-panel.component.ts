import { Component, OnDestroy, OnInit } from '@angular/core';
import { DataStoreService } from "../../core/service/data-store.service";
import { state, style, trigger } from "@angular/animations";
import { Subscription } from "rxjs";
import { ModeSelection } from "../../core/enum";

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
export class TimePanelComponent implements OnInit, OnDestroy {

  modeSelectionSubscription!: Subscription;

  expanded: boolean = true;
  modeSelection: ModeSelection = ModeSelection.NONE;

  constructor(private dataStoreService: DataStoreService) {
  }

  ngOnInit() {
    this.modeSelectionSubscription = this.dataStoreService.modeSelection.subscribe(modeSelection => {
      this.modeSelection = modeSelection;
    })
  }

  ngOnDestroy() {
    this.modeSelectionSubscription.unsubscribe();
  }

  protected readonly ModeSelection = ModeSelection;
}
