import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class EditorService {

  editorsBaseUrl = '/api/editors/';
  formFieldsUrl = 'form/';

  constructor(private http: HttpClient) {
  }

  getMagazines(username) {
    return this.http.get(this.editorsBaseUrl.concat(username));
  }

  getCorrectionTaskForm(username) {
    return this.http.get(this.editorsBaseUrl.concat(this.formFieldsUrl).concat(username));
  }

  getMagazineTexts(username) {
    return this.http.get(this.editorsBaseUrl.concat('texts/').concat(username));
  }

  getMagazineTextForm(taskId) {
    return this.http.get(this.editorsBaseUrl.concat('texts/form/').concat(taskId));
  }

}
