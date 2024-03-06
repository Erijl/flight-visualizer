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

@NgModule({
  declarations: [
    AppComponent,
    MapComponent,
    HistogramComponent
  ],
  imports: [
    BrowserModule,
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
    MatRow
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
