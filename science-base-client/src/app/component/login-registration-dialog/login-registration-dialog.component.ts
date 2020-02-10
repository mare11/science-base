import {Component} from '@angular/core';
import {MatDialog, MatDialogRef} from '@angular/material';
import {LoginDialogComponent} from '../login-dialog/login-dialog.component';
import {RegistrationDialogComponent} from '../registration-dialog/registration-dialog.component';
import {SnackBar} from '../../utils';
import {RegistrationService} from '../../service/registration/registration.service';
import {LoginService} from '../../service/login/login.service';

@Component({
  selector: 'app-login-registration-dialog',
  templateUrl: './login-registration-dialog.component.html',
  styleUrls: ['./login-registration-dialog.component.css']
})
export class LoginRegistrationDialogComponent {

  constructor(private dialogRef: MatDialogRef<LoginRegistrationDialogComponent>,
              private loginService: LoginService, private registrationService: RegistrationService,
              private dialog: MatDialog, private snackBar: SnackBar) {
  }

  openLoginDialog() {
    const dialogRef = this.dialog.open(LoginDialogComponent,
      {
        width: '500px',
        disableClose: true,
        autoFocus: true,
      });
    this.dialogRef.close();
    dialogRef.afterClosed().subscribe(
      (data) => {
        if (data) {
          this.loginService.setLocalStorageItem(data);
          this.snackBar.showSnackBar('Logged in successfully!');
        }
      }
    );
  }

  openRegistrationDialog() {
    this.registrationService.startProcess().subscribe(
      (res: any) => {
        console.log(res);
        this.dialog.open(RegistrationDialogComponent,
          {
            width: '500px',
            disableClose: true,
            autoFocus: true,
            data: res
          });
        this.dialogRef.close();
      },
      err => {
        this.snackBar.showSnackBar('An error occurred.');
      });
  }
}
