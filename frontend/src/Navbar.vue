<template>
  <nav class="nav-bar">
    <button @click="emitEvent('all')">All</button>
    <button @click="emitEvent('unimportant')">Unimportant</button>
    <button @click="emitEvent('serious')">Serious</button>
    <button @click="emitEvent('urgent')">Urgent</button>
  </nav>
</template>

<script setup lang="ts">
import { defineEmits } from "vue";

const emit = defineEmits(["filterSelected"]);

function emitEvent(filter: string) {
  emit("filterSelected", filter);
}
</script>

<style scoped lang="scss">
.nav-bar {
  display: flex;
  justify-content: space-around;
  padding: 10px;
  background-color: #f5f5f5; // Light grey background

  button {
    border: none;
    padding: 5px 15px;
    cursor: pointer;
    border-radius: 5px;
    font-weight: bold;

    &:focus,
    &:hover {
      outline: none;
      opacity: 0.9;
    }
  }

  button:nth-child(1) {
    background-color: #000;
    color: #fff;
  }

  button:nth-child(2) {
    background-color: #4caf50; // Green
    color: #fff;
  }

  button:nth-child(3) {
    background-color: #ffc107; // Amber
    color: #fff;
  }

  button:nth-child(4) {
    background-color: #f44336; // Red
    color: #fff;
  }
}
</style>
