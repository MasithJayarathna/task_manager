import { Injectable } from '@angular/core';
import {MatSnackBar, MatSnackBarConfig} from "@angular/material/snack-bar";

@Injectable({
  providedIn: 'root'
})
export class SnackbarService {

  constructor(private snakBar: MatSnackBar) { }


  showMessage(message: string ,  type: 'success' | 'error', duration:number = 3000){
    const config: MatSnackBarConfig = {
      duration,
      panelClass: type === 'success'? 'success-snackbar': 'error-snackbar',
      horizontalPosition: 'right',
      verticalPosition: 'top'
    };
    this.snakBar.open(message, 'X', config);
  }
}
