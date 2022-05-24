import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {User} from "../../../_model/user.model";
import {UserService} from "../../../_services/user.service";
import {Group} from "../../../_model/group.model";
import {GroupService} from "../../../_services/group.service";
import {EmargementService} from "../../../_services/emargement.service";
import {SubGroup} from "../../../_model/subGroup.model";

@Component({
  selector: 'app-emargement-register',
  templateUrl: './emargement-register.component.html',
  styleUrls: ['./emargement-register.component.css']
})
export class EmargementRegisterComponent implements OnInit {

  isAdmin: boolean = false;
  isSubmitted: boolean = false;
  teachers: User[] = [];
  groups: Group[] = [];
  // @ts-ignore
  user: User;
  form: any = {
    subject: null
  };
  // @ts-ignore
  idSelectedGroup: string;
  // @ts-ignore
  idSelectedSubGroup: string | null = null;
  // @ts-ignore
  idSelectedTeacher: string;


  isSuccessful = false;
  errorMessage = "";

  constructor(private groupeService: GroupService,
              private route: ActivatedRoute,
              private router: Router,
              private userService: UserService,
              private emargementService: EmargementService) { }

  ngOnInit(): void {
    this.groupeService.getAll().subscribe( {
      next: groups => {
        this.groups = groups;
        this.groups.sort((a:Group,b:Group)=>(a.name<b.name ? -1:1))
     }
    });

    this.userService.current().subscribe( {
      next: currentUser => {
        this.user = currentUser;
        if (this.user.role == "ROLE_ADMIN") {
          this.isAdmin = true;
          this.userService.getAllByRole("ROLE_TEACHER").subscribe( {
            next: teachers => {
              this.teachers = teachers;
            }
          })
        } else {
          this.idSelectedTeacher = this.user.id;
        }
      }
    })

  }


  onSubmit(): void {
      this.createAttendanceSheet();
      this.isSubmitted = true;
  }

  private createAttendanceSheet() {
    if (this.idSelectedSubGroup) {
      // @ts-ignore
      this.emargementService.create(this.idSelectedTeacher, this.form.subject, null, null, this.idSelectedGroup, this.idSelectedSubGroup).subscribe({
        next: attendanceSheet => {
          this.isSuccessful = true;
          this.router.navigate(['emargement/' + attendanceSheet.id]);
        },
        error: err => {
          this.errorMessage = err.message;
        }
      });
    } else {
      // @ts-ignore
      this.emargementService.createWithoutSubGroup(this.idSelectedTeacher, this.form.subject, null, null, this.idSelectedGroup).subscribe({
        next: attendanceSheet => {
          this.isSuccessful = true;
          this.router.navigate(['emargement/' + attendanceSheet.id]);
        },
        error: err => {
          this.errorMessage = err.message;
        }
      });
    }
  }

  get f() {
    return this.form.controls;
  }


  // @ts-ignore
  getSubGroupByGroupId(id: string): SubGroup[] {
    for (let g of this.groups) {
      if (g.id == id) {
        return g.subGroups;
      }
    }
  }
}
