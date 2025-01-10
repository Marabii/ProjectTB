<script setup lang="ts">
import type { NoteDTO } from '@/interfaces/NotesInterface'
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { serverURL } from '@/utilis/constants'
import DocumentCard from '@/components/DocumentCard.vue'

const notesData = ref<NoteDTO[]>([])
const isLoading = ref<boolean>(true)

const loadNotesData = async () => {
  try {
    const response = await axios.get(`${serverURL}/api/protected/favourite-documents`, {
      withCredentials: true,
    })
    notesData.value = response.data
    console.log(response.data)
  } catch (error) {
    console.error('Error fetching data:', error)
    alert('Loading documents failed')
  } finally {
    isLoading.value = false
  }
}

onMounted(loadNotesData)
</script>

<template>
  <main class="p-4 mt-10 flex flex-col gap-6 items-center">
    <!-- Show loading message -->
    <p v-if="isLoading" class="text-gray-500 text-lg">Loading documents...</p>

    <!-- Show message if no documents -->
    <p v-else-if="notesData.length === 0" class="text-gray-700 text-xl font-semibold">
      No documents uploaded yet. Please check back later!
    </p>

    <!-- Show document cards if data exists -->
    <section v-else class="flex flex-wrap gap-3 justify-center">
      <DocumentCard v-for="note in notesData" :key="note.id" :noteFile="note" />
    </section>
  </main>
</template>
