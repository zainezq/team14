import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IComment, NewComment } from '../comment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IComment for edit and NewCommentFormGroupInput for create.
 */
type CommentFormGroupInput = IComment | PartialWithRequiredKeyOf<NewComment>;

type CommentFormDefaults = Pick<NewComment, 'id'>;

type CommentFormGroupContent = {
  id: FormControl<IComment['id'] | NewComment['id']>;
  rating: FormControl<IComment['rating']>;
  content: FormControl<IComment['content']>;
  likeCount: FormControl<IComment['likeCount']>;
  replyingTo: FormControl<IComment['replyingTo']>;
  author: FormControl<IComment['author']>;
  targetUser: FormControl<IComment['targetUser']>;
  match: FormControl<IComment['match']>;
};

export type CommentFormGroup = FormGroup<CommentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CommentFormService {
  createCommentFormGroup(comment: CommentFormGroupInput = { id: null }): CommentFormGroup {
    const commentRawValue = {
      ...this.getFormDefaults(),
      ...comment,
    };
    return new FormGroup<CommentFormGroupContent>({
      id: new FormControl(
        { value: commentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      rating: new FormControl(commentRawValue.rating, {
        validators: [Validators.required, Validators.min(0), Validators.max(5)],
      }),
      content: new FormControl(commentRawValue.content, {
        validators: [Validators.maxLength(256)],
      }),
      likeCount: new FormControl(commentRawValue.likeCount, {
        validators: [Validators.min(0)],
      }),
      replyingTo: new FormControl(commentRawValue.replyingTo),
      author: new FormControl(commentRawValue.author),
      targetUser: new FormControl(commentRawValue.targetUser),
      match: new FormControl(commentRawValue.match),
    });
  }

  getComment(form: CommentFormGroup): IComment | NewComment {
    return form.getRawValue() as IComment | NewComment;
  }

  resetForm(form: CommentFormGroup, comment: CommentFormGroupInput): void {
    const commentRawValue = { ...this.getFormDefaults(), ...comment };
    form.reset(
      {
        ...commentRawValue,
        id: { value: commentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CommentFormDefaults {
    return {
      id: null,
    };
  }
}
