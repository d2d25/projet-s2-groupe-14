import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {EmargementService} from "../../../_services/emargement.service";

@Component({
  selector: 'app-qr-code',
  templateUrl: './qr-code.component.html',
  styleUrls: ['./qr-code.component.css']
})
export class QrCodeComponent implements OnInit {

  id:string = "";
  isSuccessful = false;
  isEditFailed = false;
  errorMessage = "";
  // @ts-ignore
  public values: string;
  idEmargement: string = "";
  token: string = "";

  public level: "L" | "M" | "Q" | "H";
  public width: number;
  // @ts-ignore
  private mapValues: { idEmargement: string; token: string };

  showData: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private emargementService:EmargementService,
  ) {
    this.level = "L";
    this.width = 300;
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];
    this.emargementService.getById(this.id).subscribe(data=>{
      this.token = data.token
      this.idEmargement= this.id
      this.mapValues = {"idEmargement":this.idEmargement, "token":this.token}
      this.values = JSON.stringify(this.mapValues)
    })
  }

  onSubmit() {
    return false;
  }

  switchShowData() {
    this.showData = !this.showData
  }
}
