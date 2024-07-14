import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SandboxTimePanelComponent } from "./sandbox-time-panel/sandbox-time-panel.component";
import { LiveFeedTimePanelComponent } from "./live-feed-time-panel/live-feed-time-panel.component";
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from "@angular/material/expansion";
import { MatFormField, MatLabel, MatSuffix } from "@angular/material/form-field";
import {
  MatDatepickerToggle,
  MatDateRangeInput,
  MatDateRangePicker,
  MatEndDate,
  MatStartDate
} from "@angular/material/datepicker";
import { MatSlider, MatSliderRangeThumb } from "@angular/material/slider";
import { MatSlideToggle } from "@angular/material/slide-toggle";
import { FormsModule } from "@angular/forms";
import { PipesModule } from "../pipes/pipes.module";
import { MatButtonToggle, MatButtonToggleGroup } from "@angular/material/button-toggle";


@NgModule({
  declarations: [
    SandboxTimePanelComponent,
    LiveFeedTimePanelComponent
  ],
  imports: [
    CommonModule,
    MatExpansionPanel,
    MatExpansionPanelTitle,
    MatFormField,
    MatDateRangeInput,
    MatDatepickerToggle,
    MatDateRangePicker,
    MatSlider,
    MatSlideToggle,
    FormsModule,
    MatEndDate,
    MatExpansionPanelHeader,
    MatLabel,
    MatSliderRangeThumb,
    MatStartDate,
    MatSuffix,
    PipesModule,
    MatButtonToggle,
    MatButtonToggleGroup
  ],
  exports: [
    SandboxTimePanelComponent,
    LiveFeedTimePanelComponent
  ]
})
export class TimePanelModule {
}
