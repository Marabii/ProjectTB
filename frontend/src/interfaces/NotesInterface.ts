// NoteFileDTO.ts

import type { UserDTO } from './UserInterface'

export interface NoteFileDTO {
  id: number
  fileUrl: string
  fileName: string
}

export interface NoteDTO {
  id: number
  owner: UserDTO
  title: string
  price: number
  description: string
  files: NoteFileDTO[]
  demoFile: NoteFileDTO
  authorizedUsers: UserDTO[]
}
