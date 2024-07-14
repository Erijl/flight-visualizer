import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AirportInfoComponent } from "./airport-info/airport-info.component";
import { RouteInfoComponent } from "./route-info/route-info.component";
import { AirplaneInfoComponent } from "./airplane-info/airplane-info.component";
import { PipesModule } from "../../pipes/pipes.module";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { AppRoutingModule } from "../../app-routing.module";
import { HttpClientModule } from "@angular/common/http";
import { FormsModule } from "@angular/forms";
import { MatSlider, MatSliderRangeThumb } from "@angular/material/slider";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow,
  MatHeaderRowDef, MatRow, MatRowDef,
  MatTable
} from "@angular/material/table";
import { MatPaginator } from "@angular/material/paginator";
import { MatButtonToggle, MatButtonToggleGroup } from "@angular/material/button-toggle";
import { MatSort, MatSortModule } from "@angular/material/sort";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatSlideToggle, MatSlideToggleModule } from "@angular/material/slide-toggle";
import { MatExpansionModule } from "@angular/material/expansion";
import { MatIcon } from "@angular/material/icon";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatCardModule } from "@angular/material/card";


@NgModule({
  declarations: [
    AirportInfoComponent,
    RouteInfoComponent,
    AirplaneInfoComponent
  ],
  imports: [
    CommonModule,
    PipesModule,
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
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatSlideToggle,
    MatExpansionModule,
    MatIcon,
    MatCheckboxModule,
    MatSlideToggleModule,
    MatCardModule,
  ],
  exports: [
    AirportInfoComponent,
    RouteInfoComponent,
    AirplaneInfoComponent
  ]
})
export class DetailPanelModule {
}
