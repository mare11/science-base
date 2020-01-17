import { Component, Inject, ViewChild } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatStepper, MatDialog } from '@angular/material';
import { SnackBar } from 'src/app/utils';
import { MagazineService } from 'src/app/service/magazine/magazine.service';

@Component({
  selector: 'app-magazine-dialog',
  templateUrl: './magazine-dialog.component.html',
  styleUrls: ['./magazine-dialog.component.css']
})
export class MagazineDialogComponent {

  private form: FormGroup;
  private formFieldsDto = null;
  private formFields = [];
  private processInstance = '';

  constructor(
    private dialogRef: MatDialogRef<MagazineDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any, private snackBar: SnackBar,
    private magazineService: MagazineService) {

    this.initForm(data);
  }

  initForm(data) {
    this.form = new FormGroup({});
    this.formFieldsDto = data;
    this.formFields = data.formFields;
    this.processInstance = data.processInstanceId;
    this.formFields.forEach(field => {
      this.form.addControl(field.id, new FormControl(field.value, this.extractValidators(field)));
    });
    console.log(this.form);
  }

  extractValidators(field) {
    const validators = [];
    if (field.validationConstraints) {
      field.validationConstraints.forEach(constraint => {
        if (field.type.name === 'email') {
          validators.push(Validators.email);
        }
        if (constraint.name === 'required') {
          validators.push(Validators.required);
        }
        if (constraint.name === 'minlength') {
          validators.push(Validators.minLength(constraint.configuration));
        }
        if (constraint.name === 'maxlength') {
          validators.push(Validators.maxLength(constraint.configuration - 1));
        }
      });
    }
    return validators;
  }

  submitForm() {
    const value = this.form.value;
    console.log(value);
    const o = new Array();
    Object.keys(value).forEach(
      key => {
        o.push({ fieldId: key, fieldValue: value[key] });
      });

    console.log(o);
    this.magazineService.submitForm(this.formFieldsDto.taskId, o).subscribe(
      res => {
        console.log(res);
        if (res) {
          this.dialogRef.close(res);
          this.snackBar.showSnackBar('New magazine created! Please add redaction.');
        } else {
          this.dialogRef.close();
          this.snackBar.showSnackBar('Redaction added!');
        }
      },
      err => {
        this.snackBar.showSnackBar('An error occurred.');
      }
    );
  }

}
