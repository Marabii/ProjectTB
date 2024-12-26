<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()
const goHome = () => router.push({ name: 'home' })
const contactSupport = () => (window.location.href = 'mailto:minehamza97@gmail.com')
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-900 text-white px-4">
    <div class="max-w-md text-center">
      <!-- Failure Icon -->
      <div class="mx-auto mb-6">
        <svg
          class="w-24 h-24 text-red-500 animate-pulse"
          xmlns="http://www.w3.org/2000/svg"
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
      </div>

      <!-- Failure Message -->
      <h1 class="text-4xl font-bold mb-4">Payment Failed</h1>
      <p class="text-lg mb-8">
        Oops! Something went wrong with your transaction. Please try again or contact support.
      </p>

      <!-- Action Buttons -->
      <div class="flex justify-center space-x-4">
        <button
          @click="goHome"
          class="px-6 py-3 bg-white text-gray-900 font-semibold rounded-lg shadow-md hover:bg-gray-200 transition duration-300"
        >
          Go to Homepage
        </button>
        <button
          @click="contactSupport"
          class="px-4 py-2 bg-transparent border border-white text-white font-semibold rounded-lg shadow-md hover:bg-gray-700 transition duration-300"
        >
          Contact Support
        </button>
      </div>
    </div>
  </div>
</template>
