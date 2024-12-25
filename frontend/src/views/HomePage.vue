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
  <main class="p-4 mt-10">
    <div class="my-5 relative justify-between flex xl:block">
      <button class="text-white bg-black px-5 py-3 rounded-full">+ Sell Files</button>
      <input
        class="w-full xl:absolute xl:left-1/2 xl:top-1/2 xl:-translate-x-1/2 xl:-translate-y-1/2 indent-3 max-w-[900px] h-[60px] border border-secondary rounded-xl"
        type="text"
        placeholder="Recherchez des fiches de cours, fiches de révisions ou résumés de livres"
      />
    </div>

    <!-- Render DocumentCard for each note -->
    <DocumentCard v-for="note in notesData" :key="note.id" :noteFile="note" />
  </main>
</template>
