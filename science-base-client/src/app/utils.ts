import { MatSnackBar } from '@angular/material';
import { NgModule } from '@angular/core';

@NgModule()
export class SnackBar {

    constructor(private snackBar: MatSnackBar) { }

    showSnackBar(message: string) {
        if (!message) {
            return;
        }
        const snackBarRef = this.snackBar.open(message, 'Dissmiss', { duration: 4000 });
        snackBarRef.onAction().subscribe(
            () => {
                snackBarRef.dismiss();
            }
        );
    }
}
