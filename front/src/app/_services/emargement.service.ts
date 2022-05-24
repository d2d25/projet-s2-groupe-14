import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {AttendanceSheet} from "../_model/attendanceSheet.model";
import {environment} from "../../environments/environment";

const EMARGEMENT_URL = environment.apiUrl + '/emargement';

@Injectable({
  providedIn: 'root'
})
export class EmargementService {

  constructor(private http: HttpClient) {
  }

  getAll(){
    return this.http.get<AttendanceSheet[]>(EMARGEMENT_URL);
  }

  getById(id:string) {
    return this.http.get<AttendanceSheet>(EMARGEMENT_URL + '/' + id);
  }

  create(idTeacher: string, subject: string, beginsAt: Date, endAt: Date, idGroup: string, idSubGroup: string){
    return this.http.post<AttendanceSheet>(EMARGEMENT_URL, {
      idTeacher,
      beginsAt,
      endAt,
      idGroup,
      idSubGroup,
      subject
    });
  }

  createWithoutSubGroup(idTeacher: string, subject: string, beginsAt: Date, endAt: Date, idGroup: string){
    return this.http.post<AttendanceSheet>(EMARGEMENT_URL, {
      idTeacher,
      beginsAt,
      endAt,
      idGroup,
      subject
    });
  }

  edit(id: string, subject: string, emargements: {val: string, key: string}, isValidate: boolean){
    return this.http.patch(EMARGEMENT_URL+"/"+id, {
      id,
      subject,
      emargements,
      isValidate
    });
  }

  delete(id:string) {
    return this.http.delete(EMARGEMENT_URL + '/' + id);
  }

  emarge(id:string, token:string){
    console.log(token)
    return this.http.put(EMARGEMENT_URL+"/"+id+"/emarge", {token})
  }

}
