-- Insert predefined merchants with all attributes
INSERT INTO merchants (merchant_name, merchant_name_pinyin, category, rating, address, avgPrice, telephone, business_hours, description, cover_url, photo_urls) VALUES
('新发现（五角场万达店）', 'xinfaxian', '中餐', 4.5, '五角场万达广场', 100.0, '1234567890', '10:00-22:00', '家庭聚餐好去处', 'cover1.jpg', 'photo1.jpg;photo2.jpg'),
('茶百道（五角场中心店）', 'chabaidao', '奶茶', 4.0, '五角场中心', 20.0, '0987654321', '09:00-21:00', '奶茶爱好者的天堂', 'cover2.jpg', 'photo3.jpg;photo4.jpg'),
('喜茶（五角场万达店）', 'xicha', '奶茶', 4.2, '五角场万达广场', 25.0, '1122334455', '10:00-22:00', '高品质奶茶', 'cover3.jpg', 'photo5.jpg;photo6.jpg');

-- Insert predefined group-buy packages
INSERT INTO packages (title, price, description, merchant_id, sales) VALUES
('家庭小聚三人餐', 199, '适合家庭聚餐的三人套餐', (SELECT id FROM merchants WHERE name = '新发现（五角场万达店）'), 0),
('招牌甄选三人餐', 209, '精选招牌菜的三人套餐', (SELECT id FROM merchants WHERE name = '新发现（五角场万达店）'), 0),
('葡萄系列3选1', 11, '三种葡萄口味任选其一', (SELECT id FROM merchants WHERE name = '茶百道（五角场中心店）'), 0),
('时令白芭乐2选1', 19, '两种时令白芭乐口味任选其一', (SELECT id FROM merchants WHERE name = '喜茶（五角场万达店）'), 0);