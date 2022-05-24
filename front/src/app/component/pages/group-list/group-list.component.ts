import { Component, OnInit } from '@angular/core';
import {GroupService} from "../../../_services/group.service";
import {ActivatedRoute} from "@angular/router";
import {Group} from "../../../_model/group.model";
import {SubGroup} from "../../../_model/subGroup.model";

@Component({
  selector: 'app-group-list',
  templateUrl: './group-list.component.html',
  styleUrls: ['./group-list.component.css']
})
export class GroupListComponent implements OnInit {

  showMainGroups:boolean = true; //determine si on est dans la liste des promotions, si false, alors je suis dans la liste de sousgroupes d'une promotion
  idMain:string = "";
  groups: Group[] = [];
  subGroups: SubGroup[] = [];
  name:string = "";

  constructor(
    private groupService:GroupService,
    private route:ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.idMain = this.route.snapshot.params['idMain'];
    this.showMainGroups = !this.idMain;

    if (this.showMainGroups){ //je vois une liste de promotions
      this.groupService.getAll().subscribe( response => {
        this.groups = response;
        this.groups.sort((a:Group,b:Group)=>(a.name<b.name ? -1:1))
      });
    } else { //je vois la liste des sous groupes d'une promotion
      this.groupService.getMainById(this.idMain).subscribe( response => {
        this.subGroups = response.subGroups;
        this.name = response.name
      });
    }

  }


  deleteGroup(id: string) {

    if(confirm("Voulez vous vraiment supprimer cette promotion ?")) {
      this.groupService.deleteGroup(id).subscribe(() => {
        this.groups = this.groups.filter(s => {
          return s.id != id;
        });
      });
    }
  }

  deleteSubGroup(idMain: string, idSub:string) {
    if(confirm("Voulez vous vraiment supprimer ce groupe ?")) {
      this.groupService.deleteSubGroup(idMain, idSub).subscribe(() => {
        this.groups = this.groups.filter(s => {
          return s.id != idSub;
        });
      });
    }
  }
}
