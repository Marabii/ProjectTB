<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'
import { serverURL } from '@/utilis/constants'

// Reactive form fields
const title = ref('')
const description = ref('')
const price = ref<number | null>(null)

// File inputs
const documents = ref<FileList | null>(null)
const demoFile = ref<File | null>(null)

/**
 * Handle multiple file selection for "documents"
 */
const handleDocuments = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files) {
    documents.value = target.files
  }
}

/**
 * Handle single file selection for "demoFile"
 */
const handleDemoFile = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files?.[0]) {
    demoFile.value = target.files[0]
  }
}

/**
 * Upload note via FormData to the /protected/notes/upload endpoint
 */
const uploadNote = async () => {
  if (!documents.value || !demoFile.value) {
    alert('Please select both documents and a demo file.')
    return
  }

  try {
    const formData = new FormData()
    formData.append('title', title.value)
    formData.append('description', description.value)
    formData.append('price', price.value?.toString() ?? '0')

    // Append demoFile (single file)
    formData.append('demoFile', demoFile.value)

    // Append documents (multiple files)
    for (let i = 0; i < documents.value.length; i++) {
      formData.append('documents', documents.value[i])
    }

    // Perform the POST request
    const response = await axios.post(`${serverURL}/protected/notes/upload`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })

    console.log('Note uploaded successfully:', response.data)
    alert('Note uploaded successfully!')
    // Reset form after successful upload (optional)
    resetForm()
  } catch (error) {
    console.error('Error uploading note:', error)
    alert('An error occurred while uploading the note.')
  }
}

/**
 * Reset form fields after successful upload
 */
const resetForm = () => {
  title.value = ''
  description.value = ''
  price.value = null
  documents.value = null
  demoFile.value = null
}
</script>

<template>
  <!-- Outer container -->
  <div class="flex min-h-screen items-center justify-center">
    <!-- White card container -->
    <div class="bg-white text-black w-full max-w-md p-8 rounded-lg shadow-lg space-y-6">
      <h1 class="text-2xl font-bold">Upload a New Note</h1>

      <!-- Upload Form -->
      <form @submit.prevent="uploadNote" class="space-y-4">
        <!-- Title -->
        <div>
          <label for="title" class="block font-medium mb-1"> Title </label>
          <input
            id="title"
            v-model="title"
            type="text"
            required
            class="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:border-gray-500"
          />
        </div>

        <!-- Description -->
        <div>
          <label for="description" class="block font-medium mb-1"> Description </label>
          <textarea
            id="description"
            v-model="description"
            rows="3"
            required
            class="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:border-gray-500"
          ></textarea>
        </div>

        <!-- Price -->
        <div>
          <label for="price" class="block font-medium mb-1"> Price </label>
          <input
            id="price"
            v-model.number="price"
            type="number"
            required
            class="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:border-gray-500"
          />
        </div>

        <!-- Multiple Documents -->
        <div>
          <label for="documents" class="block font-medium mb-1"> Documents (Multiple) </label>
          <input
            id="documents"
            type="file"
            accept=".pdf,.docx,.xlsx,.txt"
            multiple
            @change="handleDocuments"
            required
            class="block w-full text-sm text-slate-500 file:mr-4 file:py-2 file:px-4 file:rounded file:border-0 file:text-sm file:font-semibold file:bg-gray-200 file:text-gray-700 hover:file:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-300"
          />
        </div>

        <!-- Single Demo File -->
        <div>
          <label for="demoFile" class="block font-medium mb-1"> Demo File (Single) </label>
          <input
            id="demoFile"
            type="file"
            accept=".pdf,.docx,.xlsx,.txt"
            @change="handleDemoFile"
            required
            class="block w-full text-sm text-slate-500 file:mr-4 file:py-2 file:px-4 file:rounded file:border-0 file:text-sm file:font-semibold file:bg-gray-200 file:text-gray-700 hover:file:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-300"
          />
        </div>

        <!-- Submit Button -->
        <button
          type="submit"
          class="bg-black w-full text-white px-4 py-2 rounded-md hover:bg-white hover:text-black border hover:border-black transition-colors font-semibold"
        >
          Upload Note
        </button>
      </form>
    </div>
  </div>
</template>
