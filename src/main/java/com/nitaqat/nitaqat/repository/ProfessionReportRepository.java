package com.nitaqat.nitaqat.repository;

import com.nitaqat.nitaqat.dto.ProfessionReportDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProfessionReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProfessionReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ProfessionReportDTO> getProfessionReport() {
        String sql = """
            SELECT 
                p.company_code,
                a.company_name,
                sp.saudization_catageory,
                COUNT(p.id) AS total_employees,
                SUM(CASE WHEN p.nationality = 'سعودي' THEN 1 ELSE 0 END) AS total_saudi_employees,
                sp.saudization_percentage AS required_saudization_percentage,
                ROUND(
                    (SUM(CASE WHEN p.nationality = 'سعودي' THEN 1 ELSE 0 END) * 100.0) / NULLIF(COUNT(p.id), 0),
                    2
                ) AS actual_saudization_percentage
            FROM professions p
            JOIN saudization_percentage sp 
                ON p.job = sp.job
            JOIN activities a 
                ON p.company_code = a.company_code
            GROUP BY 
                p.company_code,
                a.company_name,
                sp.saudization_catageory,
                sp.saudization_percentage
            ORDER BY 
                p.company_code,
                sp.saudization_catageory
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new ProfessionReportDTO(
                        rs.getString("company_code"),
                        rs.getString("company_name"),
                        rs.getString("saudization_catageory"),
                        rs.getInt("total_employees"),
                        rs.getInt("total_saudi_employees"),
                        rs.getDouble("required_saudization_percentage"),
                        rs.getDouble("actual_saudization_percentage")
                )
        );
    }
}
