package com.flyhigh.reservation.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
@ActiveProfiles("test")
public class TruncateDatabaseService {

    private static Logger log = LoggerFactory.getLogger(TruncateDatabaseService.class);
    @Autowired
    protected DataSource dataSource;

    @Autowired
    private EntityManager entityManager;

    private ResultSet tableList;
    private Connection connection;

    @Transactional
    public void resetDb(boolean truncate, List<String> tables) throws SQLException {

        List<String> tableNames = new ArrayList<>(tables == null ? emptyList() : tables);
        connection = dataSource.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        tableList = metaData.getTables(null, null, null, new String[]{"TABLE"});
        while (tableList.next()) {
            String tableName = tableList.getString("TABLE_NAME");
            tableNames.add(tableName);
        }
        tableNames.remove("flyway_schema_history");


        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        tableNames.stream()
                  .filter(tableName -> !tableName.endsWith("_view"))
                  .collect(Collectors.toList())
                  .forEach(
                          tableName -> {
                              if (truncate) {
                                  entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
                              }
                          }
                  );
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();

    }

    public void closeResource() {
        try {
            tableList.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
