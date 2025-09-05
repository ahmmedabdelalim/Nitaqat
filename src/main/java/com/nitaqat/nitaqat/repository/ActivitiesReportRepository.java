package com.nitaqat.nitaqat.repository;

import com.nitaqat.nitaqat.dto.ActivitiesReportDTO;
import com.nitaqat.nitaqat.dto.ProfessionReportDTO;
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
                         COUNT(p.id) AS total_employees,
                         SUM(CASE WHEN p.nationality = 'سعودي' THEN 1
                          WHEN p.nationality = 'سعودي معاق' THEN 4
                          ELSE 0 END) AS total_saudi_employees,
                         ROUND(
                             (SUM(CASE WHEN p.nationality = 'سعودي' THEN 1
                              WHEN p.nationality = 'سعودي معاق' THEN 4
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
                         a.percentage
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
}
