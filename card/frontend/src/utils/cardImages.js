/**
 * 卡牌名称 → 图片文件映射
 * 图片存放在 src/assets/images/ 目录下
 */

// 使用 import.meta.glob 批量导入所有图片（Vite 特性）
const imageModules = import.meta.glob('../assets/images/*.{png,jpg,jpeg}', { eager: true })

// 构建 文件名 → URL 的映射表
const fileNameToUrl = {}
for (const [path, mod] of Object.entries(imageModules)) {
  const fileName = path.split('/').pop().toLowerCase()
  fileNameToUrl[fileName] = mod.default || mod
}

// 卡牌中文名 → 图片文件名 映射表
const cardNameToFile = {
  // 狼族
  '狼': 'wolf.png',
  '狼崽': 'wolf cub.png',
  '冰原狼': 'alpha.png',
  '郊狼': 'coyote.png',
  '寻血猎犬': 'blod hound.png',

  // 鹿族
  '麋鹿': 'elk.png',
  '小麋鹿': 'elk fawn.png',
  '雄麋鹿': 'moose buck.png',
  '叉角羚': 'prong horn.png',
  '黑山羊': 'black goat.png',
  '野牛': 'wild bull.png',
  '红鹿': 'red hart.png',
  '13号孩子': 'child 13.png',

  // 虫族
  '蜜蜂': 'bee.jpg',
  '蜂巢': 'bee nest.jpg',
  '环形虫': 'ringworm.png',
  '蟑螂': 'cockroach.png',
  '螳螂': 'mantis.png',
  '螳螂王': 'mantis god.png',
  '蚂蚁': 'worker ant.png',
  '蚁后': 'ant queen.png',
  '尸蛆': 'corpse maggots.png',
  '奇怪幼虫': 'strange larva.png',
  '触手': 'tantacle.png',
  '大触手': 'big tantacle.png',

  // 爬行族
  '牛蛙': 'bullfrog.png',
  '壁虎': 'geck.png',
  '蝰蛇': 'adder.png',
  '鳄龟': 'river snnaper.png',
  '石龙子': 'skink.png',
  '衔尾蛇': 'ouroborus.png',
  '蝌蚪': 'tadpole.png',
  '响尾蛇': 'rattler.png',

  // 鸟类
  '胡鹫': 'turkey vulture.png',
  '翠鸟': 'king fisher.png',
  '喜鹊': 'magpie.png',
  '麻雀': 'sparrow.png',
  '渡鸦': 'raven.png',
  '渡鸦蛋': 'raven egg.png',

  // 水生
  '大白鲨': 'graet white.png',
  '水獭': 'river otter.png',

  // 通用/无种族
  '白鼬': 'stoat.png',
  '猫': 'cat.png',
  '鼹鼠': 'mole.png',
  '灰熊': 'grizzly.png',
  '臭鼬': 'skunk.png',
  '猪妖': 'the daus.png',
  '鼹鼠人': 'mole man.png',
  '融合兽': 'amalgam.png',
  '负鼠': 'opossum.png',
  '蝙蝠': 'bat.png',
  '豪猪': 'porcupine.png',
  '鼠王': 'rat king.png',
  '野人': 'urayula.png',
  '浣熊': 'raccoon.png',
  '河狸': 'beaver.png',
  '阿米巴原虫': 'amoeba.png',
  '田鼠': 'field mice.png',
  '林鼠': 'pack rat.png',

  // 衍生/特殊
  '松鼠': 'squirrel.png',
  '兔子': 'rabbit.png',
  '兔子窝': 'warren.png',
  '尾巴': 'tail.png',
  '铃铛': 'chime.png',
}

/**
 * 根据卡牌名称获取图片 URL
 * @param {string} cardName 卡牌中文名
 * @returns {string|null} 图片 URL，无图片时返回 null
 */
export function getCardImage(cardName) {
  const fileName = cardNameToFile[cardName]
  if (!fileName) return null
  return fileNameToUrl[fileName.toLowerCase()] || null
}

/**
 * 检查卡牌是否有图片
 * @param {string} cardName 卡牌中文名
 * @returns {boolean}
 */
export function hasCardImage(cardName) {
  return getCardImage(cardName) !== null
}
