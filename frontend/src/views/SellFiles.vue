<script setup lang="ts">
import { ref, computed } from 'vue'
import axios from 'axios'
import { serverURL } from '@/utilis/constants'

// =====================
//     CONSTANTS
// =====================
const MAX_SIZE_BYTES = 50 * 1024 * 1024 // 50MB

// =====================
//  REACTIVE VARIABLES
// =====================
const title = ref('')
const description = ref('')
const price = ref<number | null>(null)
const isDigital = ref(false)

// File inputs
const documents = ref<FileList | null>(null)
const demoFile = ref<File | null>(null)

// =====================
//  COMPUTED PROPERTIES
// =====================
/**
 * totalSize (in bytes) of all selected files:
 *   - Demo file
 *   - Documents (if isDigital is true)
 */
const totalSize = computed(() => {
  let size = 0
  if (demoFile.value) {
    size += demoFile.value.size
  }
  if (isDigital.value && documents.value) {
    for (let i = 0; i < documents.value.length; i++) {
      size += documents.value[i].size
    }
  }
  return size
})

/**
 * Convert bytes to a human-readable MB string
 */
function formatBytesToMB(bytes: number): string {
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB'
}

// =====================
//    FILE HANDLERS
// =====================
function handleDocuments(event: Event) {
  const target = event.target as HTMLInputElement
  if (!target.files) return

  // Check each file size
  for (let i = 0; i < target.files.length; i++) {
    const file = target.files[i]
    if (file.size > MAX_SIZE_BYTES) {
      alert(`File "${file.name}" exceeds the 50MB limit.`)
      target.value = '' // Reset the input
      documents.value = null
      return
    }
  }

  // Check total size (demoFile + all selected documents)
  let currentTotal = demoFile.value ? demoFile.value.size : 0
  for (let i = 0; i < target.files.length; i++) {
    currentTotal += target.files[i].size
  }
  if (currentTotal > MAX_SIZE_BYTES) {
    alert('Total upload size exceeds the 50MB limit.')
    target.value = ''
    documents.value = null
    return
  }

  // If all checks pass, set the documents
  documents.value = target.files
}

function handleDemoFile(event: Event) {
  const target = event.target as HTMLInputElement
  if (!target.files?.[0]) return

  const file = target.files[0]

  // Check individual file size
  if (file.size > MAX_SIZE_BYTES) {
    alert(`File "${file.name}" exceeds the 50MB limit.`)
    target.value = ''
    demoFile.value = null
    return
  }

  // Check total size if documents are present (isDigital)
  let currentTotal = file.size
  if (isDigital.value && documents.value) {
    for (let i = 0; i < documents.value.length; i++) {
      currentTotal += documents.value[i].size
    }
  }
  if (currentTotal > MAX_SIZE_BYTES) {
    alert('Total upload size exceeds the 50MB limit.')
    target.value = ''
    demoFile.value = null
    return
  }

  // If all checks pass, set the demoFile
  demoFile.value = file
}

// =====================
//    UPLOAD LOGIC
// =====================
async function uploadNote() {
  // Basic checks
  if (!demoFile.value) {
    alert('Please select a demo file.')
    return
  }
  if (isDigital.value && !documents.value) {
    alert('Please select the required documents.')
    return
  }

  // Final check for total size
  if (totalSize.value > MAX_SIZE_BYTES) {
    alert('Total upload size exceeds the 50MB limit.')
    return
  }

  // Prepare FormData
  try {
    const formData = new FormData()
    formData.append('title', title.value)
    formData.append('description', description.value)
    formData.append('price', price.value?.toString() ?? '0')
    formData.append('isDigital', isDigital.value.toString())

    // Append demoFile (single file)
    formData.append('demoFile', demoFile.value)

    // Append documents (multiple files) only if isDigital is true
    if (isDigital.value && documents.value) {
      for (let i = 0; i < documents.value.length; i++) {
        formData.append('documents', documents.value[i])
      }
    }

    // Perform the POST request
    const response = await axios.post(`${serverURL}/api/protected/notes/upload`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      withCredentials: true,
    })

    console.log('Note uploaded successfully:', response.data)
    alert('Note uploaded successfully!')
    resetForm()
  } catch (err: unknown) {
    // Use AxiosError type-checking to handle errors gracefully
    if (axios.isAxiosError(err)) {
      if (err.response?.status === 413) {
        alert('An unexpected error occurred: Maximum upload size exceeded.')
      } else {
        alert(`An error occurred: ${err.response?.data?.message ?? err.message}`)
      }
    } else {
      // If it's not an Axios error, fallback to a generic error
      console.error('Error uploading note:', err)
      alert('An unknown error occurred while uploading the note.')
    }
  }
}

// =====================
//    RESET FORM
// =====================
function resetForm() {
  title.value = ''
  description.value = ''
  price.value = null
  isDigital.value = false
  documents.value = null
  demoFile.value = null
}
</script>

<template>
  <!-- Outer container -->
  <div class="flex min-h-screen items-center justify-center bg-gray-100 px-4">
    <!-- White card container -->
    <div class="bg-white text-black w-full max-w-md p-8 rounded-lg shadow-xl space-y-6">
      <h1 class="text-3xl font-extrabold text-center">Upload a New Note</h1>

      <!-- Upload Form -->
      <form @submit.prevent="uploadNote" class="space-y-6">
        <!-- Title -->
        <div>
          <label for="title" class="block text-lg font-medium mb-2">Title</label>
          <input
            id="title"
            v-model="title"
            type="text"
            required
            class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Enter the title of the note"
          />
        </div>

        <!-- Description -->
        <div>
          <label for="description" class="block text-lg font-medium mb-2">Description</label>
          <textarea
            id="description"
            v-model="description"
            rows="4"
            required
            class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Enter a brief description"
          ></textarea>
        </div>

        <!-- Price -->
        <div>
          <label for="price" class="block text-lg font-medium mb-2">Price</label>
          <input
            id="price"
            v-model.number="price"
            type="number"
            min="0"
            required
            class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Enter the price"
          />
        </div>

        <!-- Is Digital Checkbox -->
        <div class="flex items-center">
          <input
            id="isDigital"
            type="checkbox"
            v-model="isDigital"
            class="h-5 w-5 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
          />
          <label for="isDigital" class="ml-3 block text-lg font-medium">
            Is this a Digital Product?
          </label>
        </div>

        <!-- Multiple Documents (conditionally rendered) -->
        <div v-if="isDigital" class="mt-4">
          <label for="documents" class="block text-lg font-medium mb-2">Documents (Multiple)</label>
          <input
            id="documents"
            type="file"
            accept=".pdf,.docx,.xlsx,.txt"
            multiple
            @change="handleDocuments"
            :required="isDigital"
            class="w-full text-sm text-slate-500 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-blue-100 file:text-blue-700 hover:file:bg-blue-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <!-- Single Demo File -->
        <div>
          <label for="demoFile" class="block text-lg font-medium mb-2">Demo File (Single)</label>
          <input
            id="demoFile"
            type="file"
            accept=".pdf,.docx,.xlsx,.txt"
            @change="handleDemoFile"
            required
            class="w-full text-sm text-slate-500 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-blue-100 file:text-blue-700 hover:file:bg-blue-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <!-- Submit Button -->
        <button
          type="submit"
          class="w-full bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors font-semibold text-lg"
        >
          Upload Note
        </button>

        <!-- Total Size Display (in MB) -->
        <div class="mt-4 text-right">
          <span
            :class="{
              'text-green-600': totalSize <= MAX_SIZE_BYTES,
              'text-red-600': totalSize > MAX_SIZE_BYTES,
            }"
          >
            <!-- Pass totalSize (bytes) directly into formatBytesToMB -->
            Total Size: {{ formatBytesToMB(totalSize) }}
          </span>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
/* Add any additional styles if needed */
</style>
