<!-- eslint-disable vue/multi-word-component-names -->
<script setup lang="ts">
import { User } from 'lucide-vue-next'
import { RouterLink } from 'vue-router'
</script>

<template>
  <header class="flex px-6 py-4 bg-primary items-center text-white justify-between">
    <ul class="flex items-center space-x-3">
      <li class="text-2xl font-bold mr-5">
        <router-link to="/" class="hover:underline">StudiTrade</router-link>
      </li>
      <li>
        <router-link to="/purchased-files" class="hover:underline">Purchased files</router-link>
      </li>
      <li>
        <router-link to="/personal-notes" class="hover:underline">Personal Notes</router-link>
      </li>
      <li>
        <router-link to="/sell-files" class="hover:underline">Sell files</router-link>
      </li>
      <li>
        <router-link to="/my-favorites" class="hover:underline">My favorites</router-link>
      </li>
    </ul>
    <div class="flex items-center space-x-3">
      <router-link to="/auth-page"><User /></router-link>
    </div>
  </header>
</template>
