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
    const response = await axios.put(`${serverURL}/protected/notes/${noteId}/deposit-documents`)
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
  <div class="p-4">
    <!-- Main Card -->
    <div
      class="max-w-sm bg-white rounded-xl shadow-md overflow-hidden hover:shadow-2xl transition-shadow duration-300"
    >
      <button @click="openPreview" class="w-full focus:outline-none">
        <!-- PDF Thumbnail -->
        <div class="w-full bg-gray-100 flex items-center justify-center h-48">
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

          <!-- Digital/Physical Indication -->
          <p class="mt-2">
            Status:
            <span
              :class="noteFile.isDigital ? 'text-green-600' : 'text-blue-600'"
              class="font-semibold"
            >
              {{ noteFile.isDigital ? 'Digital' : 'Physical' }}
            </span>
          </p>

          <!-- Availability Indication and Deposit Button -->
          <div v-if="!noteFile.isDigital" class="mt-2">
            <span
              :class="noteFile.isAvailable ? 'text-green-600' : 'text-red-600'"
              class="font-semibold"
            >
              {{ noteFile.isAvailable ? 'Available' : 'Not Available' }}
            </span>

            <div v-if="!noteFile.isAvailable" class="mt-2">
              <p class="text-gray-700">
                Please check if the deposit box is empty. If not, deposit the documents.
              </p>
              <button
                @click="depositDocuments(noteFile.id)"
                :disabled="isDepositing"
                class="mt-2 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 disabled:opacity-50 transition-colors"
              >
                {{ isDepositing ? 'Depositing...' : 'Deposit Documents' }}
              </button>

              <!-- Feedback Message -->
              <p
                v-if="depositMessage"
                :class="depositMessageType === 'success' ? 'text-green-600' : 'text-red-600'"
                class="mt-2 font-semibold"
              >
                {{ depositMessage }}
              </p>
            </div>
          </div>
        </div>
      </button>

      <!-- Actions -->
      <div class="flex justify-between items-center px-6 pb-4">
        <!-- Additional actions can be added here if needed -->
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
