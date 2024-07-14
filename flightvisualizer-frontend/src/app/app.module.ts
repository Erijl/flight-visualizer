import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MapComponent } from './components/map/map.component';
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
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { CommonModule, NgOptimizedImage } from "@angular/common";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatInputModule } from "@angular/material/input";
import { MatFormFieldModule } from "@angular/material/form-field";
import { provideNativeDateAdapter } from "@angular/material/core";
import { MatSlideToggle, MatSlideToggleModule } from "@angular/material/slide-toggle";
import { MatExpansionModule } from "@angular/material/expansion";
import { MatIcon } from "@angular/material/icon";
import { BrowserAnimationsModule, provideAnimations } from "@angular/platform-browser/animations";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { FilterPanelComponent } from './panels/filter-panel/filter-panel.component';
import { DetailPanelComponent } from './panels/detail-panel/detail-panel.component';
import { MatCardModule } from "@angular/material/card";
import { LiabilityModalComponent } from './modals/liability-modal/liability-modal.component';
import { LoadingOverlayComponent } from './components/loading-overlay/loading-overlay.component';
import { ToastComponent } from './components/toast/toast.component';
import { PipesModule } from "./core/pipes/pipes.module";
import { DetailPanelModule } from "./panels/detail-panel/detail-panel.module";
import { TimePanelComponent } from "./panels/time-panel/time-panel.component";
import { TimePanelModule } from "./panels/time-panel/time-panel.module";

@NgModule({
  declarations: [
    AppComponent,
    MapComponent,
    TimePanelComponent,
    FilterPanelComponent,
    DetailPanelComponent,
    LiabilityModalComponent,
    LoadingOverlayComponent,
    ToastComponent
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
    MatSlideToggleModule,
    MatCardModule,
    NgOptimizedImage,
    TimePanelModule,
    PipesModule,
    DetailPanelModule
  ],
  providers: [
    provideAnimationsAsync('noop'),
    provideNativeDateAdapter(),
    provideAnimations(),
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
