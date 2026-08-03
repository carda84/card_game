import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getUserDecks, createDeck, getDeckDetail, deleteDeck as apiDeleteDeck,
  addCardToDeck as apiAddCard, removeCardFromDeck as apiRemoveCard,
  getOwnedCards as apiGetOwnedCards, renameDeck as apiRenameDeck
} from '../api/deck'

export const useDeckStore = defineStore('deck', () => {
  const decks = ref([])
  const currentDeck = ref(null)
  const ownedCards = ref([])
  const loading = ref(false)

  async function fetchDecks() {
    loading.value = true
    try { decks.value = await getUserDecks() } finally { loading.value = false }
  }

  async function createNewDeck(data) {
    const deck = await createDeck(data)
    await fetchDecks()
    return deck
  }

  async function loadDeckDetail(deckId) {
    currentDeck.value = await getDeckDetail(deckId)
    return currentDeck.value
  }

  async function removeDeck(deckId) {
    await apiDeleteDeck(deckId)
    await fetchDecks()
    if (currentDeck.value?.id === deckId) {
      currentDeck.value = null
    }
  }

  async function addCard(deckId, cardId) {
    await apiAddCard({ deckId, cardId })
    await loadDeckDetail(deckId)
  }

  async function removeCard(deckId, cardId) {
    await apiRemoveCard({ deckId, cardId })
    await loadDeckDetail(deckId)
  }

  async function fetchOwnedCards() {
    ownedCards.value = await apiGetOwnedCards()
  }

  async function rename(deckId, name) {
    const updated = await apiRenameDeck({ deckId, name })
    await fetchDecks()
    if (currentDeck.value?.id === deckId) {
      currentDeck.value = updated
    }
    return updated
  }

  return {
    decks, currentDeck, ownedCards, loading,
    fetchDecks, createNewDeck, loadDeckDetail, removeDeck,
    addCard, removeCard, fetchOwnedCards, rename
  }
})
