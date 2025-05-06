import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TaskService } from '../task.service';
import { Task } from '../task.model';

@Component({
  selector: 'app-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.css']
})
export class TaskComponent implements OnInit {
  form: FormGroup;
  tasks: Task[] = [];
  upcomingTasks: Task[] = [];
  today: string;
  isFormVisible = false;
  isEditMode = false;
  editingTaskId: number | null = null;

  constructor(private fb: FormBuilder, private taskService: TaskService) {
    this.form = this.fb.group({
      title: ['', Validators.required],
      description: [''],
      dueDate: ['', Validators.required]
    });
    
    this.today = new Date().toISOString().split('T')[0];
  }

  ngOnInit(): void {
    this.loadTasks();
  }

  toggleForm(): void {
    this.isFormVisible = !this.isFormVisible;
    if (!this.isFormVisible) {
      this.isEditMode = false;
      this.editingTaskId = null;
      this.form.reset();
    }
  }

  loadTasks(): void {
    this.taskService.getTasks().subscribe(tasks => {
      this.tasks = tasks;
      this.upcomingTasks = this.tasks.filter(t => new Date(t.dueDate) > new Date());
    });
  }

  submit(): void {
    if (this.form.invalid) return;

    const taskData = {
      ...this.form.value,
      dueDate: new Date(this.form.value.dueDate).toISOString()
    };

    if (this.isEditMode && this.editingTaskId) {
      this.taskService.updateTask(this.editingTaskId, taskData)
        .subscribe(() => this.handleSuccess());
    } else {
      this.taskService.addTask(taskData)
        .subscribe(() => this.handleSuccess());
    }
  }

  private handleSuccess(): void {
    this.loadTasks();
    this.toggleForm();
  }

  deleteTask(taskId: number): void {
    this.taskService.deleteTask(taskId)
      .subscribe(() => this.loadTasks());
  }

  editTask(task: Task): void {
    this.isFormVisible = true;
    this.isEditMode = true;
    this.editingTaskId = task.id || null;
    
    this.form.patchValue({
      title: task.title,
      dueDate: task.dueDate,
      description: task.description
    });
  }
}