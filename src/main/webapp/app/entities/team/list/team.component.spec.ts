import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TeamService } from '../service/team.service';

import { TeamComponent } from './team.component';

describe('Team Management Component', () => {
  let comp: TeamComponent;
  let fixture: ComponentFixture<TeamComponent>;
  let service: TeamService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'team', component: TeamComponent }]), HttpClientTestingModule],
      declarations: [TeamComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(TeamComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TeamComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TeamService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.teams?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to teamService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getTeamIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getTeamIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
