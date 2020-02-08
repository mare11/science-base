import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material';
import {RegistrationService} from 'src/app/service/registration/registration.service';
import {RegistrationDialogComponent} from '../registration-dialog/registration-dialog.component';
import {SnackBar} from 'src/app/utils';
import {LoginService} from 'src/app/service/login/login.service';
import {LoginDialogComponent} from '../login-dialog/login-dialog.component';
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

  getUserTasks() {
    this.magazineService.getAllMagazines().subscribe(
      (data: []) => {
        this.magazines = data;
        console.log(data);
      }
    );
    if (this.userDto) {
      if (this.userDto.role === 'ADMIN') {
        this.getNonEnabledReviewers();
        this.getNonApprovedMagazines();
      } else if (this.userDto.role === 'EDITOR') {
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

  openNewTextDialog(magazineName) {
    this.textService.startProcess(magazineName, this.userDto.username).subscribe(
      (res: any) => {
        res.title = 'Add new text';
        console.log(res);
        this.dialog.open(TextDialogComponent,
          {
            width: '500px',
            disableClose: true,
            autoFocus: true,
            data: res
          });
      });
  }

  uploadFile(event) {
    const file = event.target.files[0];
    if (file) {
      console.log(file);
      if (file.size > 131072) {
        alert('Exceeded allowed file size! Maximum size is 128 KB.');
        return;
      }
      // const fileReader = new FileReader();
      // fileReader.readAsDataURL(file);
      // fileReader.onload = () => {
      //   console.log(fileReader.result);
      // };
      this.file = file;
      const formData = new FormData();
      formData.append('file', file);
      // this.textService.submitTextFile(formData).subscribe(
      //   () => {
      //     console.log('Upload successful!');
      //   },
      //   () => {
      //     console.log('Upload failed!');
      //   }
      // );
    }
  }

  downloadFile() {
    const fileName = 'help.xml';
    this.textService.getTextFile(fileName).subscribe(
      (data) => {
        console.log(data);
        this.saveFile(data, fileName);
      },
      (error) => {
        console.log('Download error!');
      }
    );
  }

  saveFile(blob, fileName) {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName;
    link.click();
    window.URL.revokeObjectURL(url);
  }

}
