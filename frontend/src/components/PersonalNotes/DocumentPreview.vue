<!-- DocumentPreview.vue -->
<script lang="ts" setup>
import type { NoteDTO } from '@/interfaces/NotesInterface'
import { serverURL } from '@/utilis/constants'
import axios from 'axios'
import { ref, type PropType } from 'vue'
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
      <div class="w-full h-96">
        <VuePdfEmbed
          :source="noteFile.demoFile.fileUrl"
          annotation-layer
          text-layer
          class="h-full w-full object-cover"
          @loading-failed="loadingFailed"
        />
      </div>

      <!-- Document Details -->
      <div class="p-6 bg-white border-t flex flex-col space-y-4">
        <div>
          <h2 class="text-2xl font-semibold mb-2">{{ noteFile.title }}</h2>
          <p class="text-gray-700 mb-1">
            Price: <span class="font-bold">{{ noteFile.price }}€</span>
          </p>
          <p class="text-gray-700">
            Author: <span class="font-bold">{{ noteFile.owner.username }}</span>
          </p>
        </div>

        <!-- Digital/Physical Indication -->
        <div>
          <p>
            Status:
            <span
              :class="noteFile.isDigital ? 'text-green-600' : 'text-blue-600'"
              class="font-semibold"
            >
              {{ noteFile.isDigital ? 'Digital' : 'Physical' }}
            </span>
          </p>
        </div>

        <!-- Availability Indication and Deposit Button -->
        <div v-if="!noteFile.isDigital" class="mt-2">
          <p>
            Availability:
            <span
              :class="noteFile.isAvailable ? 'text-green-600' : 'text-red-600'"
              class="font-semibold"
            >
              {{ noteFile.isAvailable ? 'Available' : 'Not Available' }}
            </span>
          </p>

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
