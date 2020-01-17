import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class EditorService {

  usersBaseUrl = '/api/editors/';
  formFieldsUrl = 'form/';

  constructor(private http: HttpClient) { }

  getMagazinesForCorrection(username) {
    return this.http.get(this.usersBaseUrl.concat(username));
  }

  getCorrectionTaskForm(username) {
    return this.http.get(this.usersBaseUrl.concat(this.formFieldsUrl).concat(username));
  }
}
