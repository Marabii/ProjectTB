<!-- DocumentPreview.vue -->
<script lang="ts" setup>
import type { NoteDTO } from '@/interfaces/NotesInterface'
import { type PropType } from 'vue'
import VuePdfEmbed from 'vue-pdf-embed'
import 'vue-pdf-embed/dist/styles/annotationLayer.css'
import 'vue-pdf-embed/dist/styles/textLayer.css'

defineProps({
  noteFile: {
    type: Object as PropType<NoteDTO>,
    required: true,
  },
})

// Handle loading failure
const loadingFailed = () => {
  alert('Failed to load the document. Please try again later.')
}
</script>

<template>
  <!-- Overlay -->
  <div class="fixed inset-0 overflow-y-scroll bg-black bg-opacity-70 justify-center flex z-30">
    <!-- Modal Container -->
    <div class="bg-white my-10 rounded-lg shadow-lg relative w-[90%] max-w-3xl">
      <!-- Close Button -->
      <button
        @click="$emit('close')"
        class="absolute z-30 top-4 right-4 text-gray-600 hover:text-gray-900 transition"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="h-6 w-6"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M6 18L18 6M6 6l12 12"
          />
        </svg>
      </button>

      <!-- PDF Embed -->
      <div class="w-full">
        <VuePdfEmbed
          :source="noteFile.demoFile.fileUrl"
          annotation-layer
          text-layer
          class="h-full w-full object-cover"
          @loading-failed="loadingFailed"
        />
      </div>

      <!-- Document Details -->
      <div
        class="p-6 bg-gradient-to-r from-white via-gray-100 to-white shadow-lg rounded-lg border border-gray-200 flex flex-col space-y-4"
      >
        <div>
          <h2 class="text-2xl font-bold text-gray-800 mb-2">{{ noteFile.title }}</h2>
          <p class="text-lg text-gray-600">
            <span class="font-medium text-gray-700">Price:</span>
            <span class="font-bold text-blue-600">{{ noteFile.price }}€</span>
          </p>
          <p class="text-lg text-gray-600">
            <span class="font-medium text-gray-700">Author:</span>
            <span class="font-bold text-blue-600">{{ noteFile.owner.username }}</span>
          </p>
        </div>

        <!-- Digital/Physical Indication -->
        <div class="flex items-center">
          <span class="text-gray-700 font-medium mr-2">Status:</span>
          <span
            :class="
              noteFile.isDigital ? 'bg-green-100 text-green-800' : 'bg-blue-100 text-blue-800'
            "
            class="px-3 py-1 rounded-full text-sm font-semibold"
          >
            {{ noteFile.isDigital ? 'Digital' : 'Physical' }}
          </span>
        </div>

        <!-- Availability Indication -->
        <div v-if="!noteFile.isDigital" class="flex items-center">
          <span class="text-gray-700 font-medium mr-2">Availability:</span>
          <span
            :class="
              noteFile.isAvailable ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
            "
            class="px-3 py-1 rounded-full text-sm font-semibold"
          >
            {{ noteFile.isAvailable ? 'Available' : 'Not Available' }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
.hiddenCanvasElement {
  display: none !important;
}
</style>
