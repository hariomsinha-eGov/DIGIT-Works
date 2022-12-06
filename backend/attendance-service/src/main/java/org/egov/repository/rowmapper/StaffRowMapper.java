package org.egov.repository.rowmapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import digit.models.coremodels.AuditDetails;
import org.apache.commons.lang3.StringUtils;
import org.egov.tracer.model.CustomException;
import org.egov.web.models.*;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class StaffRowMapper implements ResultSetExtractor<List<AttendanceRegister>> {

    @Autowired
    private ObjectMapper mapper;

    @Override
    public List<AttendanceRegister> extractData(ResultSet rs) throws SQLException, DataAccessException {

        Map<String, AttendanceRegister> attendanceRegisterMap = new LinkedHashMap<>();
        while (rs.next()) {
            String id = rs.getString("id");
            String tenantId = rs.getString("tenantId");
            String registerNumber = rs.getString("registernumber");
            String name = rs.getString("name");
            BigDecimal startDate = rs.getBigDecimal("startdate");
            BigDecimal endDate = rs.getBigDecimal("enddate");
            String status = rs.getString("status");
            String createdby = rs.getString("createdby");
            String lastmodifiedby = rs.getString("lastmodifiedby");
            Long createdtime = rs.getLong("createdtime");
            Long lastmodifiedtime = rs.getLong("lastmodifiedtime");

            AuditDetails auditDetails = AuditDetails.builder().createdBy(createdby).createdTime(createdtime)
                    .lastModifiedBy(lastmodifiedby).lastModifiedTime(lastmodifiedtime)
                    .build();

            JsonNode additionalDetails = getAdditionalDetail("additionaldetails", rs);

            AttendanceRegister attendanceRegister = AttendanceRegister.builder().registerNumber(registerNumber).id(UUID.fromString(id))
                    .tenantId(tenantId).name(name).startDate(startDate.doubleValue()).endDate(endDate.doubleValue())
                    .status(Status.fromValue(status)).additionalDetails(additionalDetails).auditDetails(auditDetails).build();

            if (!attendanceRegisterMap.containsKey(id)) {
                attendanceRegisterMap.put(id, attendanceRegister);
            }

            addChildrenToProperty(rs,attendanceRegisterMap.get(id));



        }
        return new ArrayList<>(attendanceRegisterMap.values());
    }


    private void addChildrenToProperty(ResultSet rs, AttendanceRegister attendanceRegister)
            throws SQLException {
        addStaff(rs, attendanceRegister);
    }

    private void addStaff(ResultSet rs, AttendanceRegister attendanceRegister) throws SQLException {
        String id = rs.getString("staffId");
        String individualId = rs.getString("staffIndId");
        String registerId = rs.getString("staffRegId");
        BigDecimal enrollmentDate = rs.getBigDecimal("staffEnroll");
        BigDecimal deenrollmentDate = rs.getBigDecimal("staffDeenroll");


        if (StringUtils.isNotBlank(registerId) && registerId.equalsIgnoreCase(attendanceRegister.getId().toString())) {

            StaffPermission staffPermission = StaffPermission.builder().id(UUID.fromString(id)).userId(individualId)
                    .registerId(registerId).enrollmentDate(enrollmentDate.doubleValue()).denrollmentDate(deenrollmentDate.doubleValue()).build();

            attendanceRegister.addStaffItem(staffPermission);
        }
    }


    private JsonNode getAdditionalDetail(String columnName, ResultSet rs) throws SQLException {
        JsonNode additionalDetails = null;
        try {
            PGobject obj = (PGobject) rs.getObject(columnName);
            if (obj != null) {
                additionalDetails = mapper.readTree(obj.getValue());
            }
        } catch (IOException e) {
            throw new CustomException("PARSING ERROR", "Failed to parse additionalDetail object");
        }
        if (additionalDetails.isEmpty())
            additionalDetails = null;
        return additionalDetails;
    }
}