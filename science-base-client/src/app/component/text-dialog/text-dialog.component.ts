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
  private file: any;
  private form: FormGroup;
  private formFieldsDto = null;
  private formFields = [];
  private processInstance = '';

  constructor(
    private dialogRef: MatDialogRef<TextDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any, private snackBar: SnackBar,
    private textService: TextService) {

    this.initForm(data);
  }

  initForm(data) {
    this.title = data.title;
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
    const o = [];
    Object.keys(value).forEach(
      key => {
        o.push({fieldId: key, fieldValue: key === 'file' ? this.file : value[key]});
      });
    console.log(o);
    this.textService.submitForm(this.formFieldsDto.taskId, o).subscribe(
      res => {
        console.log(res);
        this.dialogRef.close();
        this.snackBar.showSnackBar('Text added!');
      },
      err => {
        this.snackBar.showSnackBar('An error occurred.');
      }
    );
  }

  uploadFile(event) {
    const file = event.target.files[0];
    if (file) {
      console.log(file);
      if (file.size > 131072) {
        alert('Exceeded allowed file size! Maximum size is 128 KB.');
        return;
      }
      const fileReader = new FileReader();
      fileReader.readAsDataURL(file);
      fileReader.onload = () => {
        this.file = fileReader.result;
      };
      // const formData = new FormData();
      // formData.append('file', file);
      // this.file = formData;
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
