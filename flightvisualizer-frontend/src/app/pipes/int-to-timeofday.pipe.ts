import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'intToTimeofday'
})
export class IntToTimeofdayPipe implements PipeTransform {

  transform(value: number | null | undefined): string {
    if(!value) return '';
    let hours = Math.floor((value)/60);
    let minutes = Math.floor((value)%60);

    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`;
  }

}
