package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.sql.*;
import java.util.*;

@Service
public class MySQLService {

    private final String url = "jdbc:mysql://localhost:3306/demo";
    private final String user = "root";
    private final String password = "sifre";

    /**
     * Verilen SQL sorgusunu çalıştırır ve sonucu List<Map> olarak döner
     */
    public List<Map<String, Object>> fetchData(String sqlQuery) {
        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlQuery)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }
}
