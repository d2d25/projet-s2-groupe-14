import {Role} from "./role";

export class User {


  id: string = "";
  email: string = "";
  firstName: string = "";
  lastName: string = "";
  numEtu: string = "";
  // @ts-ignore
  role: Role;
  token: string = "";

}
