package com.last.mapper;

import java.util.List;



import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.last.dto.MonthlyAttendanceDto;
import com.last.dto.WorkAdminAttendanceDto;
import com.last.dto.WorkDaysSummaryDto;
import com.last.dto.WorkTimesSummaryDto;
import com.last.vo.WorkAttendance;

@Mapper
public interface WorkMapper {

	// 출근시간등록
	void addAttendance(WorkAttendance attendance);
	// 퇴근시간등록
	void endAttendance(WorkAttendance attendance);
	// 일일출근등록
	WorkAttendance getTodayStartAttendancedByEmpNo(int empNo);
	// 일일퇴근등록
	WorkAttendance getTodayEndAttendancedByEmpNo(int empNo);
	// 주간근태목록
	List<WorkAttendance> getWeeklyAttendancesByEmpNo(int empNo);
	// 전체근무시간조회
	List<WorkAttendance> getAllAttendancesByDateRange(@Param("empNo") int empNo, @Param("startDate") String startDate, @Param("endDate") String endDate);
	// 조건으로 근태정보조회
	List<WorkAdminAttendanceDto> getAllAdminAttendancesByOptions(Map<String, Object> param);
	// 엑셀용리스트
	List<Map<String, Object>> getAllAdminAttendancesForExcel(Map<String, Object> param);
	// 전체행조회
	int getTotalRows(Map<String, Object> param);
	
	WorkAdminAttendanceDto getAttendanceByNo(int attendanceNo);
	// 근태정보수정
	void updateAttendance(WorkAttendance workAttendance);
	// 월근태관리 사원목록조회
	List<MonthlyAttendanceDto> getEmployees();
	// 월근무일조회
	WorkDaysSummaryDto getMonthlyWorkDaysSummary(@Param("yearMonth") String yearMonth,@Param("employeeNo") int employeeNo);
	// 월근무시간조회
	WorkTimesSummaryDto getMonthlyWorkTimesSummary(@Param("yearMonth") String yearMonth,@Param("employeeNo") int employeeNo);
	// 사원번호로 월근태조회
	List<MonthlyAttendanceDto> getEmployeesByEmpNo(int empNo);
	// 월근태현황
	//List<WorkAdminAttendanceDto> getMonthStatusList();	
}
