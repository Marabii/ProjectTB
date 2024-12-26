<script setup lang="ts">
import type { NoteDTO } from '@/interfaces/NotesInterface'
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { serverURL } from '@/utilis/constants'
import DocumentCard from '@/components/DocumentCard.vue'

const notesData = ref<NoteDTO[]>([])

const loadNotesData = async () => {
  try {
    const response = await axios.get(`${serverURL}/api/notes`)
    notesData.value = response.data
  } catch (error) {
    console.error('Error fetching data:', error)
    alert('Loading documents failed')
  }
}

onMounted(loadNotesData)
</script>

<template>
  <main class="p-4 mt-10 flex flex-wrap gap-3 shrink-0 grow-0">
    <DocumentCard v-for="note in notesData" :key="note.id" :noteFile="note" />
  </main>
</template>
