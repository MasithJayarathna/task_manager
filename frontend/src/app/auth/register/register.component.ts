import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import {SnackbarService} from "../../shared/message/snackbar.service";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

 form: FormGroup;

   constructor(private fb: FormBuilder, private auth: AuthService, private router: Router, private snackbarService:SnackbarService) {
     this.form = this.fb.group({
       username: ['', Validators.required],
       password: ['', Validators.required]
     });
   }

   submit() {
     this.auth.register(this.form.value).subscribe({
       next: () => this.router.navigate(['/login']),
       error: (err) => {
         const message = err.status === 409
           ? 'Give username is already exist, use different username.'
           : `Registration failed! ${err.message || ''}`;
         this.snackbarService.showMessage(message, 'error');
       }
     });
   }

}
