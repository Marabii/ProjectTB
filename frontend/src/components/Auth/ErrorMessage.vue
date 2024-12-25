<!-- src/components/Auth/ErrorMessage.vue -->
<template>
  <p v-if="message" class="text-red-500 text-sm mt-1">
    {{ message }}
  </p>
</template>

<script lang="ts">
import { defineComponent, type PropType } from 'vue'

export default defineComponent({
  name: 'ErrorMessage',
  props: {
    message: {
      type: String as PropType<string>,
      required: false,
      default: '',
    },
  },
})
</script>
