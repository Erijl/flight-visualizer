import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {IntToTimeofdayPipe} from "./int-to-timeofday.pipe";



@NgModule({
  declarations: [
    IntToTimeofdayPipe,
  ],
  imports: [
    CommonModule
  ],
  exports: [
    IntToTimeofdayPipe
  ]
})
export class PipesModule { }
