import {User} from "./user.model";
import {SubGroup} from "./subGroup.model";

export class Group {

  id: string = "";
  name: string = "";
  students: User[] = [];
  subGroups: SubGroup[] = [];

}
