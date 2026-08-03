import request from './request'

export function createDeck(data) {
  return request.post('/decks', data)
}

export function getUserDecks() {
  return request.get('/decks')
}

export function getDeckDetail(deckId) {
  return request.get(`/decks/${deckId}`)
}

export function addCardToDeck(data) {
  return request.post('/decks/add-card', data)
}

export function removeCardFromDeck(data) {
  return request.post('/decks/remove-card', data)
}

export function deleteDeck(deckId) {
  return request.delete(`/decks/${deckId}`)
}

export function getOwnedCards() {
  return request.get('/decks/owned-cards')
}

export function renameDeck(data) {
  return request.post('/decks/rename', data)
}
