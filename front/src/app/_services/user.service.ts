import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {User} from "../_model/user.model";
import {Observable} from "rxjs";

const USER_URL = environment.apiUrl + '/user';


@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http:HttpClient) { }

  getAll(){
    return this.http.get<User[]>(USER_URL);
  }

  getAllByRole(role:string){
    let params = new HttpParams().set("role",role); //Create new HttpParams
    return this.http.get<User[]>(USER_URL+'/byRole', {params})
  }

  getById(id:string) {
    //const headers = new HttpHeaders();
    //let user = this.tokenStorage.getUser();
    //headers.set('Authorization', user?.tokenType+' '+user?.token);
    return this.http.get<User>(USER_URL + '/' + id);
  }

  deleteById(id:string){
    return this.http.delete(USER_URL + '/' + id);
  }

  edit(id:string, email:string, firstName:string, lastName:string, role:string,  numEtu:string) : Observable<any> {
    return this.http.patch(USER_URL+'/'+id, {
      email,
      firstName,
      lastName,
      role,
      numEtu
    });
  }

  current() {
    return this.http.get<User>(USER_URL + '/current');
  }

  editPassword(password:string) : Observable<any> {
    return this.http.patch(USER_URL+'/updatePassword', { password });
  }
}
