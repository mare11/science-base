import {Component, OnInit} from '@angular/core';
import {LoginService} from './service/login/login.service';
import {RegistrationDialogComponent} from './component/registration-dialog/registration-dialog.component';
import {LoginDialogComponent} from './component/login-dialog/login-dialog.component';
import {RegistrationService} from './service/registration/registration.service';
import {MatDialog} from '@angular/material';
import {SnackBar} from './utils';
import {Router} from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  userDto: any;

  constructor(private loginService: LoginService, private registrationService: RegistrationService,
              private dialog: MatDialog, private snackBar: SnackBar, private router: Router) {
  }

  ngOnInit() {
    this.userDto = this.loginService.getLocalStorageItem();
    this.loginService.onSubject.subscribe(
      () => {
        this.userDto = this.loginService.getLocalStorageItem();
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

      },
      err => {
        this.snackBar.showSnackBar('An error occurred.');
      });
  }

  openLoginDialog() {
    const dialogRef = this.dialog.open(LoginDialogComponent,
      {
        width: '500px',
        disableClose: true,
        autoFocus: true,
      });

    dialogRef.afterClosed().subscribe(
      (data) => {
        if (data) {
          this.loginService.setLocalStorageItem(data);
          this.snackBar.showSnackBar('Logged in successfully!');
        }
      }
    );
  }

  logoutUser() {
    setTimeout(() => {
      this.loginService.removeLocalStorageItem();
      this.router.navigate(['/']);
      this.snackBar.showSnackBar('Logged out successfully!');
    }, 500);
  }
}
