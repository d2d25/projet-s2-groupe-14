import { Component, OnInit } from '@angular/core';
import {EmargementService} from "../../../_services/emargement.service";
import {AttendanceSheet} from "../../../_model/attendanceSheet.model";

@Component({
  selector: 'app-emargement-list',
  templateUrl: './emargement-list.component.html',
  styleUrls: ['./emargement-list.component.css']
})
export class EmargementListComponent implements OnInit {

  emargements : AttendanceSheet[] = [];

  constructor(private emargementService:EmargementService) { }

  ngOnInit(): void {
    this.emargementService.getAll().subscribe( response => {
      this.emargements = response;
      this.emargements.sort((a:AttendanceSheet,b:AttendanceSheet)=>(a.beginsAt<b.beginsAt ? -1:1))

    });
  }

  deleteEmargement(id: string) {
    if(confirm("Voulez vous vraiment supprimer cette emargement ?")) {
      this.emargementService.delete(id).subscribe(() => {
        this.emargements = this.emargements.filter(s => {
          return s.id != id;
        });
      });
    }
  }
}
