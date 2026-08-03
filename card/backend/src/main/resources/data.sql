-- ============================================================
-- 卡牌数据初始化 (data.sql)
-- 共 74 张卡牌模板，基于 card_data.xlsx
--
-- 列说明：
--   name              名称
--   attack            攻击力（NULL = 特殊，由游戏逻辑计算）
--   is_special_attack 是否为特殊攻击力
--   health            血量
--   blood_cost        血献祭数
--   bone_cost         骨头献祭数
--   sigils            印记（逗号分隔中文名称，NULL = 无）
--   races             种族（逗号分隔中文名称，NULL = 无）
--   max_deck_count    牌组最大数量（0 = 不可选入初始牌组）
--   is_legendary      是否传奇卡
--   can_shuffle       死亡后是否进入洗牌堆（FALSE = 永久死亡）
--   can_sacrifice     是否可被献祭
--   price             商店价格
-- ============================================================

-- ===== 狼族 =====
INSERT IGNORE INTO cards (name, attack, is_special_attack, health, blood_cost, bone_cost, sigils, races, max_deck_count, is_legendary, can_shuffle, can_sacrifice, price) VALUES
('狼', 3, FALSE, 2, 2, 0, NULL, '狼', 3, FALSE, TRUE, TRUE, 8),
('狼崽', 1, FALSE, 1, 1, 0, '幼雏', '狼', 2, FALSE, TRUE, TRUE, 3),
('冰原狼', 2, FALSE, 5, 3, 0, '双重打击', '狼', 2, FALSE, TRUE, TRUE, 12),
('冰原狼幼崽', 1, FALSE, 1, 1, 0, '幼雏,掘骨', '狼', 2, FALSE, TRUE, TRUE, 5),
('头狼', 1, FALSE, 2, 0, 4, '长老', '狼', 2, FALSE, TRUE, TRUE, 10),
('郊狼', 2, FALSE, 1, 0, 4, NULL, '狼', 2, FALSE, TRUE, TRUE, 6),
('寻血猎犬', 2, FALSE, 3, 2, 0, '守护者', '狼', 2, FALSE, TRUE, TRUE, 10);

-- ===== 鹿族 =====
INSERT IGNORE INTO cards (name, attack, is_special_attack, health, blood_cost, bone_cost, sigils, races, max_deck_count, is_legendary, can_shuffle, can_sacrifice, price) VALUES
('麋鹿', 2, FALSE, 4, 2, 0, '移动', '鹿', 3, FALSE, TRUE, TRUE, 7),
('小麋鹿', 1, FALSE, 1, 1, 0, '幼雏', '鹿', 2, FALSE, TRUE, TRUE, 3),
('雄麋鹿', 3, FALSE, 7, 3, 0, '推动', '鹿', 2, FALSE, TRUE, TRUE, 14),
('叉角羚', 1, FALSE, 3, 2, 0, '分散打击,移动', '鹿', 3, FALSE, TRUE, TRUE, 8),
('黑山羊', 0, FALSE, 1, 1, 0, '优质祭品', '鹿', 1, FALSE, TRUE, TRUE, 5),
('野牛', 2, FALSE, 3, 2, 0, '推动', '鹿', 2, FALSE, TRUE, TRUE, 8),
('红鹿', NULL, TRUE, 2, 2, 0, '移动', '鹿', 1, TRUE, TRUE, TRUE, 15),
('13号孩子', 0, FALSE, 3, 1, 0, '生生不息', '鹿', 1, TRUE, TRUE, TRUE, 12);

-- ===== 虫族 =====
INSERT IGNORE INTO cards (name, attack, is_special_attack, health, blood_cost, bone_cost, sigils, races, max_deck_count, is_legendary, can_shuffle, can_sacrifice, price) VALUES
('蜜蜂', 1, FALSE, 1, 0, 0, '空袭', '虫', 0, FALSE, FALSE, TRUE, 0),
('蜂巢', 0, FALSE, 2, 1, 0, '蜜蜂', '虫', 2, FALSE, TRUE, TRUE, 5),
('环形虫', 0, FALSE, 1, 1, 0, NULL, '虫', 4, FALSE, TRUE, TRUE, 2),
('蟑螂', 1, FALSE, 1, 0, 4, '不死', '虫', 3, FALSE, TRUE, TRUE, 6),
('螳螂', 1, FALSE, 1, 1, 0, '分散打击', '虫', 3, FALSE, TRUE, TRUE, 5),
('螳螂王', 1, FALSE, 1, 1, 0, '三分打击', '虫', 1, TRUE, TRUE, TRUE, 15),
('蚂蚁', NULL, TRUE, 2, 1, 0, NULL, '虫', 2, FALSE, TRUE, TRUE, 4),
('飞蚂蚁', NULL, TRUE, 1, 1, 0, '空袭', '虫', 2, FALSE, TRUE, TRUE, 5),
('蚁后', NULL, TRUE, 3, 2, 0, '蚁后', '虫', 2, FALSE, TRUE, TRUE, 8),
('尸蛆', 1, FALSE, 2, 0, 5, '占位', '虫', 3, FALSE, TRUE, TRUE, 7),
('奇怪幼虫', 0, FALSE, 3, 1, 0, '幼雏', '虫', 1, TRUE, TRUE, TRUE, 10),
('触手', NULL, TRUE, 3, 1, 0, NULL, NULL, 2, FALSE, TRUE, TRUE, 6),
('大触手', NULL, TRUE, 3, 2, 0, NULL, NULL, 2, FALSE, TRUE, TRUE, 8),
('面包虫', 0, FALSE, 2, 0, 2, '继承', '虫', 2, FALSE, TRUE, TRUE, 5);

-- ===== 爬行族 =====
INSERT IGNORE INTO cards (name, attack, is_special_attack, health, blood_cost, bone_cost, sigils, races, max_deck_count, is_legendary, can_shuffle, can_sacrifice, price) VALUES
('牛蛙', 1, FALSE, 2, 1, 0, '高跳', '爬行', 3, FALSE, TRUE, TRUE, 4),
('壁虎', 1, FALSE, 1, 0, 0, NULL, '爬行', 1, FALSE, TRUE, TRUE, 2),
('蝰蛇', 1, FALSE, 1, 2, 0, '剧毒', '爬行', 2, FALSE, TRUE, TRUE, 8),
('鳄龟', 1, FALSE, 6, 2, 0, NULL, '爬行', 3, FALSE, TRUE, TRUE, 8),
('东方泥龟', 2, FALSE, 2, 2, 0, '盾', '爬行', 3, FALSE, TRUE, TRUE, 7),
('石龙子', 1, FALSE, 2, 1, 0, '断尾', '爬行', 2, FALSE, TRUE, TRUE, 5),
('衔尾蛇', 1, FALSE, 1, 2, 0, '不死', '爬行', 1, TRUE, TRUE, TRUE, 15),
('蝌蚪', 0, FALSE, 1, 0, 0, '水袭,幼雏', '爬行', 1, FALSE, TRUE, TRUE, 3),
('响尾蛇', 3, FALSE, 1, 0, 6, NULL, '爬行', 2, FALSE, TRUE, TRUE, 8);

-- ===== 鸟类 =====
INSERT IGNORE INTO cards (name, attack, is_special_attack, health, blood_cost, bone_cost, sigils, races, max_deck_count, is_legendary, can_shuffle, can_sacrifice, price) VALUES
('胡鹫', NULL, TRUE, 4, 3, 0, '空袭', '鸟', 1, FALSE, TRUE, TRUE, 12),
('翠鸟', 1, FALSE, 1, 1, 0, '空袭,水袭', '鸟', 3, FALSE, TRUE, TRUE, 6),
('布谷鸟', 1, FALSE, 1, 1, 0, '空袭,下蛋', '鸟', 1, FALSE, TRUE, TRUE, 7),
('喜鹊', 1, FALSE, 1, 2, 0, '空袭,搜寻', '鸟', 1, FALSE, TRUE, TRUE, 8),
('秃鹫', 3, FALSE, 3, 0, 8, '空袭', '鸟', 2, FALSE, TRUE, TRUE, 10),
('麻雀', 1, FALSE, 2, 1, 0, '空袭', '鸟', 2, FALSE, TRUE, TRUE, 4),
('渡鸦', 2, FALSE, 3, 2, 0, '空袭', '鸟', 3, FALSE, TRUE, TRUE, 7),
('渡鸦蛋', 0, FALSE, 2, 1, 0, '幼雏', NULL, 2, FALSE, TRUE, TRUE, 3);

-- ===== 水生 =====
INSERT IGNORE INTO cards (name, attack, is_special_attack, health, blood_cost, bone_cost, sigils, races, max_deck_count, is_legendary, can_shuffle, can_sacrifice, price) VALUES
('大白鲨', 4, FALSE, 2, 3, 0, '水袭', NULL, 2, FALSE, TRUE, TRUE, 12),
('水獭', 1, FALSE, 1, 1, 0, '水袭', NULL, 3, FALSE, TRUE, TRUE, 4);

-- ===== 无种族/通用 =====
INSERT IGNORE INTO cards (name, attack, is_special_attack, health, blood_cost, bone_cost, sigils, races, max_deck_count, is_legendary, can_shuffle, can_sacrifice, price) VALUES
('白鼬', 1, FALSE, 2, 1, 0, NULL, NULL, 3, FALSE, TRUE, TRUE, 3),
('猫', 0, FALSE, 1, 1, 0, '生生不息', NULL, 1, FALSE, TRUE, TRUE, 4),
('鼹鼠', 0, FALSE, 4, 1, 0, '挖掘者', NULL, 2, FALSE, TRUE, TRUE, 5),
('灰熊', 4, FALSE, 6, 3, 0, NULL, NULL, 3, FALSE, TRUE, TRUE, 14),
('臭鼬', 0, FALSE, 3, 1, 0, '臭臭', NULL, 3, FALSE, TRUE, TRUE, 4),
('猪妖', 2, FALSE, 2, 2, 0, '铃铛', NULL, 1, TRUE, TRUE, TRUE, 15),
('鼹鼠人', 0, FALSE, 6, 1, 0, '挖掘者,高跳', NULL, 1, TRUE, TRUE, TRUE, 15),
('融合兽', 3, FALSE, 3, 2, 0, NULL, '狼,虫,鹿,爬行,鸟', 1, TRUE, TRUE, TRUE, 18),
('负鼠', 1, FALSE, 1, 0, 2, NULL, NULL, 2, FALSE, TRUE, TRUE, 3),
('蝙蝠', 2, FALSE, 1, 0, 4, '空袭', NULL, 2, FALSE, TRUE, TRUE, 6),
('豪猪', 1, FALSE, 2, 1, 0, '反击', NULL, 2, FALSE, TRUE, TRUE, 5),
('鼠王', 2, FALSE, 1, 2, 0, '骨王', NULL, 1, FALSE, TRUE, TRUE, 8),
('野人', 7, FALSE, 7, 4, 0, NULL, NULL, 1, TRUE, TRUE, TRUE, 20),
('浣熊', 1, FALSE, 1, 1, 0, '拾荒', NULL, 2, FALSE, TRUE, TRUE, 5),
('河狸', 1, FALSE, 3, 2, 0, '堤坝', NULL, 1, TRUE, TRUE, TRUE, 10),
('阿米巴原虫', 1, FALSE, 2, 0, 2, '随机', NULL, 1, TRUE, TRUE, TRUE, 12),
('田鼠', 2, FALSE, 2, 2, 0, '丰产', NULL, 1, FALSE, TRUE, TRUE, 8),
('林鼠', 2, FALSE, 2, 2, 0, '道具', NULL, 1, TRUE, TRUE, TRUE, 12);

-- ===== 衍生/特殊卡牌（不可选入初始牌组） =====
INSERT IGNORE INTO cards (name, attack, is_special_attack, health, blood_cost, bone_cost, sigils, races, max_deck_count, is_legendary, can_shuffle, can_sacrifice, price) VALUES
('松鼠', 0, FALSE, 1, 0, 0, NULL, NULL, 0, FALSE, FALSE, TRUE, 0),
('兔子', 0, FALSE, 1, 0, 0, NULL, NULL, 0, FALSE, FALSE, TRUE, 0),
('兔子窝', 0, FALSE, 2, 1, 0, '兔子', NULL, 2, FALSE, TRUE, TRUE, 3),
('尾巴', 0, FALSE, 2, 0, 0, NULL, NULL, 0, FALSE, FALSE, FALSE, 0),
('破碎的蛋', 0, FALSE, 1, 0, 0, NULL, NULL, 0, FALSE, FALSE, FALSE, 0),
('铃铛', 0, FALSE, 2, 0, 0, NULL, NULL, 0, FALSE, FALSE, FALSE, 0),
('堤坝', 0, FALSE, 2, 0, 0, NULL, NULL, 0, FALSE, FALSE, FALSE, 0);
