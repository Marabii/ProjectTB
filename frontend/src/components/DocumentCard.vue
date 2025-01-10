<!-- YourParentComponent.vue -->
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios, { AxiosError } from 'axios'
import VuePdfEmbed from 'vue-pdf-embed'
import 'vue-pdf-embed/dist/styles/annotationLayer.css'
import 'vue-pdf-embed/dist/styles/textLayer.css'
import type { NoteDTO } from '../interfaces/NotesInterface'
import type { PropType } from 'vue'

// Import the Heart icon from lucide-vue-next
import { Heart } from 'lucide-vue-next'
import DocumentPreview from './DocumentPreview.vue'
import { serverURL } from '@/utilis/constants'

// Define component props
const props = defineProps({
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

// Loading state
const loading = ref<boolean>(false)

// Function to toggle heart state
const toggleHeart = () => {
  if (loading.value) return // Prevent toggling while loading
  if (isHeartFilled.value) {
    removeFromFavorites()
  } else {
    addToFavorites()
  }
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

// Function to check if the note is already favorited
const checkIfFavorited = async () => {
  try {
    const response = await axios.get(`${serverURL}/api/protected/favourite-documents`, {
      withCredentials: true,
    })
    const favoriteNotes: NoteDTO[] = response.data

    isHeartFilled.value = favoriteNotes.some((note) => note.id === props.noteFile.id)
  } catch (error) {
    console.error('Error fetching favorite documents:', error)
    // Optionally handle the error, e.g., show a notification
  }
}

// Fetch initial favorite state when component mounts
onMounted(() => {
  checkIfFavorited()
})

// Function to add to favorites
const addToFavorites = async () => {
  loading.value = true
  try {
    const response = await axios.put(
      `${serverURL}/api/protected/favourite-documents/${props.noteFile.id}`,
      {},
      { withCredentials: true },
    )
    if (response.status === 201) {
      isHeartFilled.value = true
      console.log(response.data)
      alert('Added to favorites!')
    }
  } catch (err) {
    const error = err as AxiosError
    console.error('Error adding to favorites:', error)

    if (error.response?.status === 401) {
      alert('You need to log in first to add this item to favorites.')
    } else {
      alert('Failed to add to favorites. Please try again.')
    }
  } finally {
    loading.value = false
  }
}

// Function to remove from favorites
const removeFromFavorites = async () => {
  loading.value = true
  try {
    const response = await axios.delete(
      `${serverURL}/api/protected/favourite-documents/${props.noteFile.id}`,
      { withCredentials: true },
    )
    if (response.status === 200) {
      isHeartFilled.value = false
      console.log(response.data)
      alert('Removed from favorites!')
    }
  } catch (err) {
    const error = err as AxiosError
    console.error('Error removing from favorites:', error)

    if (error.response?.status === 401) {
      alert('You need to log in first to remove this item from favorites.')
    } else {
      alert('Failed to remove from favorites. Please try again.')
    }
  } finally {
    loading.value = false
  }
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
          :disabled="loading"
          class="flex items-center space-x-2 text-gray-600 hover:text-red-500 transition-colors focus:outline-none"
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

<style>
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
