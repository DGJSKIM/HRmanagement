package hrmanagement;

import java.util.*;

public interface InterTotalDAO {

	// 사원 로그인 //
	EmployeeDTO login_employee(Map<String, String> loginMap);

	List<NoticeDTO> noticelist();

	// 공지사항 내용 보여주는 메소드
	BoardDTO viewContent(Map<String, String> paraMap);

	// 공지 내용 보기 //
	NoticeDTO viewContent(String notice_id);

	// 일반 게시글 내용 보기 //
	BoardDTO viewContent2(String post_id);

	// 공지사항 조회수 증가 메소드 //
	void updateViewCount(String notice_id);

	// 공지사항 작성 메소드 //
	int notice_write(NoticeDTO ndto);

	// 공지사항 수정 메소드 //
	int notice_update(Map<String, String> paraMap);

	// 공지사항 삭제 메소드 //
	int deleteBoard(String notice_id);

	// 일반게시판 글 삭제 메소드 //
	int deleteBoard2(String post_id);

	// 일반 게시판 글 목록 보기 //
	List<BoardDTO> boardlist();

	// 원글에 대한 댓글 가져오는 메소드 //
	List<CommentDTO> commentList(String post_id);

	// 일반게시판 글 작성 메소드
	int write(BoardDTO bdto);

	// 출근시간 입력 메소드
	List<CommuteDTO> commute_start(CommuteDTO cmmdto);

	// 글 수정하기 //
	int updateBoard(Map<String, String> paraMap);

	// 댓글 작성하는 메소드
	int writeComment(CommentDTO cmdto);

	// 일주일간 일자별 게시글 작성건수 //
	Map<String, Integer> statisticsByWeek();

	// 이번달 일자별 게시글 작성건수 //
	List<Map<String, String>> statisticsByCurrentMonth();

	// 개인 월급 관리-조회 //
	EmployeeDTO search_employee_salary(Map<String, String> paraMap);

	// 개인 월급 관리 - 수정 //
	int update_salary(Map<String, String> paraMap);

	// 전체 부서 정보 조회 메소드
	List<DeptDTO> deptList();

	// 부서 신설하는 메소드
	int dept_insert(DeptDTO ddto);

	// 부서 수정에서 부서번호를 select 해오는 메소드
	DeptDTO selectDept(int department_Id);

	// 부서 정보 수정하는 메소드
	int updateDept(DeptDTO ddto);

	// 부서 정보 삭제하는 메소드
	int deleteDept(int dept_id);

	// 사원개인정보 조회 메소드
	EmployeeDTO select_empInfo(EmployeeDTO employee);

	// 사원개인정보 수정 메소드
	int update_emp_info(EmployeeDTO employee);

	// 사원 번호 유무를 확인해주는 메소드
	List<EmployeeDTO> emp_id_list();

	// 신규 입사자 정보 추가하는 메소드(insert)
	int emp_insert_sql(EmployeeDTO employee);

	// 부서 번호 유무를 확인해주는 메소드
	List<DeptDTO> dept_id_list();

	// 일반사원로그인 - 사원번호 검색//
	EmployeeDTO emp_id(Map<String, String> paraMap);

	// 일반사원로그인 - 사원명으로 검색하기 //
	EmployeeDTO emp_name(Map<String, String> paraMap);

	// 일반사원로그인 -부서명으로 검색하기//
	List<EmployeeDTO> fk_dept_id(String dept_name);

	// 관리자 로그인 - 출퇴근시간 검색 조회 //
	List<CommuteDTO> view_commute_list(String emp_id);

	// 관리자 로그인 - 출퇴근시간 전체 조회 //
	List<CommuteDTO> view_commute_list();

	// 출퇴근시간 입력시 지각여부 체크 //
	List<CommuteDTO> c_status(CommuteDTO cmmdto, int fk_writer_emp_id);

	// 재직증명서 테이블에 입력 //
	List<CertDTO> insert_cert1(CertDTO cdto);

	// 재직증명서 출력 //
	List<CertDTO> certList(int emp_id, String use);

	// 경력증명서 테이블에 입력 //
	List<CertDTO> insert_cert2(CertDTO cdto);

	// 경력증명서 출력 //
	List<CertDTO> certList2(int emp_id, String use);

	// 경력증명서 출력 yes or no //
	void yn_y(String yn);

	// 부서 이름 검사 // 
	int dept_name_check(String dept_name);
	
	// 면접 일정 추가 //
	int management_inerview_insert(Map<String, String> paraMap);

	// 면접 일정 내역 조회 //
	int management_inerview_view();

	// 면접 일정 수정 //
	int management_inerview_update(Scanner sc);

	// 면접 일정 전체조회 //
	void management_empinfo_allview(Scanner sc);

	// 연차 신청 메소드 // 
	int YsinCheong(Map<String, String> paraMap, Scanner sc);

	// 연차 확인 //
	List<AnnualDTO> Ychacheck(String choiceno, Scanner sc);

	// 연차 확인 //
	List<AnnualDTO> Ychacheck();

	// 문서기한 //
	int Docinsert(EmployeeDTO member, Scanner sc);

	// 문서 조회 //
	StringBuilder DocView(EmployeeDTO employee, Scanner sc);

	// 문서 승인 전 목록 조회 //
	StringBuilder DocAproval(EmployeeDTO member, Scanner sc);

	 // 승인 or 반려 or 보류
	int Aproval(EmployeeDTO member, String choiceno, String docno, Scanner sc);

	// 퇴직자 관리 //
	int delete_emp(int emp_id);

	// 퇴직처리하기 전 한 번 보여주는 메소드 //
	EmployeeDTO manage_retirement(Map<String, String> paraMap);

	// 승인해야할 목록 
	StringBuilder DocAproval2(EmployeeDTO employee, Scanner sc);

	// 결제 가능 확인 //
	int DocCheck(EmployeeDTO employee, Scanner sc, String docno);

	// 내부문서 등록 //
	void InnerDocinsert(EmployeeDTO employee, Scanner sc);

	// 문서내용 변경 //
	int DocChange(EmployeeDTO employee, Scanner sc);

	// 이메일, 휴대폰번호, 주민번호 중복 여부 검사하는 메소드
	List<EmployeeDTO> emp_overlap_list();

	// 퇴근시간 입력 메소드 //
	List<CommuteDTO> commute_end(CommuteDTO cmmdto);

	// 퇴사 처리 전 사수번호 update 구분
	int update_manager_id(EmployeeDTO employee);

	// 부서장 번호 유무를 확인해주는 메소드
	public List<DeptDTO> dept_manager_id_list();

	// 댓글 작성 시 원글의 글 번호 확인 메소드 //
	List<BoardDTO> find_board(int n_post_id);

	// 우선결재 처리 메소드 //
	int Aproval2(EmployeeDTO member, String choiceno, String docno, Scanner sc);

	//체크 메소드 //
	int DocCheck2(EmployeeDTO employee, Scanner sc, String docno);
	
	//처리한 문서 조회 //
	StringBuilder DocView2(EmployeeDTO employee, Scanner sc);

	//연차승인 //
	int annual_approval(String vacation_id, Scanner sc);

}
