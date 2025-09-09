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


    public List<ActivitiesReportDTO> getActivitiesReport(Long activityId) {

        String condition = (activityId != null) ? "WHERE a.id = " + activityId : "";


        String sql = """
             SELECT
                         a.company_code,
                         a.name AS company_name,
                         a.percentage AS required_saudization_percentage,
                         SUM(CASE WHEN p.nationality = 'سعودي معاق' THEN 4
                          WHEN p.nationality = 'سعودي إحتياجات خاصه' THEN 4
                          WHEN p.nationality = 'سعودي مسجون' THEN 2
                          WHEN p.nationality = 'سعودي وقف جنائي' THEN 2
                          WHEN p.nationality = 'سعودي موقوف جنائي' THEN 2
                          
                          ELSE 1 END ) AS total_employees,
                         
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
                         ON p.company_code = a.company_code
                         
                     %s
                     GROUP BY
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


    public List<ActivitiesAnalysisDTO> getActivitieAnalysis(Long activityId) {

        String condition = (activityId != null) ? "WHERE a.id = " + activityId : "";


//        String sql = """
//                SELECT
//                    p.nationality,
//                    COUNT(p.id) AS total_by_nationality,
//                    ROUND(
//                        (COUNT(p.id) * 100.0) /
//                        NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0),2) AS percentage,
//
//                        CASE
//                        WHEN p.nationality IN ('هندي', 'بنغالي')
//                             AND ROUND((COUNT(p.id) * 100.0) /
//                                       NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2) > 40
//                             THEN 'مخالف (تجاوز 40%)'
//
//                        WHEN p.nationality = 'يمني'
//                             AND ROUND((COUNT(p.id) * 100.0) /
//                                       NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2) > 25
//                             THEN 'مخالف (تجاوز 25%)'
//
//                        WHEN p.nationality = 'أثيوبي'
//                             AND ROUND((COUNT(p.id) * 100.0) /
//                                       NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2) > 1
//                             THEN 'مخالف (تجاوز 1%)'
//
//                        WHEN (SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code)) <= 19
//                             THEN 'مسموح (عدد موظفين أقل من او يساوى 19)'
//
//                        WHEN (SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code)) BETWEEN 20 AND 49
//                             AND ROUND((COUNT(p.id) * 100.0) /
//                                       NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2) > 70
//                             THEN 'مخالف (تجاوز 70%)'
//
//                        WHEN (SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code)) >= 50
//                             AND ROUND((COUNT(p.id) * 100.0) /
//                                       NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2) > 40
//                             THEN 'مخالف (تجاوز 40%)'
//
//                        ELSE 'ملتزم'
//                    END AS status,
//
//                    CASE
//                        WHEN (
//                            CASE
//                                WHEN p.nationality IN ('هندي', 'بنغالي')
//                                     AND ROUND((COUNT(p.id) * 100.0) /
//                                               NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2) > 40
//                                     THEN 1
//                                WHEN p.nationality = 'يمني'
//                                     AND ROUND((COUNT(p.id) * 100.0) /
//                                               NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2) > 25
//                                     THEN 1
//                                WHEN p.nationality = 'أثيوبي'
//                                     AND ROUND((COUNT(p.id) * 100.0) /
//                                               NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2) > 1
//                                     THEN 1
//                                WHEN (SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code)) BETWEEN 20 AND 49
//                                     AND ROUND((COUNT(p.id) * 100.0) /
//                                               NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2) > 70
//                                     THEN 1
//                                WHEN (SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code)) >= 50
//                                     AND ROUND((COUNT(p.id) * 100.0) /
//                                               NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2) > 40
//                                     THEN 1
//                                ELSE 0
//                            END
//                        ) = 1 THEN 'red'
//                        ELSE 'green'
//                    END AS status_color
//                FROM activities a
//                LEFT JOIN professions p
//                    ON p.company_code = a.company_code
//                %s
//                GROUP BY a.company_code, p.nationality
//                HAVING SUM(CASE WHEN p.id IS NULL THEN 0 ELSE 1 END) > 0
//
//                ORDER BY  total_by_nationality DESC;
//        """.replace("%s", condition);

        String sql = """
            SELECT
                CASE 
                    WHEN p.nationality LIKE 'سعودي%' THEN 'سعودي'
                    ELSE p.nationality
                END AS nationality,
        
                COUNT(p.id) AS total_by_nationality,
        
                ROUND(
                    (COUNT(p.id) * 100.0) /
                    NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2
                ) AS percentage,
        
                CASE  
                    WHEN (CASE 
                             WHEN p.nationality LIKE 'سعودي%' THEN 'سعودي'
                             ELSE p.nationality
                          END) = 'سعودي'
                         THEN 'ملتزم'   -- السعوديين دايمًا ملتزم
        
                    WHEN (CASE 
                             WHEN p.nationality LIKE 'سعودي%' THEN 'سعودي'
                             ELSE p.nationality
                          END) IN ('هندي', 'بنغالي')
                         AND ROUND((COUNT(p.id) * 100.0) /
                                   NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2) > 40
                         THEN 'مخالف (تجاوز 40%)'
        
                    WHEN (CASE 
                             WHEN p.nationality LIKE 'سعودي%' THEN 'سعودي'
                             ELSE p.nationality
                          END) = 'يمني'
                         AND ROUND((COUNT(p.id) * 100.0) /
                                   NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2) > 25
                         THEN 'مخالف (تجاوز 25%)'
        
                    WHEN (CASE 
                             WHEN p.nationality LIKE 'سعودي%' THEN 'سعودي'
                             ELSE p.nationality
                          END) = 'أثيوبي'
                         AND ROUND((COUNT(p.id) * 100.0) /
                                   NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2) > 1
                         THEN 'مخالف (تجاوز 1%)'
        
                    WHEN (SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code)) <= 19
                         THEN 'مسموح (عدد موظفين أقل من او يساوى 19)'
        
                    WHEN (SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code)) BETWEEN 20 AND 49
                         AND ROUND((COUNT(p.id) * 100.0) /
                                   NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2) > 70
                         THEN 'مخالف (تجاوز 70%)'
        
                    WHEN (SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code)) >= 50
                         AND ROUND((COUNT(p.id) * 100.0) /
                                   NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2) > 40
                         THEN 'مخالف (تجاوز 40%)'
        
                    ELSE 'ملتزم'
                END AS status,
        
                CASE
                    WHEN (CASE 
                             WHEN p.nationality LIKE 'سعودي%' THEN 'سعودي'
                             ELSE p.nationality
                          END) = 'سعودي'
                         THEN 'green'   -- السعوديين دايمًا أخضر
        
                    WHEN (
                        CASE
                            WHEN (CASE 
                                     WHEN p.nationality LIKE 'سعودي%' THEN 'سعودي'
                                     ELSE p.nationality
                                 END) IN ('هندي', 'بنغالي')
                                 AND ROUND((COUNT(p.id) * 100.0) /
                                           NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2) > 40
                                 THEN 1
                            WHEN (CASE 
                                     WHEN p.nationality LIKE 'سعودي%' THEN 'سعودي'
                                     ELSE p.nationality
                                 END) = 'يمني'
                                 AND ROUND((COUNT(p.id) * 100.0) /
                                           NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2) > 25
                                 THEN 1
                            WHEN (CASE 
                                     WHEN p.nationality LIKE 'سعودي%' THEN 'سعودي'
                                     ELSE p.nationality
                                 END) = 'أثيوبي'
                                 AND ROUND((COUNT(p.id) * 100.0) /
                                           NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2) > 1
                                 THEN 1
                            WHEN (SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code)) BETWEEN 20 AND 49
                                 AND ROUND((COUNT(p.id) * 100.0) /
                                           NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2) > 70
                                 THEN 1
                            WHEN (SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code)) >= 50
                                 AND ROUND((COUNT(p.id) * 100.0) /
                                           NULLIF(SUM(COUNT(p.id)) OVER (PARTITION BY a.company_code), 0), 2) > 40
                                 THEN 1
                            ELSE 0
                        END
                    ) = 1 THEN 'red'
                    ELSE 'green'
                END AS status_color
        
            FROM activities a
            LEFT JOIN professions p
                ON p.company_code = a.company_code
            %s
            GROUP BY a.company_code,
                     CASE 
                        WHEN p.nationality LIKE 'سعودي%' THEN 'سعودي'
                        ELSE p.nationality
                     END
            HAVING SUM(CASE WHEN p.id IS NULL THEN 0 ELSE 1 END) > 0
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
