import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TextService {

  textBaseUrl = '/api/text/';

  constructor(private http: HttpClient) {
  }

  startProcess(magazineName, username) {
    const params = new HttpParams()
      .set('username', username);
    return this.http.get(this.textBaseUrl.concat(magazineName), {params});
  }

  getTextFile(fileName) {
    return this.http.get(this.textBaseUrl.concat('download/').concat(fileName), {responseType: 'blob'});
  }

  submitTextFile(formData) {
    return this.http.post(this.textBaseUrl, formData);
  }

  submitForm(taskId, value) {
    return this.http.post(this.textBaseUrl.concat(taskId), value);
  }
}
