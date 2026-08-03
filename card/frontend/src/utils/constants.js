/**
 * 前端常量
 * 与后端 GameConstants 保持一致
 */
export const BOARD_SLOTS = 4
export const INITIAL_HAND_SIZE = 5
export const MAX_LEGENDARY_IN_DECK = 3
export const MAX_ITEMS = 2
export const PVP_TURN_TIMEOUT = 30
export const MAX_BATTLE_RECORDS = 20
export const UNIQUE_TAG_LENGTH = 6

export const BATTLE_MODES = {
  PVE: 'PVE',
  PVP: 'PVP'
}

export const DRAW_TYPES = {
  SQUIRREL: 'SQUIRREL',
  DECK: 'DECK'
}

export const TURN_PHASES = {
  DRAW: 'DRAW',
  SELECT_CARD: 'SELECT_CARD',
  SACRIFICE: 'SACRIFICE',
  PLAY_CARD: 'PLAY_CARD',
  END_TURN: 'END_TURN',
  AUTO_ATTACK: 'AUTO_ATTACK'
}
