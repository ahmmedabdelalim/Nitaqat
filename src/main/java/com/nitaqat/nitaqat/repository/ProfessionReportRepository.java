package com.nitaqat.nitaqat.repository;

import com.nitaqat.nitaqat.dto.ProfessionReportDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProfessionReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProfessionReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ProfessionReportDTO> getProfessionReport(Long activityId  ) {

        String condition = (activityId != null) ? "WHERE a.id = " + activityId : "";


        String sql = """
            SELECT 
                MIN(p.id) AS profession_id,
                p.company_code,
                a.name AS company_name,
                sp.saudization_catageory,
                sp.saudization_catageory_ar,
                sp.emp_threshold,
                COUNT(p.id) AS total_employees,
                
                SUM(CASE WHEN p.nationality LIKE 'سعودي%' THEN 1 ELSE 0 END) AS total_saudi_employees,
                
                 
                sp.saudization_percentage AS required_saudization_percentage,
                ( CASE WHEN SUM(CASE WHEN p.nationality LIKE 'سعودي%' THEN 1 ELSE 0 END) < sp.emp_threshold	THEN 100
                ELSE
                ROUND((SUM(CASE WHEN p.nationality LIKE 'سعودي%' THEN 1 ELSE 0 END) * 100.0) /
                          NULLIF( COUNT(p.id) , 0),2 )  END ) AS actual_saudization_percentage
                      
            FROM professions p
            JOIN saudization_percentage sp 
                ON p.job = sp.job
            JOIN activities a 
                ON p.company_code = a.company_code
            %s
            GROUP BY 
               
                p.company_code,
                a.name,
                sp.saudization_catageory,
                sp.saudization_percentage,
                sp.saudization_catageory_ar,
                sp.emp_threshold
            ORDER BY 
                p.company_code,
                sp.saudization_catageory
                
        """.replace("%s", condition);

        return jdbcTemplate.query(sql,
                (rs, rowNum) ->
                new ProfessionReportDTO(
                        rs.getInt("profession_id"),
                        rs.getString("company_code"),
                        rs.getString("company_name"),
                        rs.getString("saudization_catageory"),
                        rs.getString("saudization_catageory_ar"),
                        rs.getInt("emp_threshold"),
                        rs.getInt("total_employees"),
                        rs.getInt("total_saudi_employees"),
                        rs.getDouble("required_saudization_percentage"),
                        rs.getDouble("actual_saudization_percentage")
                )
        );
    }


    public List<ProfessionReportDTO> getProfession(Long activityId   ) {

        String condition = (activityId != null) ? "WHERE a.id = " + activityId : "";


        String sql = """
            SELECT
                MIN(p.id) AS profession_id,
                p.company_code,
                a.name AS company_name,
                sp.saudization_catageory,
                sp.saudization_catageory_ar,
                sp.emp_threshold,
                COUNT(p.id) AS total_employees,                
                SUM(CASE WHEN p.nationality LIKE 'سعودي%' THEN 1 ELSE 0 END) AS total_saudi_employees,
                     
                sp.saudization_percentage AS required_saudization_percentage,
                 ( CASE WHEN SUM(CASE WHEN p.nationality LIKE 'سعودي%' THEN 1 ELSE 0 END) < sp.emp_threshold	THEN 100
                ELSE
                ROUND((SUM(CASE WHEN p.nationality LIKE 'سعودي%' THEN 1 ELSE 0 END) * 100.0) /
                          NULLIF( COUNT(p.id) , 0),2 )  END ) AS actual_saudization_percentage
                          
            FROM professions p
            JOIN saudization_percentage sp 
                ON p.job = sp.job
            JOIN activities a 
                ON p.company_code = a.company_code
            %s
            GROUP BY 
                
                p.company_code,
                a.name,
                sp.saudization_catageory,
                sp.saudization_percentage,
                sp.saudization_catageory_ar,
                sp.emp_threshold
            ORDER BY 
                p.company_code,
                sp.saudization_catageory
                
        """.replace("%s", condition);

        return jdbcTemplate.query(sql,
                (rs, rowNum) ->
                        new ProfessionReportDTO(
                                rs.getInt("profession_id"),
                                rs.getString("company_code"),
                                rs.getString("company_name"),
                                rs.getString("saudization_catageory"),
                                rs.getString("saudization_catageory_ar"),
                                rs.getInt("emp_threshold"),
                                rs.getInt("total_employees"),
                                rs.getInt("total_saudi_employees"),
                                rs.getDouble("required_saudization_percentage"),
                                rs.getDouble("actual_saudization_percentage")
                        )
        );
    }
}
