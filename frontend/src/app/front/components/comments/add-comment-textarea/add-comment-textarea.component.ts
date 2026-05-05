import {Component, ElementRef, EventEmitter, Output, ViewChild} from '@angular/core';
import {Button} from "primeng/button";
import {NgStyle} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Textarea} from "primeng/textarea";
import {User} from '../../../../services/billkeeper-ws/user/model';
import {ToastMessageService} from '../../../../services/toast-message.service';
import {UserWsService} from '../../../../services/billkeeper-ws/user/user-ws.service';
import {SuggestedUsersMenuComponent} from '../../commun/suggested-users-menu/suggested-users-menu.component';
import {CommentWithTaggedUsers} from './model';

@Component({
  selector: 'app-add-comment-textarea',
  imports: [
    Button,
    ReactiveFormsModule,
    Textarea,
    NgStyle,
    FormsModule,
    SuggestedUsersMenuComponent
  ],
  templateUrl: './add-comment-textarea.component.html',
  styleUrl: './add-comment-textarea.component.scss'
})
export class AddCommentTextareaComponent {

  @ViewChild('commentTextArea') commentTextArea!: ElementRef;
  @ViewChild('mirrorTextArea') mirrorTextArea!: ElementRef;

  @Output()
  onAddComment: EventEmitter<CommentWithTaggedUsers> = new EventEmitter();

  content: string = "";
  showSuggestedUsers = false;
  suggestedUsersDivPosition = {top: 0, left: 0};
  suggestedUsers: User[] = [];
  taggedUsers: User[] = [];

  constructor(
    private userWsService: UserWsService,
    private toastMessageService: ToastMessageService
  ) {
  }

  onClickSuggestedUser(user: User) {
    this.showSuggestedUsers = false;
    if (!this.userIsAlreadyTagged(user)) {
      this.taggedUsers.push(user)
    }
    const cursorPosition = this.commentTextArea.nativeElement.selectionStart;
    const currentWord = this.getCurrentWord(this.commentTextArea.nativeElement.value, cursorPosition);
    this.commentTextArea.nativeElement.value = this.replaceString(this.commentTextArea.nativeElement.value, currentWord.startIndex, currentWord.endIndex, "@" + user.firstname);
    this.commentTextArea.nativeElement.focus();
    this.content = this.commentTextArea.nativeElement.value;
  }

  private userIsAlreadyTagged(user: User) {
    return this.taggedUsers
      .filter(u => u.id === user.id)
      .length > 0;
  }

  private getCurrentWord(text: string, cursorPosition: number): {
    startIndex: number,
    endIndex: number,
    value: string
  } {
    const left = text.slice(0, cursorPosition).search(/\S+$/);
    const right = text.slice(cursorPosition).search(/\s/);
    return {
      startIndex: left,
      endIndex: right === -1 ? text.length : cursorPosition + right,
      value: right === -1 ? text.slice(left) : text.slice(left, cursorPosition + right)
    }
  }

  private replaceString(originalText: string, start: number, end: number, replacement: string) {
    return originalText.slice(0, start) + replacement + originalText.slice(end);
  }

  async onCommentInputChange(event: any) {
    const value = event.target.value;
    const cursorPosition = event.target.selectionStart;
    const currentWord = this.getCurrentWord(value, cursorPosition);

    if (currentWord.value.startsWith("@")) {
      await this.updateSuggestedUsers(currentWord.value.replace("@", ""));
      if (this.suggestedUsers.length > 0) {
        this.showSuggestedUsers = true;
        this.updateMirrorTextArea();
      }
    } else {
      this.showSuggestedUsers = false;
    }
  }

  private async updateSuggestedUsers(user: string) {
    try {
      this.suggestedUsers = await this.userWsService.getUsersStartingWith(user);
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  private updateMirrorTextArea() {
    const textArea = this.commentTextArea.nativeElement;
    const mirrorArea = this.mirrorTextArea.nativeElement;
    const text = textArea.value;

    mirrorArea.textContent = text.substring(0, textArea.selectionStart);

    const span = document.createElement('span');
    span.textContent = '\u200b';
    mirrorArea.appendChild(span);

    const spanRect = span.getBoundingClientRect();
    const textareaRect = textArea.getBoundingClientRect();

    this.suggestedUsersDivPosition = {
      top: spanRect.top - textareaRect.top + 20,
      left: spanRect.left - textareaRect.left,
    };
  }

  addComment() {
    this.updateTaggedUsers();
    this.onAddComment.emit({
      content: this.content,
      taggedUsersId: this.taggedUsers.map(user => user.id)
    });
    this.taggedUsers = [];
    this.content = "";
  }

  private updateTaggedUsers() {
    const textArea = this.commentTextArea.nativeElement;
    const text = textArea.value;
    let usersToRemove: User[] = [];
    this.taggedUsers.forEach(user => {
      if (text.search("@" + user.firstname) === -1) {
        usersToRemove.push(user);
      }
    });
    this.taggedUsers = this.taggedUsers
      .filter(user => !usersToRemove.find(u => u.id === user.id));
  }

}
