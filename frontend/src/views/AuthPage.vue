<script setup lang="ts">
import { ref } from 'vue'
import ToggleButtons from '@/components/Auth/ToggleButtons.vue'
import LoginForm from '@/components/Auth/LoginForm.vue'
import RegisterForm from '@/components/Auth/RegisterForm.vue'

const currentForm = ref<'login' | 'register'>('login')

const onToggle = (form: 'login' | 'register') => {
  currentForm.value = form
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100 p-4">
    <div class="w-full max-w-md bg-white rounded-lg shadow-lg p-6">
      <ToggleButtons :active-form="currentForm" @toggle="onToggle" />

      <div v-if="currentForm === 'login'">
        <LoginForm />
      </div>
      <div v-else>
        <RegisterForm />
      </div>
    </div>
  </div>
</template>
