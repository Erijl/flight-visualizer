import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MapComponent } from './map/map.component';
import {HttpClientModule} from "@angular/common/http";
import {FormsModule} from "@angular/forms";
import { HistogramComponent } from './histogram/histogram.component';
import {MatSlider, MatSliderRangeThumb} from "@angular/material/slider";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow,
  MatHeaderRowDef, MatRow, MatRowDef,
  MatTable
} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {MatSort, MatSortModule} from "@angular/material/sort";
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { AirportInfoComponent } from './airport-info/airport-info.component';
import {CommonModule} from "@angular/common";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatInputModule} from "@angular/material/input";
import {MatFormFieldModule} from "@angular/material/form-field";
import {provideNativeDateAdapter} from "@angular/material/core";
import {MatSlideToggle, MatSlideToggleModule} from "@angular/material/slide-toggle";
import { RouteInfoComponent } from './route-info/route-info.component';
import {MatExpansionModule} from "@angular/material/expansion";
import {MatIcon} from "@angular/material/icon";
import {BrowserAnimationsModule, provideAnimations} from "@angular/platform-browser/animations";
import { TimePanelComponent } from './time-panel/time-panel.component';
import {MatCheckboxModule} from "@angular/material/checkbox";

@NgModule({
  declarations: [
    AppComponent,
    MapComponent,
    HistogramComponent,
    AirportInfoComponent,
    RouteInfoComponent,
    TimePanelComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    MatSlider,
    MatSliderRangeThumb,
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatCell,
    MatPaginator,
    MatHeaderCellDef,
    MatCellDef,
    MatHeaderRowDef,
    MatRowDef,
    MatHeaderRow,
    MatRow,
    MatButtonToggleGroup,
    MatButtonToggle,
    MatSort,
    MatSortModule,
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatSlideToggle,
    MatExpansionModule,
    MatIcon,
    MatCheckboxModule,
    MatSlideToggleModule
  ],
  providers: [
    provideAnimationsAsync('noop'),
    provideNativeDateAdapter(),
    provideAnimations(),
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
