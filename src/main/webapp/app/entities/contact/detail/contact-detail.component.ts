import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IContact } from '../contact.model';

@Component({
  selector: 'jhi-contact-detail',
  templateUrl: './contact-detail.component.html',
})
export class ContactDetailComponent implements OnInit {
  contact: IContact | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ contact }) => {
      this.contact = contact;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
