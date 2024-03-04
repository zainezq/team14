import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IAvailableDate, NewAvailableDate } from '../available-date.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAvailableDate for edit and NewAvailableDateFormGroupInput for create.
 */
type AvailableDateFormGroupInput = IAvailableDate | PartialWithRequiredKeyOf<NewAvailableDate>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAvailableDate | NewAvailableDate> = Omit<T, 'fromTime' | 'toTime'> & {
  fromTime?: string | null;
  toTime?: string | null;
};

type AvailableDateFormRawValue = FormValueOf<IAvailableDate>;

type NewAvailableDateFormRawValue = FormValueOf<NewAvailableDate>;

type AvailableDateFormDefaults = Pick<NewAvailableDate, 'id' | 'fromTime' | 'toTime' | 'isAvailable'>;

type AvailableDateFormGroupContent = {
  id: FormControl<AvailableDateFormRawValue['id'] | NewAvailableDate['id']>;
  fromTime: FormControl<AvailableDateFormRawValue['fromTime']>;
  toTime: FormControl<AvailableDateFormRawValue['toTime']>;
  isAvailable: FormControl<AvailableDateFormRawValue['isAvailable']>;
  userProfile: FormControl<AvailableDateFormRawValue['userProfile']>;
  team: FormControl<AvailableDateFormRawValue['team']>;
};

export type AvailableDateFormGroup = FormGroup<AvailableDateFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AvailableDateFormService {
  createAvailableDateFormGroup(availableDate: AvailableDateFormGroupInput = { id: null }): AvailableDateFormGroup {
    const availableDateRawValue = this.convertAvailableDateToAvailableDateRawValue({
      ...this.getFormDefaults(),
      ...availableDate,
    });
    return new FormGroup<AvailableDateFormGroupContent>({
      id: new FormControl(
        { value: availableDateRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      fromTime: new FormControl(availableDateRawValue.fromTime, {
        validators: [Validators.required],
      }),
      toTime: new FormControl(availableDateRawValue.toTime, {
        validators: [Validators.required],
      }),
      isAvailable: new FormControl(availableDateRawValue.isAvailable, {
        validators: [Validators.required],
      }),
      userProfile: new FormControl(availableDateRawValue.userProfile),
      team: new FormControl(availableDateRawValue.team),
    });
  }

  getAvailableDate(form: AvailableDateFormGroup): IAvailableDate | NewAvailableDate {
    return this.convertAvailableDateRawValueToAvailableDate(form.getRawValue() as AvailableDateFormRawValue | NewAvailableDateFormRawValue);
  }

  resetForm(form: AvailableDateFormGroup, availableDate: AvailableDateFormGroupInput): void {
    const availableDateRawValue = this.convertAvailableDateToAvailableDateRawValue({ ...this.getFormDefaults(), ...availableDate });
    form.reset(
      {
        ...availableDateRawValue,
        id: { value: availableDateRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): AvailableDateFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fromTime: currentTime,
      toTime: currentTime,
      isAvailable: false,
    };
  }

  private convertAvailableDateRawValueToAvailableDate(
    rawAvailableDate: AvailableDateFormRawValue | NewAvailableDateFormRawValue
  ): IAvailableDate | NewAvailableDate {
    return {
      ...rawAvailableDate,
      fromTime: dayjs(rawAvailableDate.fromTime, DATE_TIME_FORMAT),
      toTime: dayjs(rawAvailableDate.toTime, DATE_TIME_FORMAT),
    };
  }

  private convertAvailableDateToAvailableDateRawValue(
    availableDate: IAvailableDate | (Partial<NewAvailableDate> & AvailableDateFormDefaults)
  ): AvailableDateFormRawValue | PartialWithRequiredKeyOf<NewAvailableDateFormRawValue> {
    return {
      ...availableDate,
      fromTime: availableDate.fromTime ? availableDate.fromTime.format(DATE_TIME_FORMAT) : undefined,
      toTime: availableDate.toTime ? availableDate.toTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}