import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {TextService} from '../../service/text/text.service';
import {TextDialogComponent} from '../text-dialog/text-dialog.component';
import {LoginService} from '../../service/login/login.service';
import {MatDialog} from '@angular/material';
import {SnackBar} from '../../utils';
import {LoginRegistrationDialogComponent} from '../login-registration-dialog/login-registration-dialog.component';

@Component({
  selector: 'app-magazine-overview',
  templateUrl: './magazine-overview.component.html',
  styleUrls: ['./magazine-overview.component.css']
})
export class MagazineOverviewComponent implements OnInit, OnDestroy {

  sub: any;
  magazineName: string;
  texts: [];
  userDto: any;

  constructor(private activatedRoute: ActivatedRoute, private textService: TextService, private loginService: LoginService,
              private dialog: MatDialog, private snackBar: SnackBar) {
  }

  ngOnInit() {
    this.sub = this.activatedRoute.params.subscribe(params => {
      this.magazineName = params.name;
      console.log('magazine name: ' + this.magazineName);
      this.textService.getMagazineTexts(this.magazineName).subscribe(
        (data: []) => {
          this.texts = data;
          this.userDto = this.loginService.getLocalStorageItem();
          this.loginService.onSubject.subscribe(
            () => {
              this.userDto = this.loginService.getLocalStorageItem();
            }
          );
        },
        error => {
          this.snackBar.showSnackBar('Error loading magazine info!');
        }
      );
    });
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  openNewTextDialog() {
    if (!this.userDto) {
      this.dialog.open(LoginRegistrationDialogComponent,
        {
          width: '500px',
          disableClose: true,
          autoFocus: true,
        });
    } else {
      this.textService.startProcess(this.magazineName, this.userDto.username).subscribe(
        (res: any) => {
          console.log(res);
          this.dialog.open(TextDialogComponent,
            {
              width: '500px',
              disableClose: true,
              autoFocus: true,
              data: res
            });
        },
        error => {
          this.snackBar.showSnackBar('An error occurred.');
        });
    }
  }

  downloadFile(title) {
    this.textService.downloadTextFile(title).subscribe(
      (data) => {
        console.log(data);
        const url = window.URL.createObjectURL(data);
        const link = document.createElement('a');
        link.href = url;
        link.download = title.concat('.pdf');
        link.click();
        window.URL.revokeObjectURL(url);
      },
      (error) => {
        console.log('Download error!');
      }
    );
  }

}
