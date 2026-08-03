import request from './request'

export function getAllCharacters() {
  return request.get('/characters')
}

export function getCharacterById(id) {
  return request.get(`/characters/${id}`)
}
