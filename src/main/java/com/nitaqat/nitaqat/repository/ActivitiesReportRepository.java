package com.nitaqat.nitaqat.repository;

import com.nitaqat.nitaqat.dto.ActivitiesAnalysisDTO;
import com.nitaqat.nitaqat.dto.ActivitiesReportDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ActivitiesReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public ActivitiesReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<ActivitiesReportDTO> getActivitiesReport(Long activityId , Long userId) {

        String condition = "";

        if (activityId != null) {
            condition += "WHERE a.id = " + activityId + " ";
        }

        if (userId != null) {
            condition += (condition.isEmpty() ? "WHERE " : "AND ") + "a.user_id = " + userId + " ";
        }

        String sql = """
             SELECT
                         a.id,
                         a.company_code,
                         a.name AS company_name,
                         a.percentage AS required_saudization_percentage,
                         SUM(CASE WHEN p.nationality = 'سعودي معاق' THEN 4
                          WHEN p.nationality = 'سعودي إحتياجات خاصه' THEN 4
                          WHEN p.nationality = 'سعودي مسجون' THEN 2
                          WHEN p.nationality = 'سعودي وقف جنائي' THEN 2
                          WHEN p.nationality = 'سعودي موقوف جنائي' THEN 2
                          WHEN p.id IS NOT NULL THEN 1
                       
                          ELSE 0 END ) AS total_employees,
                         
                         SUM(CASE WHEN p.nationality = 'سعودي' THEN 1
                          WHEN p.nationality = 'سعودي معاق' THEN 4
                          WHEN p.nationality = 'سعودي إحتياجات خاصه' THEN 4
                          WHEN p.nationality = 'سعودي مسجون' THEN 2
                          WHEN p.nationality = 'سعودي وقف جنائي' THEN 2
                          WHEN p.nationality = 'سعودي موقوف جنائي' THEN 2
                          
                          ELSE 0 END) AS total_saudi_employees,
                          
                         ROUND(
                             (SUM(CASE WHEN p.nationality = 'سعودي' THEN 1
                              WHEN p.nationality = 'سعودي معاق' THEN 4
                              WHEN p.nationality = 'سعودي إحتياجات خاصه' THEN 4
                              WHEN p.nationality = 'سعودي مسجون' THEN 2
                              WHEN p.nationality = 'سعودي وقف جنائي' THEN 2
                              WHEN p.nationality = 'سعودي موقوف جنائي' THEN 2
                              
                              ELSE 0 END) * 100.0) / NULLIF(COUNT(p.id), 0),
                             2
                         ) AS actual_saudization_percentage,
                         a.low_green AS LowGreen,
                         a.middel_green AS MiddelGreen,
                         a.high_green  AS HighGreen,
                         a.platinum  AS Platinum
                     FROM activities a
                     LEFT JOIN professions p
                         ON p.activity_id = a.id
                         
                     %s
                     GROUP BY
                         a.id,
                         a.company_code,
                         a.name,
                         a.percentage,
                         a.low_green,
                         a.middel_green,
                         a.high_green,
                         a.platinum
                     ORDER BY
                         a.company_code;
                
        """.formatted(condition);

        return jdbcTemplate.query(sql,
                (rs, rowNum) ->
                        new ActivitiesReportDTO(
                                rs.getInt("id"),
                                rs.getString("company_code"),
                                rs.getString("company_name"),
                                rs.getInt("total_employees"),
                                rs.getInt("total_saudi_employees"),
                                rs.getDouble("required_saudization_percentage"),
                                rs.getDouble("actual_saudization_percentage"),
                                rs.getDouble("Lowgreen"),
                                rs.getDouble("MiddelGreen"),
                                rs.getDouble("highGreen"),
                                rs.getDouble("Platinum")

                        )
        );
    }



    // get activity
    public List<ActivitiesReportDTO> getActivitiy(Long activityId) {

        String condition = (activityId != null) ? "WHERE a.id = " + activityId : "";


        String sql = """
             SELECT
                         a.id,
                         a.company_code,
                         a.name AS company_name,
                         a.percentage AS required_saudization_percentage,
                         SUM(CASE WHEN p.nationality = 'سعودي معاق' THEN 4
                          WHEN p.nationality = 'سعودي إحتياجات خاصه' THEN 4
                          WHEN p.nationality = 'سعودي مسجون' THEN 2
                          WHEN p.nationality = 'سعودي وقف جنائي' THEN 2
                          WHEN p.nationality = 'سعودي موقوف جنائي' THEN 2
                          WHEN p.id IS NOT NULL THEN 1
                       
                          ELSE 0 END ) AS total_employees,
                         
                         SUM(CASE WHEN p.nationality = 'سعودي' THEN 1
                          WHEN p.nationality = 'سعودي معاق' THEN 4
                          WHEN p.nationality = 'سعودي إحتياجات خاصه' THEN 4
                          WHEN p.nationality = 'سعودي مسجون' THEN 2
                          WHEN p.nationality = 'سعودي وقف جنائي' THEN 2
                          WHEN p.nationality = 'سعودي موقوف جنائي' THEN 2
                          
                          ELSE 0 END) AS total_saudi_employees,
                          
                         ROUND(
                             (SUM(CASE WHEN p.nationality = 'سعودي' THEN 1
                              WHEN p.nationality = 'سعودي معاق' THEN 4
                              WHEN p.nationality = 'سعودي إحتياجات خاصه' THEN 4
                              WHEN p.nationality = 'سعودي مسجون' THEN 2
                              WHEN p.nationality = 'سعودي وقف جنائي' THEN 2
                              WHEN p.nationality = 'سعودي موقوف جنائي' THEN 2
                              
                              ELSE 0 END) * 100.0) / NULLIF(COUNT(p.id), 0),
                             2
                         ) AS actual_saudization_percentage,
                         a.low_green AS LowGreen,
                         a.middel_green AS MiddelGreen,
                         a.high_green  AS HighGreen,
                         a.platinum  AS Platinum
                     FROM activities a
                     LEFT JOIN professions p
                         ON p.activity_id = a.id
                         
                     %s
                     GROUP BY
                         a.id,
                         a.company_code,
                         a.name,
                         a.percentage,
                         a.low_green,
                         a.middel_green,
                         a.high_green,
                         a.platinum
                     ORDER BY
                         a.company_code;
                
        """.formatted(condition);

        return jdbcTemplate.query(sql,
                (rs, rowNum) ->
                        new ActivitiesReportDTO(
                                rs.getInt("id"),
                                rs.getString("company_code"),
                                rs.getString("company_name"),
                                rs.getInt("total_employees"),
                                rs.getInt("total_saudi_employees"),
                                rs.getDouble("required_saudization_percentage"),
                                rs.getDouble("actual_saudization_percentage"),
                                rs.getDouble("Lowgreen"),
                                rs.getDouble("MiddelGreen"),
                                rs.getDouble("highGreen"),
                                rs.getDouble("Platinum")

                        )
        );
    }


    //



    public List<ActivitiesAnalysisDTO> getActivitieAnalysis(Long activityId) {

        String condition = (activityId != null) ? "WHERE a.id = " + activityId : "";

                String sql = """
            WITH normalized AS (
                SELECT 
                    a.company_code,
                    p.activity_id as activity_id,
                    CASE 
                        WHEN p.nationality LIKE 'سعودي%' THEN 'سعودي'
                        ELSE p.nationality
                    END AS nationality,
                    p.id
                FROM activities a
                LEFT JOIN professions p
                    ON p.activity_id = a.id
                %s
            )
            SELECT
                n.company_code,
                n.nationality,
                COUNT(n.id) AS total_by_nationality,
                
                ROUND(
                    (COUNT(n.id) * 100.0) /
                    NULLIF(SUM(COUNT(n.id)) OVER (PARTITION BY n.activity_id), 0), 2
                ) AS percentage,
                
                CASE  
                    WHEN n.nationality = 'سعودي'
                         THEN 'ملتزم'
                    WHEN n.nationality IN ('هندي', 'بنغالي')
                         AND ROUND((COUNT(n.id) * 100.0) /
                                   NULLIF(SUM(COUNT(n.id)) OVER (PARTITION BY n.activity_id), 0), 2) > 40
                         THEN 'مخالف (تجاوز 40%)'
                    WHEN n.nationality = 'يمني'
                         AND ROUND((COUNT(n.id) * 100.0) /
                                   NULLIF(SUM(COUNT(n.id)) OVER (PARTITION BY n.activity_id), 0), 2) > 25
                         THEN 'مخالف (تجاوز 25%)'
                    WHEN n.nationality = 'أثيوبي'
                         AND ROUND((COUNT(n.id) * 100.0) /
                                   NULLIF(SUM(COUNT(n.id)) OVER (PARTITION BY n.activity_id), 0), 2) > 1
                         THEN 'مخالف (تجاوز 1%)'
                    WHEN (SUM(COUNT(n.id)) OVER (PARTITION BY n.activity_id)) <= 19
                         THEN 'مسموح (عدد موظفين ≤ 19)'
                    WHEN (SUM(COUNT(n.id)) OVER (PARTITION BY n.activity_id)) BETWEEN 20 AND 49
                         AND ROUND((COUNT(n.id) * 100.0) /
                                   NULLIF(SUM(COUNT(n.id)) OVER (PARTITION BY n.activity_id), 0), 2) > 70
                         THEN 'مخالف (تجاوز 70%)'
                    WHEN (SUM(COUNT(n.id)) OVER (PARTITION BY n.activity_id)) >= 50
                         AND ROUND((COUNT(n.id) * 100.0) /
                                   NULLIF(SUM(COUNT(n.id)) OVER (PARTITION BY n.activity_id), 0), 2) > 40
                         THEN 'مخالف (تجاوز 40%)'
                    ELSE 'ملتزم'
                END AS status,
                
                CASE
                    WHEN n.nationality = 'سعودي'
                         THEN 'green'
                    WHEN (
                        CASE
                            WHEN n.nationality IN ('هندي', 'بنغالي')
                                 AND ROUND((COUNT(n.id) * 100.0) /
                                           NULLIF(SUM(COUNT(n.id)) OVER (PARTITION BY n.activity_id), 0), 2) > 40
                                 THEN 1
                            WHEN n.nationality = 'يمني'
                                 AND ROUND((COUNT(n.id) * 100.0) /
                                           NULLIF(SUM(COUNT(n.id)) OVER (PARTITION BY n.activity_id), 0), 2) > 25
                                 THEN 1
                            WHEN n.nationality = 'أثيوبي'
                                 AND ROUND((COUNT(n.id) * 100.0) /
                                           NULLIF(SUM(COUNT(n.id)) OVER (PARTITION BY n.activity_id), 0), 2) > 1
                                 THEN 1
                            WHEN (SUM(COUNT(n.id)) OVER (PARTITION BY n.activity_id)) BETWEEN 20 AND 49
                                 AND ROUND((COUNT(n.id) * 100.0) /
                                           NULLIF(SUM(COUNT(n.id)) OVER (PARTITION BY n.activity_id), 0), 2) > 70
                                 THEN 1
                            WHEN (SUM(COUNT(n.id)) OVER (PARTITION BY n.activity_id)) >= 50
                                 AND ROUND((COUNT(n.id) * 100.0) /
                                           NULLIF(SUM(COUNT(n.id)) OVER (PARTITION BY n.activity_id), 0), 2) > 40
                                 THEN 1
                            ELSE 0
                        END
                    ) = 1 THEN 'red'
                    ELSE 'green'
                END AS status_color
                
            FROM normalized n
            GROUP BY n.activity_id, n.nationality
            ORDER BY total_by_nationality DESC;
        """.replace("%s", condition);

        return jdbcTemplate.query(sql,
                (rs, rowNum) ->
                        new ActivitiesAnalysisDTO(
                                rs.getString("nationality"),

                                rs.getInt("total_by_nationality"),
                                rs.getDouble("percentage"),
                                rs.getString("status"),
                                rs.getString("status_color")

                        )
        );
    }



}
