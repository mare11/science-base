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

  uploadTextFile(title, formData) {
    return this.http.post(this.textBaseUrl.concat('upload/').concat(title), formData);
  }

  downloadTextFile(title) {
    return this.http.get(this.textBaseUrl.concat('download/').concat(title), {responseType: 'blob'});
  }

  submitForm(taskId, value) {
    return this.http.post(this.textBaseUrl.concat(taskId), value);
  }

  getUserTextsWithActiveTask(username) {
    return this.http.get(this.textBaseUrl.concat('user/').concat(username));
  }

  getMagazineTexts(magazineName) {
    return this.http.get(this.textBaseUrl.concat('magazine/').concat(magazineName));
  }
}
