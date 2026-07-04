package com.nitaqat.nitaqat.repository;

import com.nitaqat.nitaqat.dto.ProfessionReportDTO;
import com.nitaqat.nitaqat.dto.ProfessionReportDTOWithGap;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class ProfessionReportRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public ProfessionReportRepository(JdbcTemplate jdbcTemplate , NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ProfessionReportDTOWithGap> getProfessionReport(Long activityId, Long userId) {
        StringBuilder condition = new StringBuilder();
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (activityId != null) {
            condition.append(" WHERE a.id = :activityId ");
            params.addValue("activityId", activityId);
        }
        if (userId != null) {
            condition.append(condition.isEmpty() ? " WHERE " : " AND ")
                    .append(" a.user_id = :userId AND p.user_id = :userId ");
            params.addValue("userId", userId);
        }

        String sql = """
        SELECT 
            MIN(p.id) AS profession_id,
            a.name AS company_name,
            p.company_code,
            sp.saudization_catageory,
            sp.saudization_catageory_ar,
            sp.emp_threshold,
            COUNT(p.id) AS total_employees,
            SUM(CASE WHEN p.nationality LIKE 'سعودي%%' THEN 1 ELSE 0 END) AS total_saudi_employees,
            sp.saudization_percentage AS required_saudization_percentage,

            CASE
                WHEN COUNT(p.id) = SUM(CASE WHEN p.nationality LIKE 'سعودي%%' THEN 1 ELSE 0 END) THEN 100
                WHEN COUNT(p.id) < sp.emp_threshold THEN 100
                ELSE ROUND(
                    (SUM(CASE WHEN p.nationality LIKE 'سعودي%%' THEN 1 ELSE 0 END) * 100.0)
                    / NULLIF(COUNT(p.id), 0), 2
                )
            END AS actual_saudization_percentage,

            -- ✅ Option A: Replace non-Saudi staff (headcount stays the same)
            CASE
                WHEN COUNT(p.id) < sp.emp_threshold THEN 0
                WHEN SUM(CASE WHEN p.nationality LIKE 'سعودي%%' THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(p.id),0) >= sp.saudization_percentage THEN 0
                ELSE CEIL(
                    (sp.saudization_percentage / 100.0 * COUNT(p.id))
                    - SUM(CASE WHEN p.nationality LIKE 'سعودي%%' THEN 1 ELSE 0 END)
                )
            END AS gap_replace_existing,

            -- ✅ Option B: Add new Saudi hires (headcount grows)
            CASE
                WHEN COUNT(p.id) < sp.emp_threshold THEN 0
                WHEN SUM(CASE WHEN p.nationality LIKE 'سعودي%%' THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(p.id),0) >= sp.saudization_percentage THEN 0
                WHEN sp.saudization_percentage >= 100 THEN NULL
                ELSE CEIL(
                    (sp.saudization_percentage * COUNT(p.id) - 100.0 * SUM(CASE WHEN p.nationality LIKE 'سعودي%%' THEN 1 ELSE 0 END))
                    / (100.0 - sp.saudization_percentage)
                )
            END AS gap_new_hires,
            -- ✅ Option C: Employees needed to reach the exemption threshold
                CASE
                    WHEN COUNT(p.id) < sp.emp_threshold
                        THEN sp.emp_threshold - COUNT(p.id) -1
                    ELSE 0
                END AS employees_needed_for_threshold

        FROM professions p
        LEFT JOIN saudization_percentage sp ON p.job = sp.job
        JOIN activities a ON p.activity_id = a.id
        %s
        GROUP BY
            p.company_code, a.name,
            sp.saudization_catageory, sp.saudization_percentage,
            sp.saudization_catageory_ar, sp.emp_threshold
        ORDER BY sp.saudization_catageory
    """.formatted(condition);

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            Integer gapReplace = toIntOrNull(rs.getObject("gap_replace_existing"));
            Integer gapHiring = toIntOrNull(rs.getObject("gap_new_hires"));
            Integer neededForThreshold = toIntOrNull(rs.getObject("employees_needed_for_threshold"));


            return new ProfessionReportDTOWithGap(
                    rs.getInt("profession_id"),
                    rs.getString("company_code"),
                    rs.getString("company_name"),
                    rs.getString("saudization_catageory"),
                    rs.getString("saudization_catageory_ar"),
                    rs.getInt("emp_threshold"),
                    rs.getInt("total_employees"),
                    rs.getInt("total_saudi_employees"),
                    rs.getDouble("required_saudization_percentage"),
                    rs.getDouble("actual_saudization_percentage"),
                    gapReplace,
                    buildGapText(gapReplace, "en", "replace"),
                    buildGapText(gapReplace, "ar", "replace"),
                    gapHiring,
                    buildGapText(gapHiring, "en", "hire"),
                    buildGapText(gapHiring, "ar", "hire"),
                    neededForThreshold,
                    buildThresholdText(neededForThreshold, "en"),
                    buildThresholdText(neededForThreshold, "ar")
            );
        });
    }

    // Helper to generate human-readable explanation text
    private String buildGapText(Integer gap, String lang, String type) {
        if (gap == null) {
            return null;
        }
        if (gap == 0) {
            return lang.equals("ar") ? "الشركة ملتزمة بالفعل" : "Already compliant";
        }
        if (type.equals("replace")) {
            return lang.equals("ar")
                    ? "يلزم استبدال " + gap + " موظف غير سعودي بموظف سعودي"
                    : "Replace " + gap + " non-Saudi employee(s) with Saudi employee(s)";
        } else {
            return lang.equals("ar")
                    ? "يلزم توظيف " + gap + " موظف سعودي إضافي"
                    : "Hire " + gap + " additional Saudi employee(s)";
        }
    }

    // Helper for the threshold-gap text
    private String buildThresholdText(Integer needed, String lang) {
        if (needed == null || needed == 0) {
            return null;
        }
        return lang.equals("ar")
                ? "يلزم توظيف " + needed + " موظف إضافي للوصول للحد الأدنى "
                : "Hire " + needed + " more employee(s) to reach the minimum threshold";
    }

    private Integer toIntOrNull(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal bd) {
            return bd.intValue();
        }
        if (value instanceof Number n) {
            return n.intValue();
        }
        return null;
    }



// get profession for activity for calculation

    public List<ProfessionReportDTO> getProfessionForActivity(Long activityId , Long userId) {

        String condition = (activityId != null) ? "WHERE a.id = " + activityId : "";
        condition += " AND p.activity_id = "  + activityId ;

        if (userId != null) {
            condition += (condition.isEmpty() ? "WHERE " : " AND ") + "a.user_id = " + userId + " ";
            condition += " AND p.user_id = "  + userId ;
        }



        String sql = """
            SELECT
                MIN(p.id) AS id,
                p.company_code,
                a.name AS company_name,
                sp.saudization_catageory,
                sp.saudization_catageory_ar,
                sp.emp_threshold,
                COUNT(p.id) AS total_employees,
                
                SUM(CASE WHEN p.nationality LIKE 'سعودي%' THEN 1 ELSE 0 END) AS total_saudi_employees,
                
                sp.saudization_percentage AS required_saudization_percentage,
                
                CASE
                    WHEN COUNT(p.id) = SUM(CASE WHEN p.nationality LIKE 'سعودي%' THEN 1 ELSE 0 END)
                        THEN 100
                WHEN COUNT(p.id) < sp.emp_threshold
                        THEN 100
        
                ELSE
                        ROUND(
                            (SUM(CASE WHEN p.nationality LIKE 'سعودي%' THEN 1 ELSE 0 END) * 100.0)
                            / NULLIF(COUNT(p.id), 0),
                            2
                        )
                END AS actual_saudization_percentage
                      
            FROM professions p
            JOIN saudization_percentage sp 
                ON p.job = sp.job
            JOIN activities a 
                ON p.activity_id = a.id
            %s
            GROUP BY 
          
                p.company_code,
                a.name,
                sp.saudization_catageory,
                sp.saudization_percentage,
                sp.saudization_catageory_ar,
                sp.emp_threshold
            ORDER BY 
                sp.saudization_catageory
                
        """.replace("%s", condition);

        return jdbcTemplate.query(sql,
                (rs, rowNum) ->
                        new ProfessionReportDTO(
                                rs.getInt("id"),
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
    public List<ProfessionReportDTO> getProfession(Long activityId ,   Long userId ) {

        String condition = (activityId != null) ? "WHERE a.id = " + activityId : "";
        condition += " AND p.activity_id = "  + activityId ;

        if (userId != null) {
            condition += (condition.isEmpty() ? "WHERE " : " AND ") + "a.user_id = " + userId + " ";
            condition += " AND p.user_id = "  + userId ;
        }



        String sql = """
            SELECT 
                MIN(p.id) AS id, 
                p.company_code,
                a.name AS company_name,
                sp.saudization_catageory,
                sp.saudization_catageory_ar,
                sp.emp_threshold,
                COUNT(p.id) AS total_employees,
                
                SUM(CASE WHEN p.nationality LIKE 'سعودي%' THEN 1 ELSE 0 END) AS total_saudi_employees,
                
                sp.saudization_percentage AS required_saudization_percentage,
                
                CASE
                    WHEN COUNT(p.id) = SUM(CASE WHEN p.nationality LIKE 'سعودي%' THEN 1 ELSE 0 END)
                        THEN 100
                WHEN COUNT(p.id) < sp.emp_threshold
                        THEN 100
        
                ELSE
                        ROUND(
                            (SUM(CASE WHEN p.nationality LIKE 'سعودي%' THEN 1 ELSE 0 END) * 100.0)
                            / NULLIF(COUNT(p.id), 0),
                            2
                        )
                END AS actual_saudization_percentage
                      
            FROM professions p
            JOIN saudization_percentage sp 
                ON p.job = sp.job
            JOIN activities a 
                ON p.activity_id = a.id
            %s
            GROUP BY 
                
                p.company_code,
                a.name,
                sp.saudization_catageory,
                sp.saudization_percentage,
                sp.saudization_catageory_ar,
                sp.emp_threshold
            ORDER BY 
                sp.saudization_catageory
                
        """.replace("%s", condition);

        return jdbcTemplate.query(sql,
                (rs, rowNum) ->
                        new ProfessionReportDTO(
                                rs.getInt("id"),
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
