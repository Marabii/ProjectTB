<script setup lang="ts">
import type { NoteDTO } from '@/interfaces/NotesInterface'
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { serverURL } from '@/utilis/constants'
import DocumentCard from '@/components/PersonalNotes/DocumentCard.vue'

const notesData = ref<NoteDTO[]>([])
const isLoading = ref(false)

const loadNotesData = async () => {
  try {
    isLoading.value = true
    const response = await axios.get(`${serverURL}/api/protected/notes`, { withCredentials: true })
    notesData.value = response.data
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
  <main class="p-8 bg-black text-white min-h-screen">
    <section class="flex flex-col items-center gap-6">
      <template v-if="isLoading">
        <p class="text-lg font-medium animate-pulse">Loading your documents...</p>
      </template>
      <template v-else>
        <template v-if="notesData.length > 0">
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 w-full">
            <DocumentCard v-for="note in notesData" :key="note.id" :noteFile="note" />
          </div>
        </template>
        <template v-else>
          <div class="text-center mt-20">
            <p class="text-xl font-semibold">You haven't uploaded any documents yet.</p>
            <p class="mt-4">
              Visit
              <a href="/sell-files" class="text-white underline hover:text-gray-300">
                /sell-files
              </a>
              to upload and start selling your files.
            </p>
          </div>
        </template>
      </template>
    </section>
  </main>
</template>
