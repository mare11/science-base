import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {MaterialModule} from './material/material.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {SnackBar} from './utils';
import {HomepageComponent} from './component/homepage/homepage.component';
import {VerificationComponent} from './component/verification/verification.component';
import {RegistrationDialogComponent} from './component/registration-dialog/registration-dialog.component';
import {LoginDialogComponent} from './component/login-dialog/login-dialog.component';
import {MagazineDialogComponent} from './component/magazine-dialog/magazine-dialog.component';
import {TextDialogComponent} from './component/text-dialog/text-dialog.component';
import {MagazineOverviewComponent} from './component/magazine-overview/magazine-overview.component';
import {LoginRegistrationDialogComponent} from './component/login-registration-dialog/login-registration-dialog.component';

@NgModule({
  declarations: [
    AppComponent,
    RegistrationDialogComponent,
    LoginDialogComponent,
    VerificationComponent,
    HomepageComponent,
    MagazineDialogComponent,
    TextDialogComponent,
    MagazineOverviewComponent,
    LoginRegistrationDialogComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    MaterialModule,
    SnackBar
  ],
  providers: [],
  bootstrap: [AppComponent],
  entryComponents: [RegistrationDialogComponent, LoginDialogComponent, MagazineDialogComponent, TextDialogComponent, LoginRegistrationDialogComponent]
})
export class AppModule {
}
