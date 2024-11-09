<script setup lang="ts">
import { ref, onMounted } from "vue";
import Note from "./Note.vue";
import { HOST } from "@/config";
import CreateNote from "./CreateNote.vue";
import { NoteInterface, StatusEnum } from "./interfaces/Note";
import Navbar from "./Navbar.vue";

const notesList = ref<NoteInterface[]>([]);

const fetchNotes = async () => {
  const notes: NoteInterface[] = await (await fetch(`${HOST}/notes`)).json();
  return notes;
};

const handleNoteDeleted = (noteId: number) => {
  notesList.value = notesList.value.filter((note) => note.id !== noteId);
};

const filterNotes = async (filter: string) => {
  const notes = await fetchNotes();
  switch (filter) {
    case "all":
      notesList.value = notes;
      break;
    case "unimportant":
      notesList.value = notes.filter(
        (note) => note.status === StatusEnum.unimportant
      );
      break;
    case "serious":
      notesList.value = notes.filter(
        (note) => note.status === StatusEnum.serious
      );
      break;
    case "urgent":
      notesList.value = notes.filter(
        (note) => note.status === StatusEnum.urgent
      );
      break;
    default:
      break;
  }
};

onMounted(async () => {
  const notes = await fetchNotes();
  notesList.value = notes;
});
</script>

<template>
  <Navbar @filter-selected="filterNotes" />
  <div class="notes-container">
    <Note
      v-for="note in notesList"
      :key="note.id"
      :note="note"
      @note-deleted="handleNoteDeleted"
    ></Note>

    <CreateNote @note-added="fetchNotes"></CreateNote>
  </div>
</template>

<style lang="scss" scoped>
@use "assets/mediaQueryScreens.scss" as *;

$gutter-size: 15px;

.notes-container {
  display: flex;
  flex-wrap: wrap;
  gap: $gutter-size;

  margin-right: auto;
  margin-left: auto;

  border: 1px solid gray;
  border-radius: 10px;

  padding: $gutter-size;
}

@include smallScreen {
  .notes-container {
    width: 90vw;
  }
}

@include mediumScreen {
  .notes-container {
    width: 80vw;
  }
}

@include largeScreen {
  .notes-container {
    width: 1024px;
  }
}
</style>
