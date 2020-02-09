import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {SnackBar} from '../../utils';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {TextService} from '../../service/text/text.service';

@Component({
  selector: 'app-text-dialog',
  templateUrl: './text-dialog.component.html',
  styleUrls: ['./text-dialog.component.css']
})
export class TextDialogComponent {

  private title: string;
  private textTitle: string;
  private file: any;
  private form: FormGroup;
  private formFieldsDto = null;
  private formFields = [];

  constructor(
    private dialogRef: MatDialogRef<TextDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any, private snackBar: SnackBar,
    private textService: TextService) {

    this.initForm(data);
  }

  initForm(data) {
    this.title = data.taskName;
    this.textTitle = data.textTitle;
    this.form = new FormGroup({});
    this.formFieldsDto = data;
    this.formFields = data.formFields;
    this.formFields.forEach(field => {
      this.form.addControl(field.id, new FormControl({
        value: field.value,
        disabled: this.isDisabled(field)
      }, this.extractValidators(field)));
    });
    console.log(this.form);
  }

  isDisabled(field) {
    if (field.validationConstraints) {
      for (const constraint of field.validationConstraints) {
        if (constraint.name === 'readonly') {
          return true;
        }
      }
    }
    return false;
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

  chooseFile(event) {
    const file = event.target.files[0];
    if (file) {
      console.log(file);
      if (file.size > 2097152) {
        alert('Exceeded allowed file size! Maximum size is 128 KB.');
        return;
      }
      this.file = file;
    } else {
      this.file = null;
    }
  }

  submitForm() {
    const value = this.form.value;
    console.log(value);
    const o = [];
    if (!this.allControlsAreDisabled()) {
      Object.keys(value).forEach(
        key => {
          o.push({fieldId: key, fieldValue: key === 'file' ? this.getFileName().concat('.pdf') : value[key]});
        });
    }
    console.log(o);
    if (value.file) {
      this.uploadFile().subscribe(
        () => {
          console.log('Upload successful!');
          this.sendFormData(o);
        },
        () => {
          console.log('Upload failed!');
        });
    } else {
      this.sendFormData(o);
    }

  }

  allControlsAreDisabled() {
    const controls = this.form.controls;
    for (const control of Object.keys(controls)) {
      if (controls[control].enabled) {
        return false;
      }
    }
    return true;
  }

  getFileName() {
    const formTitle = this.form.value.title;
    return formTitle ? formTitle : this.textTitle;
  }

  uploadFile() {
    const textTitle = this.getFileName();
    const formData = new FormData();
    formData.append('file', this.file);
    // @ts-ignore
    formData.append('overwrite', this.textTitle != null);
    return this.textService.uploadTextFile(textTitle, formData);
  }

  sendFormData(data) {
    this.textService.submitForm(this.formFieldsDto.taskId, data).subscribe(
      res => {
        console.log(res);
        this.snackBar.showSnackBar('Data saved!');
        this.dialogRef.close(res);
      },
      err => {
        this.snackBar.showSnackBar('An error occurred.');
      }
    );
  }

  downloadFile() {
    this.textService.downloadTextFile(this.data.taskId).subscribe(
      (data) => {
        console.log(data);
        const url = window.URL.createObjectURL(data);
        const link = document.createElement('a');
        link.href = url;
        link.download = this.textTitle.concat('.pdf');
        link.click();
        window.URL.revokeObjectURL(url);
      },
      (error) => {
        console.log('Download error!');
      }
    );
  }

}
