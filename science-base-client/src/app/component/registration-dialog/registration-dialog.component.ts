import { Component, Inject } from '@angular/core';
import { MatDialogRef, MatSnackBar, MAT_DIALOG_DATA } from '@angular/material';
import { FormControl, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { RegistrationService } from 'src/app/service/registration/registration.service';
import { SnackBar } from 'src/app/utils';

@Component({
  selector: 'app-registration-dialog',
  templateUrl: './registration-dialog.component.html',
  styleUrls: ['./registration-dialog.component.css']
})
export class RegistrationDialogComponent {

  private form: FormGroup;
  private formFieldsDto = null;
  private formFields = [];
  private processInstance = '';

  constructor(
    private dialogRef: MatDialogRef<RegistrationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any, private snackBar: SnackBar,
    private registrationService: RegistrationService) {

    this.form = new FormGroup({});
    this.formFieldsDto = data;
    this.formFields = data.formFields;
    this.processInstance = data.processInstanceId;
    this.formFields.forEach((field) => {
      this.form.addControl(field.id, new FormControl('', this.extractValidators(field)));
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
          validators.push(Validators.maxLength(constraint.configuration));
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
    this.registrationService.submitForm(this.formFieldsDto.taskId, o).subscribe(
      res => {
        this.snackBar.showSnackBar('Registration successful! Please check your email for confirmation link.');
        this.dialogRef.close();
      },
      err => {
        this.snackBar.showSnackBar('An error occurred.');
      }
    );
  }

}
