<script setup lang="ts">
import { ref, onMounted } from "vue";
import type { NoteInterface, TaskInterface } from "./interfaces/Note";
import { HOST } from "@/config";
import { defineEmits } from "vue";

const props = defineProps(["note"]);
const note: NoteInterface = props.note;
const tasks = ref<TaskInterface[]>([]);
const taskInput = ref<string>("");
const emit = defineEmits(["note-deleted"]);

const getTasksOfNote = async () => {
  if (!note.id) {
    console.error("Note ID is missing");
    return;
  }
  try {
    const response = await fetch(`${HOST}/notes/${note.id}/tasks`, {
      method: "GET",
      headers: { "Content-Type": "application/json" },
    });
    if (!response.ok) {
      throw new Error(`Error: ${response.statusText}`);
    }
    tasks.value = await response.json();
  } catch (error) {
    console.error("Failed to fetch tasks: ", error);
  }
};

const handleSubmit = async () => {
  if (!note.id) {
    console.error("Cannot submit task without note ID");
    return;
  }
  try {
    const response = await fetch(`${HOST}/notes/${note.id}/tasks`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ content: taskInput.value }),
    });

    if (!response.ok) {
      throw new Error(`Error: ${response.statusText}`);
    }

    taskInput.value = "";
    await getTasksOfNote();
  } catch (error) {
    console.error("Failed to submit the task: ", error);
    alert("something went wrong, cannot submit task");
  }
};

const deleteTask = async (taskId: number) => {
  try {
    const response = await fetch(`${HOST}/tasks/${taskId}`, {
      method: "DELETE",
      headers: { "Content-Type": "application/json" },
    });

    if (!response.ok) {
      throw new Error(`Error: ${response.statusText}`);
    }

    tasks.value = tasks.value.filter((task) => task.id !== taskId);
  } catch (error) {
    console.error(error);
    alert("something went wrong, cannot delete task");
  }
};

const deleteNote = async () => {
  try {
    const response = await fetch(`${HOST}/notes/${note.id}`, {
      method: "DELETE",
    });

    if (!response.ok) {
      throw new Error(`Error: ${response.statusText}`);
    }

    emit("note-deleted", note.id);
  } catch (error) {
    console.error(error);
    alert("Cannot delete note");
  }
};

onMounted(() => {
  if (note.id) {
    getTasksOfNote();
  } else {
    console.log("Note ID not available on mounted.");
  }
});
</script>

<template>
  <form @submit.prevent="handleSubmit" :class="['note', note.status]">
    <button type="button" @click="deleteNote" class="delete-button">
      &#x1F5D1;
    </button>
    <h2>{{ note.title }}</h2>

    <div class="tasks">
      <div v-for="task in tasks" class="task">
        <div class="content">{{ task.content }}</div>
        <button
          type="button"
          @click="deleteTask(task.id)"
          class="delete-button"
        >
          &#x1F5D1;
        </button>
      </div>

      <div class="new-task">
        <input
          v-model="taskInput"
          class="content"
          placeholder="Enter new task..."
        />
        <button type="submit" class="create-btn">+</button>
      </div>
    </div>
  </form>
</template>

<style lang="scss" scoped>
@use "assets/mediaQueryScreens.scss" as *;
@use "assets/colors.scss" as *;

$gutter-size: 15px;

.note {
  min-height: 200px;
  border-radius: 10px;
  padding: 10px;
  color: white;
  position: relative;

  & > .delete-button {
    all: unset;
    position: absolute;
    top: 5px;
    right: 10px;
    font-size: 25px;
    cursor: pointer;
  }

  h2 {
    padding-bottom: 5px;
    width: calc(100% - 25px);
  }

  .task {
    padding: 10px 5px 10px 5px;
    margin-bottom: 10px;
  }

  .task {
    border-radius: 5px;
    display: flex;
    align-items: center;

    & > .content {
      flex-grow: 1;
    }

    & > .delete-button {
      visibility: hidden;
      flex-grow: 0;
      color: black;
      font-size: 20px;
      cursor: pointer;
    }

    &:hover > .delete-button {
      visibility: visible;
    }
  }

  .new-task {
    display: flex;

    & > input {
      flex-grow: 1;
      border: 0;
      padding: 15px 5px 15px 5px;

      &::placeholder {
        color: white;
      }
    }

    & > .create-btn {
      background-color: white;
      font-size: 25px;
      font-weight: bold;
      width: 40px;
    }
  }

  &.unimportant {
    background-color: $dark-green;
    .task,
    .new-task > input {
      background-color: $light-green;
      color: darken($dark-green, 20%);
    }

    .new-task > .create-btn {
      color: $dark-green;
    }
  }

  &.serious {
    background-color: $dark-yellow;

    .task,
    .new-task > input {
      background-color: $light-yellow;
      color: darken($dark-yellow, 20%);
    }

    .new-task > .create-btn {
      color: $dark-yellow;
    }
  }

  &.urgent {
    background-color: $dark-red;

    .task,
    .new-task > input {
      background-color: $light-red;
      color: darken($dark-red, 20%);
    }

    .new-task > .create-btn {
      color: $dark-red;
    }
  }
}

@include smallScreen {
  .note {
    width: 100%;
  }
}

@include mediumScreen {
  .note {
    width: calc((100% - $gutter-size) / 2);
  }
}

@include largeScreen {
  .note {
    width: calc((100% - ($gutter-size * 2)) / 3);
  }
}
</style>
