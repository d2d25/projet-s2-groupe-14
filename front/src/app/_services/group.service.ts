import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {Group} from "../_model/group.model";
import {SubGroup} from "../_model/subGroup.model";

const GROUP_URL = environment.apiUrl + '/group';


@Injectable({
  providedIn: 'root'
})
export class GroupService {

  constructor(private http:HttpClient) { }

  getAll(){
    return this.http.get<Group[]>(GROUP_URL);
  }

  getMainById(id:string) {
    return this.http.get<Group>(GROUP_URL + '/' + id);
  }

  getSubById(idMain:string, idSub:string) {
    return this.http.get<SubGroup>(GROUP_URL + '/' + idMain + '/' + idSub);
  }

  deleteGroup(id:string){
    return this.http.delete(GROUP_URL + '/' + id);
  }
  deleteSubGroup(idMain:string, idSub:string){
    return this.http.delete(GROUP_URL + '/' + idMain + '/' + idSub);
  }

  createMainGroup(name:string, students: string[]) : Observable<any> {
    return this.http.post(GROUP_URL, {
      name,
      students
    });
  }

  createSubGroup(id:string, name:string, students: string[]) : Observable<any> {
    return this.http.post(GROUP_URL + '/' + id, {
      name,
      students
    });
  }

  editMainGroup(id:string, name:string, students: string[]) : Observable<any> {
    return this.http.patch(GROUP_URL+'/'+id, {
      name,
      students,
    });
  }

  editSubGroup(idMain:string, id:string, name:string, students: string[]) : Observable<any> {
    console.log(idMain)
    console.log(id)
    console.log(name)
    console.log(students)

    return this.http.patch(GROUP_URL+'/'+idMain +'/'+id, {
      name,
      students
    });
  }

}
