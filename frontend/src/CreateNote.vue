<script setup lang="ts">
import { ref } from "vue";
import { HOST } from "@/config";
import { defineEmits } from "vue";

const newNoteStatus = ref<string>("unimportant");
const newNoteTitle = ref<string>("");
const currentClass = ref<string>("note unimportant");
const emit = defineEmits(["note-added"]);

async function handleSubmit() {
  try {
    const response = await fetch(`${HOST}/notes`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        title: newNoteTitle.value,
        status: newNoteStatus.value,
      }),
    });

    if (!response.ok) {
      throw new Error(`Error: ${response.statusText}`);
    }

    newNoteTitle.value = "";
    newNoteStatus.value = "unimportant";
    handleChange();
    emit("note-added");
  } catch (error) {
    console.error("Failed to submit the note:", error);
    alert("Something went wrong");
  }
}

const handleChange = () => {
  currentClass.value = "note " + newNoteStatus.value;
};
</script>

<template>
  <form @submit.prevent="handleSubmit" :class="currentClass">
    <input
      class="new-note-title"
      v-model="newNoteTitle"
      placeholder="Enter note title..."
    />

    <div class="status-select">
      <div>
        <input
          type="radio"
          id="unimportant"
          name="status"
          v-model="newNoteStatus"
          @change="handleChange"
          value="unimportant"
        />
        <label for="unimportant">Unimportant</label>
      </div>

      <div>
        <input
          type="radio"
          id="serious"
          name="status"
          v-model="newNoteStatus"
          value="serious"
          @change="handleChange"
        />
        <label for="serious">Serious</label>
      </div>

      <div>
        <input
          type="radio"
          id="urgent"
          name="status"
          v-model="newNoteStatus"
          @change="handleChange"
          value="urgent"
        />
        <label for="urgent">Urgent</label>
      </div>
    </div>

    <button type="submit" class="create-btn">Create new note</button>
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

  & > .new-note-title {
    /* Updated selector */
    padding: 15px 5px;
    border: 0;
    font-size: 20px;
    width: 100%;
    border-radius: 5px;

    &::placeholder {
      font-style: italic;
      color: white;
    }
  }

  & > .create-btn {
    /* Updated selector */
    padding: 10px 5px;
    width: 100%;
    background-color: white;
    color: $dark-green;
    font-weight: bold;
    font-size: 15px;
    cursor: pointer; /* Optional: Change cursor on hover */
  }

  &.unimportant {
    background-color: $dark-green;

    & > .new-note-title {
      /* Updated selector */
      background-color: $light-green;
      color: darken($dark-green, 20%);
    }

    & > .create-btn {
      /* Updated selector */
      color: $dark-green;
    }
  }

  &.serious {
    background-color: $dark-yellow;

    & > .new-note-title {
      /* Updated selector */
      background-color: $light-yellow;
      color: darken($dark-yellow, 20%);
    }

    & > .create-btn {
      /* Updated selector */
      color: $dark-yellow;
    }
  }

  &.urgent {
    background-color: $dark-red;

    & > .new-note-title {
      /* Updated selector */
      background-color: $light-red;
      color: darken($dark-red, 20%);
    }

    & > .create-btn {
      /* Updated selector */
      color: $dark-red;
    }
  }

  .status-select {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;

    margin-top: 10px;
    margin-bottom: 10px;

    & input {
      margin-right: 5px;
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
    width: calc((100% - #{$gutter-size}) / 2);
  }
}

@include largeScreen {
  .note {
    width: calc((100% - (#{$gutter-size} * 2)) / 3);
  }
}
</style>
