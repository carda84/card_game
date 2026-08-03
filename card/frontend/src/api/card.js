import request from './request'

export function getAllCards() {
  return request.get('/cards')
}

export function getCardById(id) {
  return request.get(`/cards/${id}`)
}

export function getDeckableCards() {
  return request.get('/cards/deckable')
}

export function getCardsByRace(race) {
  return request.get(`/cards/race/${race}`)
}

export function getCardsBySigil(sigil) {
  return request.get(`/cards/sigil/${sigil}`)
}
