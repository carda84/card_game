-- ============================================================
-- 人物数据初始化 (character.sql)
-- 共 4 个人物，基于 character.txt
--
-- 列说明：
--   name                 名称
--   max_hp               血量
--   deck_size            卡组数量（必须严格满足）
--   special_ability_desc 特殊能力描述
--   initial_items        初始道具（逗号分隔，NULL = 无）
--   is_default_char      是否默认免费（TRUE = 注册即可用）
--   price                解锁价格（金币）
-- ============================================================

INSERT IGNORE INTO characters (name, max_hp, deck_size, special_ability_desc, initial_items, is_default_char, price) VALUES
('卡德', 30, 15, '游戏开始时，将3张松鼠牌添加至手牌', '画笔', TRUE, 0),
('莱西', 30, 15, '游戏开始时，将一张随机卡牌添加至手牌，并为其赋予3个随机额外印记（与其原本拥有的不能重复）', NULL, FALSE, 200),
('格里魔拉', 25, 15, '当己方卡牌因受敌方攻击死亡时（非献祭），可以选择将死亡卡牌在本局游戏内移除卡组（不会再次进入抽牌堆），对不能进入抽牌堆的卡牌无效', NULL, FALSE, 300),
('蔓尼菲科', 20, 20, '每己方回合开始前，己方一个随机种族的所有卡牌会获得一个随机印记，持续到下一个己方回合开始', NULL, FALSE, 500);