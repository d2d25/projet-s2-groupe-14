import {Component, OnInit, ViewEncapsulation, ChangeDetectorRef } from '@angular/core';
import {OperationResponse} from "../../../_model/operationResponse.model";
import { LogService } from 'src/app/_services/logService.service';
import {Appointment} from "../../../_model/appointment.model";
import {EmargementService} from "../../../_services/emargement.service";
import {AuthService} from "../../../_services/auth.service";

interface ResultatScan {
  idEmargement: string;
  token: string;
}

@Component({
  selector: 'app-home-etudiant',
  templateUrl: './home-etudiant.component.html',
  styleUrls: ['./home-etudiant.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class HomeEtudiantComponent implements OnInit {

  scannerEnabled: boolean = false;
  // @ts-ignore
  resultatScan: ResultatScan;
  information: string = "Aucune information de code détectée. Zoomez sur un QR code à scanner.\n";

  isSuccessful: boolean = false;
  errorMessage: string = "";

  token: string = "";
  idEmargement: string = "";

  showData: boolean = false;


  constructor(private logService: LogService,
              private cd: ChangeDetectorRef,
              private authService:AuthService,
              private emargementService:EmargementService
              ) { }

  ngOnInit(): void {
  }

  public scanSuccessHandler($event: any) {
    this.scannerEnabled = false;
    this.information = "Patientez pendant la récupération des informations... ";

    const appointment = new Appointment($event);
    this.logService.logAppointment(appointment).subscribe(
      (result: OperationResponse) => {
        this.information = $event;
        this.resultatScan = result.object; //a la place rien / un redirect / une alerte votre signature a bien été pris en compte

        console.log(this.resultatScan)
        this.emargementService.emarge(this.resultatScan.idEmargement, this.resultatScan.token).subscribe({
          next: () => {
            this.isSuccessful = true;
            // this.router.navigate(['emargement-list']);
          },
          error: err => {
            this.errorMessage = err.message;
          }
        });

        this.cd.markForCheck();
      },
      () => {
        this.information = "Une erreur s'est produite, veuillez réessayer...";
        this.cd.markForCheck();
      });
  }

  public enableScanner() {
    this.scannerEnabled = !this.scannerEnabled;
    this.information = "Aucune information de code détectée. Zoomez sur un QR code à scanner.\n";
  }

  onSubmit() {
    this.emargementService.emarge(this.idEmargement, this.token).subscribe({
      next: () => {
        this.isSuccessful = true;
        // this.router.navigate(['emargement-list']);
      },
      error: err => {
        this.errorMessage = err.message;
      }
    });
  }
  switchShowData() {
    this.showData = !this.showData
  }

}


