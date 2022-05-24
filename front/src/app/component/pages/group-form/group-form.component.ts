import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../../../_services/user.service";
import {User} from "../../../_model/user.model";
import {Group} from "../../../_model/group.model";
import {GroupService} from "../../../_services/group.service";

interface CheckedStudents{
  student:User;
  checked:boolean;
}


@Component({
  selector: 'app-group-form',
  templateUrl: './group-form.component.html',
  styleUrls: ['./group-form.component.css']
})
export class GroupFormComponent implements OnInit {


  isMainGroup: boolean = false;
  isCreateMode: boolean = true;
  idMain: string = "";
  idSub: string = "";
  form: any = {
    name: null,
  };
  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = "";
  // @ts-ignore
  idSelectedGroup:string;
  promotions: Group[] = [];
  newGroup: string = "new"
  checkedStudents : CheckedStudents[] = [];

  constructor(private groupService: GroupService,
              private route: ActivatedRoute,
              private router: Router,
              private userService: UserService,) {}

  ngOnInit(): void {
    this.idMain = this.route.snapshot.params['idMain'];
    this.idSub = this.route.snapshot.params['idSub'];
    this.isCreateMode = !this.idMain;
    this.isMainGroup = !this.idSub

    if (this.isCreateMode) { //Je crée un nouveau groupe
      this.groupService.getAll().subscribe(data => {
        this.promotions = data;
        this.promotions.sort((a:Group,b:Group)=>(a.name<b.name ? -1:1))
      });
    } else { //Je modifie un  groupe
      if (this.isMainGroup) { //C'est une Promotion
        this.initMainGroup()
      } else { //C'est un sous groupe
        this.initSubGroup()
      }
    }
  }

  initMainGroup() {
    this.groupService.getMainById(this.idMain).subscribe({ //appel de tout les etudiants dans la promotion
      next: mainData => {
        this.form.name = mainData.name
        this.userService.getAllByRole("ROLE_STUDENT").subscribe({  //appel de tout les etudiants de la bdd
          next: allDatas => {
            for (let dataStudent of allDatas) {
              if (mainData.students.filter( s => s.id === dataStudent.id).length>0) { //checked = true pour les étudiants déjà dans la promotion
                this.checkedStudents.push({
                  student: dataStudent,
                  checked: true
                })
              } else {
                this.checkedStudents.push({ //checked = false pour le reste
                  student: dataStudent,
                  checked: false
                })
              }
            }
            this.checkedStudents.sort((a:CheckedStudents,b:CheckedStudents)=>(a.student.lastName<b.student.lastName ? -1:1)).sort((a:CheckedStudents,b:CheckedStudents)=>(a.checked>=b.checked ? -1:1))
          }})
      },
      error: err => {
        this.errorMessage = err.error.message;
      }
    });
  }

  initSubGroup() {
    this.groupService.getSubById(this.idMain, this.idSub).subscribe({ //appel de tout les etudiants du sous Groupe
      next: subData => {
        this.form.name = subData.name
        this.groupService.getMainById(this.idMain).subscribe({ //appel de tout les etudiants de la promotion du sous groupe
          next: mainData => {
            for (let dataStudent of mainData.students) {
              if (subData.students.filter( s => s.id === dataStudent.id).length>0) { //checked = true pour les étudiants déjà dans le sous groupe
                this.checkedStudents.push({
                  student: dataStudent,
                  checked: true
                })
              } else {
                this.checkedStudents.push({ //checked = false pour le reste
                  student: dataStudent,
                  checked: false
                })
              }
            }
            this.checkedStudents.sort((a:CheckedStudents,b:CheckedStudents)=>(a.student.lastName<b.student.lastName ? -1:1)).sort((a:CheckedStudents,b:CheckedStudents)=>(a.checked>=b.checked ? -1:1))
          }})
      },
      error: err => {
        this.errorMessage = err.error.message;
      }
    });
  }

  getIdStudentsChecked() : string[] {
    let idStudentsChecked : string[] = [];

    this.checkedStudents.forEach(checkedStudent => {
      if (checkedStudent.checked) {
        idStudentsChecked.push(checkedStudent.student.id)
      }
    })
    return idStudentsChecked
  }

  get f() { return this.form.controls; }

  updateStudentChecked(){
    this.checkedStudents = []
    if (this.idSelectedGroup==this.newGroup){
      this.isMainGroup = true;
      this.userService.getAllByRole("ROLE_STUDENT").subscribe((response)=> {
        for (let student of response) {
          this.checkedStudents.push({
            student: student,
            checked: false
          })
        }
        this.checkedStudents.sort((a:CheckedStudents,b:CheckedStudents)=>(a.student.lastName<b.student.lastName ? -1:1))
      })
    } else {
      this.groupService.getMainById(this.idSelectedGroup).subscribe((response)=>{
        this.isMainGroup = false;
        for (let student of response.students) {
          this.checkedStudents.push({
            student: student,
            checked: false
          })
        }
        this.checkedStudents.sort((a:CheckedStudents,b:CheckedStudents)=>(a.student.lastName<b.student.lastName ? -1:1)).sort((a:CheckedStudents,b:CheckedStudents)=>(a.checked>=b.checked ? -1:1))
      })
    }
  }


  onSubmit(): void {
    if (this.isCreateMode){ //je crée un nouveau groupe
      if (this.isMainGroup) { //je crée une nouvelle promotion
        this.createMainGroup();
      } else { //je crée un nouveau sous groupe
        this.createSubGroup();
      }
    } else { //Je modifier un groupe
      if (this.isMainGroup) { // je modifie une promotion
        this.editMainGroup();
      } else { // je modifie un sous groupe
        this.editSubGroup();
      }
    }

  }

  private editSubGroup() {
    this.groupService.editSubGroup(this.idMain, this.idSub, this.form.name, this.getIdStudentsChecked()).subscribe({
      next: () => {
        this.idSelectedGroup = this.idMain
        this.isSuccessful = true;
        this.isSignUpFailed = false;
        this.router.navigate(['group-list/' + this.idSelectedGroup]);
      },
      error: err => {
        this.errorMessage = err.message;
        this.isSignUpFailed = true;
      }
    });
  }

  private editMainGroup() {
    this.groupService.editMainGroup(this.idMain, this.form.name, this.getIdStudentsChecked()).subscribe({
      next: () => {
        this.isSuccessful = true;
        this.isSignUpFailed = false;
        this.router.navigate(['group-list']);
      },
      error: err => {
        this.errorMessage = err.message;
        this.isSignUpFailed = true;
      }
    });
  }

  private createSubGroup() {
    this.groupService.createSubGroup(this.idSelectedGroup, this.form.name, this.getIdStudentsChecked()).subscribe({
      next: () => {
        this.isSuccessful = true;
        this.isSignUpFailed = false;
        this.router.navigate(['group-list/' + this.idSelectedGroup]);
      },
      error: err => {
        this.errorMessage = err.message;
        this.isSignUpFailed = true;
        alert(err.message)

      }
    });
  }

  private createMainGroup() {
    this.groupService.createMainGroup(this.form.name, this.getIdStudentsChecked()).subscribe({
      next: () => {
        this.isSuccessful = true;
        this.isSignUpFailed = false;
        this.router.navigate(['group-list']);
      },
      error: err => {
        this.errorMessage = err.message;
        this.isSignUpFailed = true;
      }
    });
  }
}
