import { Component, OnInit } from '@angular/core';
import {User} from "../../../_model/user.model";
import {UserService} from "../../../_services/user.service";

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {

  users : User[] = new Array<User>();
  constructor(private userService: UserService,) { }

  ngOnInit(): void {
    this.userService.getAll().subscribe((response: User[]) => {
      this.users = response;
      this.users.sort((a:User,b:User)=>(a.lastName<b.lastName ? -1:1)).sort((a:User,b:User)=>(a.role<b.role ? -1:1))
    });
  }

  deleteUser(id:string) {
      if(confirm("Voulez vous vraiment supprimer l'utilisateur ?")) {
        this.userService.deleteById(id).subscribe((response) => {
          console.log(response);
          this.users = this.users.filter(s => {
            return s.id != id;
          });
        });
      }
  }

}
