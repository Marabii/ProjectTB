<script setup lang="ts">
import { ref } from 'vue'
import VuePdfEmbed from 'vue-pdf-embed'
import 'vue-pdf-embed/dist/styles/annotationLayer.css'
import 'vue-pdf-embed/dist/styles/textLayer.css'
import type { PropType } from 'vue'
import DocumentPreview from './DocumentPreview.vue'
import type { NoteDTO } from '@/interfaces/NotesInterface'
import axios from 'axios'
import { serverURL } from '@/utilis/constants'

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

// Deposit Documents Logic
const isDepositing = ref<boolean>(false)
const depositMessage = ref<string | null>(null)
const depositMessageType = ref<'success' | 'error' | null>(null)

const depositDocuments = async (noteId: number) => {
  isDepositing.value = true
  depositMessage.value = null
  depositMessageType.value = null

  try {
    const response = await axios.put(
      `${serverURL}/api/protected/notes/${noteId}/deposit-documents`,
      {},
      { withCredentials: true },
    )
    if (response.status === 200) {
      depositMessage.value = 'Documents deposited successfully.'
      depositMessageType.value = 'success'
    } else {
      depositMessage.value = 'Failed to deposit documents.'
      depositMessageType.value = 'error'
    }
  } catch (error: any) {
    if (error.response) {
      if (error.response.status === 409) {
        depositMessage.value = 'Conflict: Documents cannot be deposited at this time.'
      } else if (error.response.status === 404) {
        depositMessage.value = 'Error: Document not found.'
      } else if (error.response.status === 401) {
        depositMessage.value = 'Error: You should relogin.'
      } else {
        depositMessage.value = 'An unexpected error occurred.'
      }
    } else {
      depositMessage.value = 'Network error. Please try again.'
    }
    depositMessageType.value = 'error'
  } finally {
    isDepositing.value = false
    // Clear message after 5 seconds
    setTimeout(() => {
      depositMessage.value = null
      depositMessageType.value = null
    }, 5000)
  }
}
</script>

<template>
  <div class="bg-white w-fit">
    <button
      @click="openPreview"
      class="p-4 max-w-sm bg-white rounded-xl shadow-md hover:shadow-2xl transition-shadow duration-300 focus:outline-none"
    >
      <!-- PDF Thumbnail -->
      <div class="w-fit mx-auto bg-gray-100 flex items-center justify-center">
        <VuePdfEmbed
          :source="noteFile.demoFile.fileUrl"
          text-layer
          :page="1"
          annotation-layer
          class="h-full w-full object-cover"
          @loading-failed="loadingFailed"
        />
      </div>
    </button>
    <!-- Content -->
    <div class="p-6 text-start flex flex-col space-y-4">
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
          :class="noteFile.isDigital ? 'bg-green-100 text-green-800' : 'bg-blue-100 text-blue-800'"
          class="px-3 py-1 rounded-full text-sm font-semibold"
        >
          {{ noteFile.isDigital ? 'Digital' : 'Physical' }}
        </span>
      </div>

      <!-- Availability Indication -->
      <div v-if="!noteFile.isDigital" class="flex items-center">
        <span class="text-gray-700 font-medium mr-2">Availability:</span>
        <span
          :class="noteFile.isAvailable ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
          class="px-3 py-1 rounded-full text-sm font-semibold"
        >
          {{ noteFile.isAvailable ? 'Available' : 'Not Available' }}
        </span>
      </div>

      <!-- Deposit Documents Section -->
      <div v-if="!noteFile.isDigital && !noteFile.isAvailable" class="mt-4">
        <p class="text-gray-700 mb-2">
          Please check if the deposit box is empty. If not, deposit the documents.
        </p>
        <button
          @click="depositDocuments(noteFile.id)"
          :disabled="isDepositing"
          class="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 disabled:opacity-50 transition-colors flex items-center justify-center"
        >
          <svg
            v-if="isDepositing"
            class="animate-spin h-5 w-5 mr-2 text-white"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
          >
            <circle
              class="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              stroke-width="4"
            ></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
          </svg>
          {{ isDepositing ? 'Depositing...' : 'Deposit Documents' }}
        </button>

        <!-- Feedback Message -->
        <p
          v-if="depositMessage"
          :class="depositMessageType === 'success' ? 'text-green-600' : 'text-red-600'"
          class="mt-3 font-semibold text-center"
        >
          {{ depositMessage }}
        </p>
      </div>
    </div>
  </div>
  <!-- Document Preview Modal with Transition -->
  <transition name="fade">
    <DocumentPreview v-if="previewDocument" :noteFile="noteFile" @close="closePreview" />
  </transition>
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
