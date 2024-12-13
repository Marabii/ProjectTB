<script setup lang="ts">
import Header from '@/components/Header.vue'
import VuePdfEmbed from 'vue-pdf-embed'
import 'vue-pdf-embed/dist/styles/annotationLayer.css'
import 'vue-pdf-embed/dist/styles/textLayer.css'

const pdfUrl = 'https://storage.googleapis.com/projettb/1/demo_job%20ad.pdf'
</script>

<template>
  <div class="grid">
    <Header></Header>
    <input
      class="w-full my-16 justify-self-center indent-3 max-w-[900px] h-[60px] border border-secondary rounded-xl"
      type="text"
      placeholder="Recherchez des fiches de cours, fiches de révisions ou résumés de livres"
    />

    <VuePdfEmbed annotation-layer text-layer :source="pdfUrl" />
  </div>
</template>
