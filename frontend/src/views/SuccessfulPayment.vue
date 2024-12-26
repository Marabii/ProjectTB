<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()
const goHome = () => router.push({ name: 'home' })
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-900 text-white px-4">
    <div class="max-w-md text-center">
      <!-- Success Icon -->
      <div class="mx-auto mb-6">
        <svg
          class="w-24 h-24 text-green-500 animate-bounce"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M5 13l4 4L19 7"
          />
        </svg>
      </div>

      <!-- Success Message -->
      <h1 class="text-4xl font-bold mb-4">Payment Successful!</h1>
      <p class="text-lg mb-8">
        Thank you for your purchase. Your transaction was completed successfully.
      </p>

      <!-- Action Button -->
      <button
        @click="goHome"
        class="px-6 py-3 bg-white text-gray-900 font-semibold rounded-lg shadow-md hover:bg-gray-200 transition duration-300"
      >
        Go to Homepage
      </button>
    </div>
  </div>
</template>
