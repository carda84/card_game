import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getAllCharacters } from '../api/character'

export const useCharacterStore = defineStore('character', () => {
  const characters = ref([])
  const selectedCharacter = ref(null)
  const loading = ref(false)

  async function fetchCharacters() {
    loading.value = true
    try { characters.value = await getAllCharacters() } finally { loading.value = false }
  }

  function selectCharacter(character) {
    selectedCharacter.value = character
  }

  return { characters, selectedCharacter, loading, fetchCharacters, selectCharacter }
})
