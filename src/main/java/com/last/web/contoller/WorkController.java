package com.last.web.contoller;


import java.util.HashMap;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.last.dto.MonthlyAttendanceDto;
import com.last.dto.WorkDaysSummaryDto;
import com.last.dto.WorkTimesSummaryDto;
import com.last.security.AuthenticatedUser;
import com.last.security.LoginEmployee;
import com.last.service.WorkService;
import com.last.vo.WorkAttendance;

@Controller
@RequestMapping("/work")
@SessionAttributes({"modifyAttendance"})
public class WorkController {
	
	@Autowired(required=true)
	private WorkService workService;
	
	// 일일근태등록페이지
	@GetMapping("/day")
	public String dailyAttendance(@AuthenticatedUser LoginEmployee loginEmployee, WorkAttendance workAttendance, Model model) {
		// 이번 주 출퇴근 정보 조회
		List<WorkAttendance> attendances = workService.getWeeklyAttendances(loginEmployee.getNo());
		model.addAttribute("attendances", attendances);
		
		//일일출퇴근여부를 판단
		boolean isStartAttendanced = workService.isStartAttendancedToday(loginEmployee.getNo());
		boolean isEndAttendanced = workService.isEndAttendancedToday(loginEmployee.getNo());
		
		model.addAttribute("isStartAttendanced", isStartAttendanced);
		model.addAttribute("isEndAttendanced", isEndAttendanced);
		
		return "work/daily-attendance"; // WEB-INF/views/work/daily-attendance.jsp로 내부 이동
	}
	
	// 출근시간등록
	@GetMapping("/startAttendance")
	public String startAttendance(@AuthenticatedUser LoginEmployee loginEmployee) {
		
		workService.addAttendance(loginEmployee.getNo());
		
		return "redirect:/work/day"; // 저장작업했기 때문에 재요청 URL을 전송
	}
	
	// 퇴근시간등록
	@GetMapping("/endAttendance")
	public String endAttendance(@AuthenticatedUser LoginEmployee loginEmployee) {
		workService.endAttendance(loginEmployee.getNo());
		
		return "redirect:/work/day";
	}
	
	// 근태정보 기간으로 조회, startDate와endDate의 값을 전달해줌
	@GetMapping("/searchAttendances")
	public String getAttendanceList(@AuthenticatedUser LoginEmployee loginEmployee,
									@RequestParam("startDate") String startDate,
									@RequestParam("endDate") String endDate, Model model) {
		List<WorkAttendance> attendances = workService.getAllAttendances(loginEmployee.getNo(), startDate, endDate);
		model.addAttribute("attendances", attendances);
		
		boolean isStartAttendanced = workService.isStartAttendancedToday(loginEmployee.getNo());
		boolean isEndAttendanced = workService.isEndAttendancedToday(loginEmployee.getNo());
		
		model.addAttribute("isStartAttendanced", isStartAttendanced);
		model.addAttribute("isEndAttendanced", isEndAttendanced);
		
		return "work/daily-attendance";	
	}
	
	// 관리자일일근태페이지
	@GetMapping("/dayadmin")
	public String getAdminAttendanceList(@RequestParam(name = "startDate", required = false, defaultValue = "") String startDate, 
			@RequestParam(name = "endDate", required = false, defaultValue = "") String endDate,
			@RequestParam(name = "empNo", required = false, defaultValue = "0") int empNo,
			@RequestParam(name = "positionNo", required = false, defaultValue = "0") int positionNo,
			@RequestParam(name = "deptNo", required = false, defaultValue = "0") int deptNo, 
			@RequestParam(name = "page", required = false, defaultValue = "1") int page, Model model) {
		
		Map<String, Object> param = new HashMap<String, Object>();
		if (!startDate.isBlank()) {
			param.put("startDate", startDate);
		}
		if (!endDate.isBlank()) {
			param.put("endDate", endDate);
		}
		if (empNo > 0) {
			param.put("empNo", empNo);
		}
		if (positionNo > 0) {
			param.put("positionNo", positionNo);
		}
		if (deptNo > 0) {
			param.put("deptNo", deptNo);
		}
		param.put("page", page);
		
		Map<String, Object> result = workService.getAllAdminAttendances(param);
		model.addAttribute("adminAttendanceDtos", result.get("adminAttendanceDtos"));
		model.addAttribute("pagination", result.get("pagination"));
		
		return "work/daily-manage";
	}
	
	@PostMapping("/modify")
	public String modifySearchAttendance(@RequestParam("no") int attendanceNo, 
			@RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime,
			@RequestParam(name = "startDate", required = false, defaultValue = "") String startDate, 
			@RequestParam(name = "endDate", required = false, defaultValue = "") String endDate,
			@RequestParam(name = "empNo", required = false, defaultValue = "0") int empNo,
			@RequestParam(name = "positionNo", required = false, defaultValue = "0") int positionNo,
			@RequestParam(name = "deptNo", required = false, defaultValue = "0") int deptNo, 
			@RequestParam(name = "page", required = false, defaultValue = "1") int page,
			RedirectAttributes redirectAttributes) {
		
		workService.updateAttendance(attendanceNo, startTime, endTime);
		
		// redirectAttributes를 사용하면 redirect url뒤에 파라미터값을 하나씩 안넣어줘도됨
		redirectAttributes.addAttribute("startDate", startDate);
		redirectAttributes.addAttribute("endDate", endDate);
		redirectAttributes.addAttribute("empNo", empNo);
		redirectAttributes.addAttribute("positionNo", positionNo);
		redirectAttributes.addAttribute("deptNo", deptNo);
		redirectAttributes.addAttribute("page", page);
		
		return "redirect:/work/dayadmin";
		}
	
	// 월근태관리자페이지
	@GetMapping("/month")
	public String getMonthlyAttendance(Model model) {
		List<MonthlyAttendanceDto> monthlyAttendanceDtos = workService.getEmployees();
		model.addAttribute("monthlyAttendanceDtos", monthlyAttendanceDtos);
		return "work/monthly-manage";
	}
	
	// 월근태사원페이지
	@GetMapping("/my-month")
	public String getMyMonthlyAttendance(@AuthenticatedUser LoginEmployee loginEmployee, Model model) {
		List<MonthlyAttendanceDto> monthlyAttendanceDtos = workService.getEmployeesByEmpNo(loginEmployee.getNo());
		model.addAttribute("monthlyAttendanceDtos", monthlyAttendanceDtos);
		return "work/monthly-manage-user";
	}
	
	// 월근태상세정보
	@GetMapping("/monthDetail")
	@ResponseBody
	public Map<String, Object> getMonthlyAttendanceDetail(@RequestParam("yearMonth") String yearMonth, 
			@RequestParam("empNo") int employeeNo) {
		WorkDaysSummaryDto daysDto = workService.getMonthlyWorkDaysSummary(yearMonth, employeeNo);
		WorkTimesSummaryDto timesDto = workService.getMonthlyWorkTimesSummary(yearMonth, employeeNo);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("daysDto", daysDto);
		result.put("timesDto", timesDto);
		
		return result;
	}

	// 엑셀파일다운로드
	//produces = "application/octet-stream" : 바이너리 데이터를 응답으로 내려줌
	@GetMapping(path = "/download", produces = "application/octet-stream")
	public String downloadAttendances(@RequestParam(name = "startDate", required = false, defaultValue = "") String startDate, 
			@RequestParam(name = "endDate", required = false, defaultValue = "") String endDate,
			@RequestParam(name = "empNo", required = false, defaultValue = "0") int empNo,
			@RequestParam(name = "positionNo", required = false, defaultValue = "0") int positionNo,
			@RequestParam(name = "deptNo", required = false, defaultValue = "0") int deptNo, 
			@RequestParam(name = "page", required = false, defaultValue = "1") int page, Model model) {
		
		Map<String, Object> param = new HashMap<String, Object>();
		if (!startDate.isBlank()) {
			param.put("startDate", startDate);
		}
		if (!endDate.isBlank()) {
			param.put("endDate", endDate);
		}
		if (empNo > 0) {
			param.put("empNo", empNo);
		}
		if (positionNo > 0) {
			param.put("positionNo", positionNo);
		}
		if (deptNo > 0) {
			param.put("deptNo", deptNo);
		}
		param.put("page", page);
		
		List<Map<String, Object>> items = workService.getAttendancesByOptions(param);
		
		// xml과 동일한 명칭을 적어야함
		List<String> keys = List.of("WORKINGDATE", "EMPNO", "EMPNAME", "DEPTNAME", "POSITIONNAME","ATTENDANCESTYPE", 
									"STARTWORKTIME", "ENDWORKTIME", "WORKEDTIMES", "OVERTIMEWORKEDTIMES", "NIGHTWORKEDTIMES");
		List<String> headers = List.of("근무일자", "사원번호", "성명", "부서", "직급", "근태구분", "출근시간", "퇴근시간", "근무시간", "연장근무시간", "야근시간");
		List<Integer> widths = List.of(20, 10, 10, 10, 10, 20, 10, 10, 10, 20, 10);
		
		
		model.addAttribute("filename", "일일근태목록.xlsx");
		model.addAttribute("keys", keys);
		model.addAttribute("headers", headers);
		model.addAttribute("widths", widths);
		model.addAttribute("items", items);
		
		return "excelView";
	}
	
	// 월근태현황
//	@GetMapping("/monthstatus")
//	public String monthStatus(Model model) {
//		List<WorkAdminAttendanceDto> attendanceDtos = workService.getMonthStatusList();
//		model.addAttribute("attendanceDtos",attendanceDtos);
//		return "work/monthly-status";
//	}
	
}
