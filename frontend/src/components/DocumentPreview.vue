<!-- DocumentPreview.vue -->
<script lang="ts" setup>
import type { NoteDTO } from '@/interfaces/NotesInterface'
import { PaymentStatus, type PaymentSessionResponse } from '@/interfaces/Payment'
import { serverURL } from '@/utilis/constants'
import axios from 'axios'
import { defineProps, ref, type PropType } from 'vue'
import VuePdfEmbed from 'vue-pdf-embed'
import 'vue-pdf-embed/dist/styles/annotationLayer.css'
import 'vue-pdf-embed/dist/styles/textLayer.css'
import { useRouter } from 'vue-router'

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

// Handle Payment

const router = useRouter()
const isLoading = ref(false)
const error = ref<string | null>(null)

const handlePayment = async () => {
  try {
    isLoading.value = true

    // Verify user authentication
    await axios.get(`${serverURL}/api/protected/verifyUser`, { withCredentials: true })

    // Create payment session
    const response = await axios.post<PaymentSessionResponse>(
      `${serverURL}/api/protected/v1/stripe/create-payment`,
      { noteId: props.noteFile.id },
      { withCredentials: true },
    )

    // Check for payment failure
    if (response.data.status === PaymentStatus.FAILURE) {
      throw new Error('Payment went wrong')
    }

    // Navigate to the payment session URL
    window.location.href = response.data.data.sessionUrl
  } catch (err: unknown) {
    // Handle specific HTTP errors
    if (axios.isAxiosError(err) && err.response) {
      const status = err.response.status
      if (status === 401 || status === 403) {
        alert('You might have forgotten to log in or you do not have an account.')
      } else {
        alert(`An error occurred: ${err.response.statusText}`)
      }
    } else if (err instanceof Error) {
      // Handle other errors
      error.value = err.message
      alert(`An unexpected error occurred: ${err.message}`)
    } else {
      alert(`An unexpected error occurred`)
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <!-- Overlay -->
  <div class="fixed inset-0 overflow-y-scroll bg-black bg-opacity-70 justify-center flex z-30">
    <!-- Modal Container -->
    <div class="bg-white my-10 rounded-lg shadow-lg relative w-[80%] lg:w-2/3 xl:w-1/2">
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
          @loading-failed="loadingFailed"
        />
      </div>

      <!-- Document Details -->
      <div class="p-6 bg-white border-t flex justify-between">
        <div>
          <h2 class="text-2xl font-semibold mb-2">{{ noteFile.title }}</h2>
          <p class="text-gray-700 mb-1">
            Price: <span class="font-bold">{{ noteFile.price }}€</span>
          </p>
          <p class="text-gray-700">
            Author: <span class="font-bold">{{ noteFile.owner.username }}</span>
          </p>
        </div>
        <div class="flex flex-col space-y-2">
          <button
            @click="handlePayment"
            class="bg-black text-white px-4 py-2 rounded-md hover:bg-white hover:text-black border hover:border-black transition-colors font-semibold"
          >
            Buy
          </button>
          <button
            @click="$emit('close')"
            class="bg-white px-4 py-2 rounded-md text-black hover:text-white hover:bg-black border border-black transition-colors font-semibold"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
