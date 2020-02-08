import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material';
import {RegistrationService} from 'src/app/service/registration/registration.service';
import {SnackBar} from 'src/app/utils';
import {LoginService} from 'src/app/service/login/login.service';
import {Router} from '@angular/router';
import {AdminService} from 'src/app/service/admin/admin.service';
import {MagazineDialogComponent} from '../magazine-dialog/magazine-dialog.component';
import {MagazineService} from 'src/app/service/magazine/magazine.service';
import {FormControl, FormGroup} from '@angular/forms';
import {EditorService} from 'src/app/service/editor/editor.service';
import {TextService} from '../../service/text/text.service';
import {TextDialogComponent} from '../text-dialog/text-dialog.component';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit {

  userDto: any;
  userTasks: [];
  nonEnabledReviewers: any;
  nonApprovedMagazines: any;
  magazines: [];
  file: any;
  texts: [];

  constructor(
    private registrationService: RegistrationService, private loginService: LoginService,
    private adminService: AdminService, private editorService: EditorService,
    private magazineService: MagazineService, private textService: TextService,
    private dialog: MatDialog, private snackBar: SnackBar, private router: Router) {
  }

  ngOnInit() {
    this.userDto = this.loginService.getLocalStorageItem();
    this.getUserTasks();
    this.loginService.onSubject.subscribe(
      () => {
        this.userDto = this.loginService.getLocalStorageItem();
        this.getUserTasks();
      }
    );
  }

  getUserTasks() {
    this.magazineService.getAllMagazines().subscribe(
      (data: []) => {
        this.magazines = data;
        console.log(data);
      }
    );
    if (this.userDto) {
      if (this.userDto.role === 'USER') {
        this.getUserTexts();
      } else if (this.userDto.role === 'ADMIN') {
        this.getNonEnabledReviewers();
        this.getNonApprovedMagazines();
      } else if (this.userDto.role === 'EDITOR') {
        this.getMagazineTexts();
        // TODO
        // this.editorService.getMagazines(this.userDto.username).subscribe(
        //   (data: []) => {
        //     this.magazines = data;
        //     console.log(data);
        //   }
        // );
      }
    } else {
      this.userTasks = [];
    }
  }

  // ADMIN
  getNonEnabledReviewers() {
    this.adminService.getNonEnabledReviewers().subscribe(
      (data: []) => {
        console.log(data);
        this.nonEnabledReviewers = data;
      }
    );
  }

  enableReviewer(reviewer) {
    this.adminService.enableReviewer(reviewer.taskId).subscribe(
      () => {
        const index = this.nonEnabledReviewers.indexOf(reviewer);
        this.nonEnabledReviewers.splice(index, 1);
        this.snackBar.showSnackBar('Reviewer \'' + reviewer.username + '\' enabled!');
      },
      () => {
        this.snackBar.showSnackBar('An error occurred.');
      }
    );
  }

  getNonApprovedMagazines() {
    this.adminService.getNonApprovedMagazines().subscribe(
      (data: []) => {
        console.log(data);
        this.nonApprovedMagazines = data;
        data.forEach((magazine: any) => {
          magazine.formFieldsDto.formFields.forEach((field: any) => {
            const formGroup = new FormGroup({});
            formGroup.addControl(field.id, new FormControl(''));
            magazine.formFieldsDto.form = formGroup;
          });
        });
      });
    console.log(this.nonApprovedMagazines);
  }

  approveMagazine(magazine) {
    console.log(magazine);
    const value = magazine.formFieldsDto.form.value;
    const o = [];
    Object.keys(value).forEach(
      key => {
        o.push({fieldId: key, fieldValue: value[key]});
      });
    console.log(o);
    this.adminService.approveMagazine(magazine.formFieldsDto.taskId, o).subscribe(
      () => {
        const index = this.nonApprovedMagazines.indexOf(magazine);
        this.nonApprovedMagazines.splice(index, 1);
        this.snackBar.showSnackBar('Magazine \'' + magazine.name + '\' (not) approved LOL!'
          + 'Do we have correction here ??? '); // ???
      },
      () => {
        this.snackBar.showSnackBar('An error occurred.');
      }
    );
  }

  openNewMagazineDialog() {
    this.magazineService.startProcess(this.userDto.username).subscribe(
      (res: any) => {
        res.title = 'Create new magazine';
        console.log(res);
        const dialogRef = this.dialog.open(MagazineDialogComponent,
          {
            width: '500px',
            disableClose: true,
            autoFocus: true,
            data: res
          });

        dialogRef.afterClosed().subscribe(
          (result) => {
            if (result) {
              result.title = 'Add magazine redaction';
              this.dialog.open(MagazineDialogComponent,
                {
                  width: '500px',
                  disableClose: true,
                  autoFocus: true,
                  data: result
                });
            }
          }
        );
      },
      err => {
        this.snackBar.showSnackBar('An error occurred.');
      });
  }

  openMagazineDialogForCorrection(taskId) {
    this.editorService.getCorrectionTaskForm(taskId).subscribe(
      (res: any) => {
        res.title = 'Update magazine data';
        console.log(res);
        this.dialog.open(MagazineDialogComponent,
          {
            width: '500px',
            disableClose: true,
            autoFocus: true,
            data: res
          });
      });
  }

  getMagazineTexts() {
    this.editorService.getMagazineTexts(this.userDto.username).subscribe(
      (data: []) => {
        this.texts = data;
      }
    );
  }

  getUserTexts() {
    this.textService.getUserTexts(this.userDto.username).subscribe(
      (data: []) => {
        this.texts = data;
      }
    );
  }

  openTextActionDialog(taskDto) {
    this.editorService.getMagazineTextForm(taskDto.taskId).subscribe(
      res => {
        console.log(res);
        const dialogRef = this.dialog.open(TextDialogComponent,
          {
            width: '500px',
            disableClose: true,
            autoFocus: true,
            data: res
          });
        dialogRef.afterClosed().subscribe(
          data => {
            if (data) {
              this.openNextTaskDialog(data);
            }
          }
        );
      });
  }

  openNewTextDialog(magazineName) {
    this.textService.startProcess(magazineName, this.userDto.username).subscribe(
      (res: any) => {
        console.log(res);
        const dialogRef = this.dialog.open(TextDialogComponent,
          {
            width: '500px',
            disableClose: true,
            autoFocus: true,
            data: res
          });
        dialogRef.afterClosed().subscribe(
          data => {
            if (data) {
              this.openNextTaskDialog(data);
            }
          }
        );
      });
  }

  openNextTaskDialog(taskDto) {
    const coauthorDialog = this.dialog.open(TextDialogComponent,
      {
        width: '500px',
        disableClose: true,
        autoFocus: true,
        data: taskDto
      });
    coauthorDialog.afterClosed().subscribe(
      data => {
        if (data) {
          this.openNextTaskDialog(data);
        }
      }
    );
  }

}
