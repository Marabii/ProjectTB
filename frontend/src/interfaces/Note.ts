export interface NoteInterface {
  id: number;
  nbTasks: number;
  status: StatusEnum;
  title: string;
}

export enum StatusEnum {
  urgent = "urgent",
  serious = "serious",
  unimportant = "unimportant",
}

export interface TaskInterface {
  id: number;
  content: string;
  noteId: number;
}
