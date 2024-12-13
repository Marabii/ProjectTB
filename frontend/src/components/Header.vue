<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const buttonClasses = (path: string) =>
  computed(() => {
    return route.path === path ? 'bg-white text-custom-blue' : 'bg-custom-blue text-white'
  })
</script>

<template>
  <header class="w-full flex justify-between items-center bg-custom-blue p-5">
    <div class="flex items-center gap-5">
      <img class="w-[100px]" src="/src/assets/logo.png" alt="logo" />
      <ul class="flex space-x-10">
        <li>
          <button :class="buttonClasses('/').value" class="w-[160px] rounded-xl">
            <router-link to="/">Menu</router-link>
          </button>
        </li>
        <li>
          <button :class="buttonClasses('/my-files').value" class="w-[160px] rounded-xl">
            <router-link to="/my-files">My files</router-link>
          </button>
        </li>
        <li>
          <button :class="buttonClasses('/my-favorites').value" class="w-[160px] rounded-xl">
            <router-link to="/my-favorites">My favorites</router-link>
          </button>
        </li>
      </ul>
    </div>
    <button>
      <img
        class="w-[50px] rounded-full"
        src="/src/assets/profilePicture.jpg"
        alt="profile-picture"
      />
    </button>
  </header>
</template>
