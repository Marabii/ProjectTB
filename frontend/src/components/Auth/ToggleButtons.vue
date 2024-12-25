<script setup lang="ts">
const props = defineProps<{
  activeForm: 'login' | 'register'
}>()

const emit = defineEmits<{
  (e: 'toggle', form: 'login' | 'register'): void
}>()

const emitToggle = (form: 'login' | 'register') => {
  emit('toggle', form)
}

const buttonClasses = (form: 'login' | 'register'): string =>
  form === props.activeForm
    ? 'px-6 py-2 bg-black text-white rounded-md focus:outline-none'
    : 'px-6 py-2 bg-gray-200 text-black rounded-md focus:outline-none'
</script>

<template>
  <div class="flex space-x-3 justify-center mb-6">
    <button :class="buttonClasses('login')" @click="emitToggle('login')">Login</button>
    <button :class="buttonClasses('register')" @click="emitToggle('register')">Register</button>
  </div>
</template>
