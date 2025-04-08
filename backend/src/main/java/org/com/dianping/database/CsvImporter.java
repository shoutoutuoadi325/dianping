package org.com.dianping.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileInputStream;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import net.sourceforge.pinyin4j.PinyinHelper;

public class CsvImporter {

    // 数据库连接信息
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;

    // CSV 文件路径
    private static final String CSV_FILE_PATH = "backend\\src\\main\\java\\org\\com\\dianping\\database\\merchants.csv";

    public static void main(String[] args) {
        loadDatabaseConfig();

        String insertSql = "INSERT INTO merchant (merchant_name, category, rating, address, avg_price, telephone, business_hours, description, cover_url, photo_urls, merchant_name_pinyin) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // 创建表 SQL
        String createTableSql = "CREATE TABLE IF NOT EXISTS merchant ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "merchant_name VARCHAR(255) NOT NULL, "
                + "category VARCHAR(255) NOT NULL, "
                + "rating FLOAT NOT NULL, "
                + "address VARCHAR(255) NOT NULL, "
                + "avg_price FLOAT NOT NULL, "
                + "telephone VARCHAR(50) NOT NULL, "
                + "business_hours VARCHAR(50) NOT NULL, "
                + "description VARCHAR(1000) NOT NULL, "
                + "cover_url VARCHAR(255) NOT NULL, "
                + "photo_urls VARCHAR(255) NOT NULL, "
                + "merchant_name_pinyin VARCHAR(255)"
                + ")";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_PATH))) {

            // 创建 merchant 表（如果不存在）
            stmt.execute(createTableSql);
            System.out.println("表 merchant 已创建/存在。");

            String line;
            // 跳过表头
            br.readLine();

            // 开启事务
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                while ((line = br.readLine()) != null) {
                    // 简单拆分 CSV 字符串，如需处理复杂情况建议使用 CSV 解析库
                    String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    if (parts.length < 10) {
                        System.out.println("跳过错误行: " + line);
                        continue;
                    }
                    // 去除可能存在的引号
                    for (int i = 0; i < parts.length; i++) {
                        parts[i] = parts[i].replaceAll("^\"|\"$", "");
                    }
                    if (!setPreparedStatementValues(pstmt, parts, line)) {
                        continue;
                    }
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            conn.commit();
            System.out.println("数据导入完成！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadDatabaseConfig() {
        try (FileInputStream fis = new FileInputStream("backend\\src\\main\\java\\org\\com\\dianping\\database\\config.properties")) {
            Properties props = new Properties();
            props.load(fis);
            DB_URL = props.getProperty("DB_URL");
            DB_USER = props.getProperty("DB_USER");
            DB_PASSWORD = props.getProperty("DB_PASSWORD");

            if (DB_URL == null || DB_USER == null || DB_PASSWORD == null) {
                throw new IllegalArgumentException("配置文件中缺少数据库连接信息");
            }
        } catch (Exception e) {
            throw new RuntimeException("加载数据库配置失败", e);
        }
    }

    static String getPinyin(String chinese) {
        StringBuilder pinyin = new StringBuilder();
        for (char c : chinese.toCharArray()) {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
            if (pinyinArray != null) {
                // 去掉音调标记
                pinyin.append(pinyinArray[0].replaceAll("\\d", ""));
            } else {
                pinyin.append(c);
            }
        }
        return pinyin.toString();
    }

    private static boolean setPreparedStatementValues(PreparedStatement pstmt, String[] parts, String line) throws SQLException {
        try {
            pstmt.setString(1, parts[0]);
            pstmt.setString(2, parts[1]);
            pstmt.setFloat(3, Float.parseFloat(parts[2]));
            pstmt.setString(4, parts[3]);
            pstmt.setFloat(5, Float.parseFloat(parts[4]));
            pstmt.setString(6, parts[5]);
            pstmt.setString(7, parts[6]);
            pstmt.setString(8, parts[7]);
            pstmt.setString(9, parts[8]);
            pstmt.setString(10, parts[9]);
            pstmt.setString(11, getPinyin(parts[0])); // 商家名称拼音
            return true;
        } catch (NumberFormatException e) {
            System.out.println("跳过格式错误行: " + line);
            return false;
        }
    }
}
