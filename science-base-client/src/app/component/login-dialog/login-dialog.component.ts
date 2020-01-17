import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { LoginService } from 'src/app/service/login/login.service';
import { MatDialogRef } from '@angular/material';

@Component({
  selector: 'app-login-dialog',
  templateUrl: './login-dialog.component.html',
  styleUrls: ['./login-dialog.component.css']
})
export class LoginDialogComponent implements OnInit, OnDestroy {

  loginForm: FormGroup;
  enterListener: any;
  loggingInProgress = false;

  constructor(
    private loginService: LoginService,
    private dialogRef: MatDialogRef<LoginDialogComponent>,
    private formBuilder: FormBuilder
  ) {
    this.enterListener = (event) => {
      if (event.keyCode === 13) {
        this.loginUser();
      }
    };
  }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      username: [],
      password: []
    });
    document.addEventListener('keyup', this.enterListener);
  }

  ngOnDestroy() {
    document.removeEventListener('keyup', this.enterListener);
  }

  loginUser() {
    this.loggingInProgress = true;
    const credentials = this.loginForm.value;
    this.loginService.loginUser(credentials).subscribe(
      data => {
        this.loggingInProgress = false;
        this.dialogRef.close(data);
      },
      () => {
        this.loginForm.get('username').setErrors({ loginFailed: true });
        this.loginForm.get('password').setErrors({ loginFailed: true });
        this.loggingInProgress = false;
      }
    );
  }

}
