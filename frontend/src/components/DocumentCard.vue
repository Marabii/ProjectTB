<!-- YourParentComponent.vue -->
<script setup lang="ts">
import { ref } from 'vue'
import VuePdfEmbed from 'vue-pdf-embed'
import 'vue-pdf-embed/dist/styles/annotationLayer.css'
import 'vue-pdf-embed/dist/styles/textLayer.css'
import type { NoteDTO } from '../interfaces/NotesInterface'
import type { PropType } from 'vue'

// Import the Heart icon from lucide-vue-next
import { Heart } from 'lucide-vue-next'
import DocumentPreview from './DocumentPreview.vue'

// Define component props
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

// Heart state
const isHeartFilled = ref<boolean>(false)

// Function to toggle heart state
const toggleHeart = () => {
  isHeartFilled.value = !isHeartFilled.value
  console.log(`Heart is now ${isHeartFilled.value ? 'filled' : 'unfilled'}`)
}

// Handle document preview
const previewDocument = ref<boolean>(false)

// Function to open preview
const openPreview = () => {
  previewDocument.value = true
}

// Function to close preview
const closePreview = () => {
  previewDocument.value = false
}
</script>

<template>
  <div class="p-4">
    <!-- Main Card -->
    <div
      class="max-w-sm bg-white rounded-xl shadow-md overflow-hidden hover:shadow-2xl transition-shadow duration-300"
    >
      <button @click="openPreview" class="w-full focus:outline-none">
        <!-- PDF Thumbnail -->
        <div class="w-full bg-gray-100 flex items-center justify-center">
          <VuePdfEmbed
            :source="noteFile.demoFile.fileUrl"
            text-layer
            :page="1"
            annotation-layer
            class="h-full w-full object-cover"
            @loading-failed="loadingFailed"
          />
        </div>

        <!-- Content -->
        <div class="p-6">
          <h2 class="text-xl font-semibold text-gray-800">{{ noteFile.title }}</h2>
          <p class="mt-2 text-gray-600">
            Price: <span class="font-bold text-black">{{ noteFile.price }}€</span>
          </p>
          <p class="mt-1 text-gray-600">
            Author: <span class="font-bold text-black">{{ noteFile.owner.username }}</span>
          </p>
        </div>
      </button>

      <!-- Actions -->
      <div class="flex justify-between items-center px-6 pb-4">
        <button
          @click="toggleHeart"
          class="flex items-center space-x-2 text-gray-600 hover:text-red-500 transition-colors"
          aria-label="Toggle Favorite"
        >
          <Heart
            :stroke="isHeartFilled ? 'none' : '#000'"
            :fill="isHeartFilled ? '#e0245e' : 'none'"
            class="w-6 h-6"
          />
          <span class="text-sm">{{ isHeartFilled ? 'Favorited' : 'Favorite' }}</span>
        </button>
      </div>
    </div>

    <!-- Document Preview Modal with Transition -->
    <transition name="fade">
      <DocumentPreview v-if="previewDocument" :noteFile="noteFile" @close="closePreview" />
    </transition>
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
</style>
