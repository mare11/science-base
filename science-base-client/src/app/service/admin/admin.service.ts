import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  adminBaseUrl = '/api/admin/';
  reviewersUrl = 'reviewers/';
  magazinesUrl = 'magazines/';

  constructor(private http: HttpClient) { }

  getNonEnabledReviewers() {
    return this.http.get(this.adminBaseUrl.concat(this.reviewersUrl));
  }

  enableReviewer(taskId) {
    return this.http.put(this.adminBaseUrl.concat(this.reviewersUrl).concat(taskId), null);
  }

  getNonApprovedMagazines() {
    return this.http.get(this.adminBaseUrl.concat(this.magazinesUrl));
  }

  approveMagazine(taskId, value) {
    return this.http.put(this.adminBaseUrl.concat(this.magazinesUrl).concat(taskId), value);
  }
}
