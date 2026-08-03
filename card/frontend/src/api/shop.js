import request from './request'

export function getShopItems() {
  return request.get('/shop')
}

export function buyCard(data) {
  return request.post('/shop/buy', data)
}
