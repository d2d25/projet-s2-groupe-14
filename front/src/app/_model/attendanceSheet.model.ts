import {User} from "./user.model";
import {Emargement} from "./emargement.model";

export class AttendanceSheet {

  id: string = "";
  // @ts-ignore
  teacher: User;
  subject: string = "";
  // @ts-ignore
  beginsAt: Date;
  // @ts-ignore
  endAt: Date;
  idGroup: string = "";
  idSubGroup: string = "";
  nameGroup:string = "";
  nameSubGroup:string = "";
  // @ts-ignore
  emargements: Emargement[] = [];
  isValidate: boolean = false;
  token: string = "";

}
