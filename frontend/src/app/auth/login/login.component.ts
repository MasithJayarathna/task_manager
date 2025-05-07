import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import {SnackbarService} from "../../shared/message/snackbar.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent{

  form: FormGroup;

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router, private snackbarService: SnackbarService) {
    this.form = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  submit() {
    this.auth.login(this.form.value).subscribe({
      next: () => this.router.navigate(['/tasks']),
      error: (err) => {
        const message = err.status === 401
          ? 'Invalid credentials. Please try again.'
          : `Login failed! ${err.message || ''}`;
        this.snackbarService.showMessage(message, 'error');
      }
    });
  }


}
