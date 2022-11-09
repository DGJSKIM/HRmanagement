package hrmanagement;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Pattern;

public class Controller_HR {

	InterTotalDAO tdao = new TotalDAO();
	EmployeeDTO employee = null; // 로그인 시 정보 저장
	ManagerDTO manager = null;

	// 로그인, 프로그램 종료 입력받는 메소드 //
	public void login(Scanner sc) {
		String menu_no = ""; // 입력받은 메뉴번호 저장

		do {
			System.out.println("\n=== 인사관리 프로그램 ===");
			System.out.println("1.로그인   2.프로그램종료");
			System.out.println("=====================");
			System.out.print("▷ 메뉴번호 입력 : ");
			menu_no = sc.nextLine(); // 메뉴번호 입력 "123123" "똘똘이"

			switch (menu_no) {
			case "1":
				employee = login_employee(sc); // 로그인 정보를 employee 객체에 저장
				if (employee != null) { // 로그인 되어있는 경우에만 출력
					switch (employee.getCk_manager_rank()) {
					case 1: // admin
						menu_admin(employee, sc);
						break;
					case 2: // 부서장
						menu_employee(employee, sc);
						break;
					case 3: // 일반사원
						menu_employee(employee, sc); // 일반사원 전용 게시판 메뉴에 들어간다.
						break;
					}// end of switch(employee.getCk_manager_rank())-------------------
				} // end of if(employee != null)-----------------
				break;
			case "2":
				ProjectDBConnection.closeConnection();
				break;

			default:
				System.out.println("[경고] 메뉴에 없는 번호입니다. \n");
				break;
			}// end of switch (menu_no)------------------------------------
		} while (!("2".equals(menu_no)));// end of do ~ while()------------------------
	}// end of public void login(Scanner sc)--------------

	// 로그인 해주는 메소드 //
	private EmployeeDTO login_employee(Scanner sc) {
		EmployeeDTO employee = null;

		System.out.println("\n>>> 로그인 <<<");
		// 사원번호(아이디) 입력받기
		System.out.print("사원번호 : ");
		String emp_id = sc.nextLine();

		// 비밀번호 입력받기
		System.out.print("비밀번호 : ");
		String passwd = sc.nextLine();

		Map<String, String> loginMap = new HashMap<>(); // 아이디, 비밀번호를 Map에 저장
		loginMap.put("emp_id", emp_id);
		loginMap.put("passwd", passwd);

		employee = tdao.login_employee(loginMap);

		if (employee != null) {
			System.out.println("\n >>> 로그인 성공!! <<<");
		} else {
			System.out.println("\n >>> 로그인 실패!! <<<");
		} // end of if()--------------------------------
		return employee;
	}// end of private EmployeeDTO login_employee(Scanner

	// 관리자,사장 전용 메뉴
	private void menu_admin(EmployeeDTO employee, Scanner sc) {
		String menu_no = ""; // 입력받은 메뉴번호 저장
		do {
			System.out.println("\n=== 메인 메뉴 " + employee.getName() + " 로그인중..===");
			System.out.println("1.로그아웃         2.사내게시판 \n3.부서관리         4.관리항목  \n5.출/퇴근시간조회    6.휴가승인");
			System.out.println("============================");

			System.out.print("▷ 메뉴번호 선택 : ");
			menu_no = sc.nextLine();

			switch (menu_no) {
			case "1": // 1.로그아웃
				break;

			case "2": // 2.사내게시판
				board_total_admin(employee, sc);
				break;

			case "3": // 3.부서관리
				dept_Start(sc);
				break;

			case "4": // 4.관리항목
				management(employee, sc);

				break;
			case "5": // 4.출퇴근시간조회
				tdao.view_commute_list();
				break;
			case "6": // 휴가승인

				break;

			default:
				System.out.println(">> 메뉴에 없는 번호입니다. <<\n");
				break;
			}// end of switch (menu_no)
		} while (!"1".equals(menu_no)); // end of do~ while()---------------------------------------
	}// end of private void menu_admin(EmployeeDTO employee, Scanner sc)
		// ------------------------------------

	// 출퇴근시간 조회해주는 메소드
	private void view_commute(EmployeeDTO employee, Scanner sc) {
		String menuno = "";

		System.out.print("▷ 1.전체사원 조회   2.사원별 조회 : ");
		menuno = sc.nextLine();

		switch (menuno) {
		case "1": // 전체사원 출퇴근 시간 조회
			view_commute_all();
			break;

		case "2": // 사원별 출퇴근 시간 조회
			view_commute_indi(sc);
			break;

		default:
			break;
		}
	}// end of private void view_commute(EmployeeDTO employee2, Scanner
		// sc)()---------------

	// 출퇴근시간 검색조회//
	private void view_commute_indi(Scanner sc) {
		System.out.print("사원번호를 입력하세요 : ");
		String emp_id = sc.nextLine();
		List<CommuteDTO> commute_list = tdao.view_commute_list(emp_id);
		if (commute_list.size() > 0) {
			// 사원이 있는 경우

			System.out.println("\n=================[출퇴근시간 조회]=================");
			System.out.println("기본번호\t사원번호\t출근시간\t\t퇴근시간\t\t지각여부");
			System.out.println("================================================");

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < commute_list.size(); i++) {
				sb.append(commute_list.get(i).toString() + "\n");
				// boardList.get(i) 는 BoardDTO 이다.
			} // end of for()--------------------------------------

			System.out.println(sb.toString());

		} // end of if
			// 사원이 없는 경우
		else {
			System.out.println(">> 조회하고자 하는 사원의 정보가 없습니다. <<\n");
		} // end of if()-----------------------------
	}// end of private void view_commute_indi(Scanner sc)
		// ----------------------------------

	// 전체사원 출퇴근시간 조회 //
	private void view_commute_all() {
		List<CommuteDTO> commute_list = tdao.view_commute_list();
		if (commute_list.size() > 0) { // 출퇴근 테이블이 존재하는 경우

		} else {
			// 출퇴근 테이블이 존재하는 경우
			System.out.println(">> 출퇴근 정보가 입력된 사원이 없습니다. <<\n");
		}
	}// 출퇴근시간 조회해주는 메소드

	// 관리항목 //
	private void management(EmployeeDTO employee, Scanner sc) {

		if (employee.getCk_manager_rank() != 1) {

			String menu_no = ""; // 입력받은 메뉴번호 저장
			do {
				System.out.println("\n================= 관리 메뉴 " + employee.getName() + " 로그인중.. =================");
				System.out.println("1.면접관리  2.급여관리  3. 직원 인적사항 관리  4.전직원연차조회"
						+ "5.연차승인 	6.문서결재		7.문서우선결재  8. 처리한문서확인 9.뒤로가기");
				System.out.println("=============================================================");

				System.out.print("▷ 메뉴번호 선택 : ");
				menu_no = sc.nextLine();

				switch (menu_no) {
				case "1": // 1.면접관리
					management_inerview(employee, sc);
					break;

				case "2": // 2.급여관리
					management_salary(employee, sc);

					break;

				case "3": // 3.직원 인적사항 관리
					management_empinfo(employee, sc);

					break;
				case "4": // 4.전직원연차조회
					Ychacheck(employee, sc);

					break;

				case "5": // 5.연차최종승인
					annual_approval(sc);
					break;

				case "6": // 6.문서결재
					DocAproval_2(employee, sc);
					break;

				case "7": // 7.문서우선결재
					DocAproval(employee, sc);
					break;
				case "8":
					DocView2(employee, sc);
					break;

				default:
					System.out.println(">> 메뉴에 없는 번호입니다. <<\n");
					break;
				} // end of switch (menu_no)
			} while (!"9".equals(menu_no)); // end of do~ while()---------------------------------------
		} else {

			String menu_no = ""; // 입력받은 메뉴번호 저장
			do {
				System.out.println("\n================= 관리 메뉴 " + employee.getName() + " 로그인중.. =================");
				System.out.println("1.면접관리  2.급여관리  3. 직원 인적사항 관리  4.전직원연차조회" + "5.연차승인   6.뒤로가기");
				System.out.println("=============================================================");

				System.out.print("▷ 메뉴번호 선택 : ");
				menu_no = sc.nextLine();

				switch (menu_no) {
				case "1": // 1.면접관리
					management_inerview(employee, sc);
					break;

				case "2": // 2.급여관리
					management_salary(employee, sc);

					break;

				case "3": // 3.직원 인적사항 관리
					management_empinfo(employee, sc);

					break;
				case "4": // 4.전직원연차조회
					Ychacheck(employee, sc);
					break;

				case "5": // 5.연차최종승인
					annual_approval(sc);
					break;
				case "6": // 5.연차최종승인
					break;
				default:
					System.out.println(">> 메뉴에 없는 번호입니다. <<\n");
					break;
				} // end of switch (menu_no)
			} while (!"6".equals(menu_no)); // end of do~ while()---------------------------------------

		}

	}

	// 연차 승인
	private void annual_approval(Scanner sc) {
		String vacation_id = "";
		int vacation_no = 0;
		do {
			System.out.print("▷ 처리할 연차의 연차번호를 입력하세요.");
			vacation_id = sc.nextLine();

			try {
				vacation_no = Integer.parseInt(vacation_id);
				break;
			} catch (NumberFormatException e) {
				System.out.println("[경고] 연차번호는 숫자만 입력해주세요.\n");
			}
		} while (true);

		int n = tdao.annual_approval(vacation_id, sc);

		if (n == 1) {
			System.out.println("연차 처리 완료!");
		} else {
			System.out.println("연차를 처리하지 않으셨습니다.");
		}

	}// end of private void management(EmployeeDTO employee, Scanner sc) -----------------------------------

	// 면접관리 메뉴 호출 //
	private void management_inerview(EmployeeDTO employee, Scanner sc) {
		String menu_no = "";

		do {
			System.out.println("\n==== 면접 관리 " + employee.getName() + " 로그인중..====");
			System.out.println("1.면접일정 조회    2.면접일정 추가  \n3.면접일정 변경    4.뒤로가기");
			System.out.println("==============================");

			System.out.print("▷ 메뉴번호 선택 : ");
			menu_no = sc.nextLine();

			switch (menu_no) {
			case "1":// 면접일정 조회
				tdao.management_inerview_view();
				break;
			case "2":// 면접일정 추가
				interview_insert(sc);
				break;
			case "3":// 면접일정 변경
				tdao.management_inerview_view();
				tdao.management_inerview_update(sc);
//				management_inerview_update(sc);
				break;
			case "4":// 뒤로가기
				break;
			default:
				System.out.println(">>[경고] 메뉴에 없는 번호입니다. <<\n");
				break;
			}// end of switch~case()---------------------

		} while (!"4".equals(menu_no)); // end of do ~ while()-----------------------------------

	}// end of private void management_inerview(EmployeeDTO employee, Scanner sc)--------------

	// 면접일정 추가
	private void interview_insert(Scanner sc) {
		Map<String, String> paraMap = new HashMap<>();

		String dept_name = "";
		String duty = "";
		String applicant_name = "";
		String applicant_mobile = "";
		String interview_date = "";

		System.out.println("======== 면접일정 추가 ========");
		System.out.print("1.부서명 : ");
		dept_name = sc.nextLine();
		System.out.print("2.직무 : ");
		duty = sc.nextLine();
		System.out.print("3.지원자명 : ");
		applicant_name = sc.nextLine();
		System.out.print("4.휴대폰 번호 : ");
		applicant_mobile = sc.nextLine();
		System.out.print("5.면접 날짜 : ");
		interview_date = sc.nextLine();
		System.out.println("============================");

		paraMap.put("dept_name", dept_name);
		paraMap.put("duty", duty);
		paraMap.put("applicant_name", applicant_name);
		paraMap.put("applicant_mobile", applicant_mobile);
		paraMap.put("interview_date", interview_date);

		int n = tdao.management_inerview_insert(paraMap);

		if (n == 1) {
			System.out.println("면접 일정 추가 성공!");
		} else {
			System.out.println("면접 일정 추가 실패!!");
		}
	}// end of private void interview_insert(Scanner sc) ----------------------

	// 직원 인적사항 관리 메뉴출력 //
	private void management_empinfo(EmployeeDTO employee, Scanner sc) {
		String s_no = "";

		do {

			System.out.println("==================== 직원 인적사항 관리 =====================");
			System.out.println("1.입사자 관리   2.퇴사자 관리   3.직원 조회   4.뒤로가기 ");
			System.out.println("=========================================================");
			System.out.print("▷ 메뉴번호 선택 : ");
			s_no = sc.nextLine();

			switch (s_no) {
			case "1": // 입사자 관리
				new_emp_insert(employee, sc);
				break;

			case "2": // 퇴사자 관리
				delete_emp(employee, sc); // 0803
//	        	tdao.delete_emp(employee.getEmp_id()); 
				break;

			case "3": // 직원 조회
				search_emp_info(employee, sc);
				break;

			case "4": // 뒤로가기
				break;

			default:
				System.out.println(">> 메뉴에 있는 번호만 선택해주세요. <<\n");
				break;
			}

		} while (!"4".equals(s_no));
	} // private void management_empinfo(EmployeeDTO employee, Scanner sc) ---------------

	// 퇴사자 처리 메소드
	private void delete_emp(EmployeeDTO employee, Scanner sc) {

		Map<String, String> paraMap = new HashMap<>();
		int emp_id = 0;
		String s_emp_id = "";
		EmployeeDTO member = new EmployeeDTO();

		do {
			System.out.print("\n▶ 퇴직자 사원번호 : ");
			s_emp_id = sc.nextLine();

			try {
				paraMap.put("emp_id", s_emp_id);
				member = tdao.manage_retirement(paraMap); // 08030803 수정
				emp_id = Integer.parseInt(s_emp_id);

			} catch (NumberFormatException e) {
				System.out.println("[경고] 삭제할 사원 번호는 숫자로만 입력하세요.\n");
				continue;
			}

			if (employee != null) {
				System.out.println("\n------[퇴직자(퇴직 예정자) 정보]------");
				System.out.println("사원번호 : " + member.getEmp_id());
				System.out.println("부서번호 : " + member.getFk_dept_id());
				System.out.println("직위    : " + member.getRank());
				System.out.println("이름    : " + member.getName());
				System.out.println("직속상관번호 : " + member.getFk_manager_id() + "\n");
				break;
			} else
				System.out.println("[경고]해당하는 사원 번호는 존재하지 않습니다.");
		} while (true);

		StringBuilder sb = new StringBuilder();
		sb = tdao.DocAproval2(member, sc);

		if (sb.length() != 0) {
			System.out.println("[경고] 선택한 사원이 결재해야하는 문서가 남아있으므로, 퇴사처리가 불가능합니다.\n");
		} else {

			int n = 0;
			do {
				System.out.print("▷ 위 사원을 퇴직처리 하시겠습니까?[Y/N] : ");
				String yn = sc.nextLine();
				if ("y".equalsIgnoreCase(yn)) {
					n = tdao.delete_emp(member.getEmp_id());
					tdao.update_manager_id(member);
					break;
				}

				else if ("n".equalsIgnoreCase(yn)) {
					System.out.println(">> 퇴직처리를 취소하였습니다. <<");
					break;
				}

				else {
					System.out.println("[경고] Y 또는 N 만 입력하세요!!\n");
				}

			} while (true);

			if (n == 1) {
				System.out.println("퇴직처리 성공");
			}
		}
	} // end of private void delete_emp(EmployeeDTO employee, Scanner sc) --------------------

	// 직원 조회
	private void search_emp_info(EmployeeDTO employee, Scanner sc) {
		String menu_no = "";
		String emp_id = "";

		Map<String, String> paraMap = new HashMap<>();

		do {

			System.out.println("==================== 직원 조회 =====================");
			System.out.println("1.전체사원조회   2.사원번호검색   3. 이름으로 검색   4. 부서명으로 검색  5.뒤로가기");
			System.out.println("=========================================================");
			System.out.print("▷ 메뉴번호 선택 : ");
			menu_no = sc.nextLine();

			switch (menu_no) {
			case "1": // 전체사원조회
				tdao.management_empinfo_allview(sc);
				break;

			case "2": // 사원번호검색
				emp_id(sc);
				break;

			case "3": // 이름으로 검색
				emp_name(sc);
				break;

			case "4": // 부서명으로 검색
				dept_name(sc);
				break;

			case "5": // 뒤로가기
				break;

			default:
				System.out.println(">> 메뉴에 있는 번호만 선택해주세요. <<\n");
				break;
			}
		} while (!"5".equals(menu_no));
	}// end of private void search_emp_info(EmployeeDTO employee, Scanner sc) -------------------------

	// 급여관리 메소드 //
	private void management_salary(EmployeeDTO employee, Scanner sc) {
		List<EmployeeDTO> empList = new ArrayList<>();

		String menu_no = ""; // 입력받은 메뉴번호 저장
		do {
			System.out.println(
					"\n====================  사원조회( " + employee.getName() + " )(관리자)로그인중..  ====================");
			System.out.println("1.개인월급관리 2. 뒤로가기");
			System.out.println("=============================================================");

			System.out.print("▷ 메뉴번호 선택 : ");
			menu_no = sc.nextLine();

			switch (menu_no) {
			case "1": // 1.개인월급관리
				employee_salary(employee, sc);
				break;

			case "2": // 2.뒤로가기
				break;

			default:
				System.out.println(">> 메뉴에 없는 번호입니다. <<\n");
				break;
			} // end of switch (menu_no)
		} while (!"2".equals(menu_no)); // end of do~ while()---------------------------------------
	}// end of private void management_salary(EmployeeDTO employee, Scanner sc) --------------------------------

	// 개인 월급 관리
	private void employee_salary(EmployeeDTO employee, Scanner sc) {

		System.out.println("\n>>> 개인 월급 관리 <<<");

		boolean check = true;
		int emp_id = 0;
		String s_emp_id = "";
		do { // ♤

			System.out.println("급여 변경을 희망하는 사원 번호를 입력하세요");
			System.out.print("▷ 사원번호 : ");
			s_emp_id = sc.nextLine();

			if (s_emp_id.trim().isEmpty()) {
				System.out.println("[경고] 조회할 사원번호를 입력하여 주시기 바랍니다. \n");
			}

			try {
				emp_id = Integer.parseInt(s_emp_id);
			} catch (NumberFormatException e) {
				System.out.println("[경고] 조회할 사원번호는 숫자로만 입력하세요.\n");
			}

			List<EmployeeDTO> emp_id_list = tdao.emp_id_list();

			int flag = 0;

			for (int i = 0; i < emp_id_list.size(); i++) {
				// 입력할 사원번호가 존재하는 경우
				if (emp_id == emp_id_list.get(i).getEmp_id()) {
					flag = 1;
					break;
					// 입력할 사원번호가 존재하지 않는 경우
				} else if (emp_id != emp_id_list.get(i).getEmp_id()) {
					flag = 2;
				}
			}
			if (flag == 1) {
				check = true;
			} else {
				check = false;
				System.out.println("[경고] 존재하는 사원 번호를 입력하세요.\n");
			}

		} while (!check);

		s_emp_id = Integer.toString(emp_id);

		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("emp_id", s_emp_id);

		employee = tdao.search_employee_salary(paraMap);

		if (employee != null) {
			System.out.println("==========================================");
			System.out.println("사원번호 \t사원명 \t직급 \t기본급 \t연봉 ");
			System.out.println("==========================================");
			System.out.println(employee.getEmp_id() + "\t" + employee.getName() + "\t" + employee.getRank() + "\t"
					+ employee.getSalary() + " 만원\t" + (employee.getSalary() * 12) + "만원");
			System.out.println("==========================================\n");
			int n = 0;
			do {
				System.out.print(">> 위 사원의 급여를 변경하시겠습니까? [Y/N] : ");
				String yn = sc.nextLine();
				if ("Y".equalsIgnoreCase(yn)) {
					System.out.print(">> 변경할 급여를 입력하세요 : ");
					String salary = sc.nextLine();

					if (!salary.trim().isEmpty()) {
						paraMap.put("salary", salary);
						n = tdao.update_salary(paraMap);
						break;
					} else {
						System.out.println("[경고] 변경할 급여는 공백을 입력할 수 없습니다. ");
						break;
					}
				} else if ("N".equalsIgnoreCase(yn)) {
					System.out.println(">> 급여변경이 취소되었습니다.");
					break;
				} else {
					System.out.println("[경고]Y 또는 N만 입력이 가능합니다.");
				}
			} while (true);// end of do~ while()---------------------------------------

			if (n == 1) {
				System.out.println(">> 급여가 정상적으로 변경되었습니다. <<");
			} else {
			}
		} else {
			System.out.println(">>> 사원번호 " + emp_id + " 인 사원은 존재하지 않습니다. <<<");
		}// end of if else ----------------------------
	}// end of private void employee_salary(EmployeeDTO employee, Scanner sc)------------------------

	// 관리자용 사내게시판 메뉴
	private void board_total_admin(EmployeeDTO employee, Scanner sc) {
		String menu_no = ""; // 입력받은 메뉴번호 저장
		do {
			System.out.println("\n=== 사내게시판( " + employee.getName() + " )로그인중..===");
			System.out.println("1.공지사항       2.일반게시판\n3.뒤로가기 ");
			System.out.println("==============================");

			System.out.print("▷ 메뉴번호 선택 : ");
			menu_no = sc.nextLine();

			switch (menu_no) {
			case "1": // 1.공지사항(작성,조회,삭제)
				notice_admin(employee, sc);
				break;

			case "2": // 2.일반게시판
				board_general(employee, sc);
				break;

			case "3": // 3.뒤로가기
				break;
			default:
				System.out.println("[경고] 메뉴에 없는 번호입니다.\n");
				break;
			}// end of switch (menu_no)
		} while (!"3".equals(menu_no)); // end of do~ while()---------------------------------------

	}// end of private void board_total_admin(EmployeeDTO employee, Scanner sc)-----------------------------

	
	
	
	// 관리자 공지사항메뉴 (조회 작성 수정 삭제)
	private void notice_admin(EmployeeDTO employee, Scanner sc) {
		String menu_no = ""; // 입력받은 메뉴번호 저장
		do {
			System.out.println("\n=== 공지사항 ( " + employee.getName() + " )로그인중..===");
			System.out.println("1.공지사항 목록조회 2.공지사항 내용보기  \n3.공지사항 작성    4.공지사항수정   \n5.공지사항 삭제    6.뒤로가기 ");
			System.out.println("==============================");

			System.out.print("▷ 메뉴번호 선택 : ");
			menu_no = sc.nextLine();

			switch (menu_no) {
			case "1": // 1.공지사항 목록 조회
				notice_list();
				break;

			case "2": // 2. 공지사항 내용보기
				notice_content(employee.getEmp_id(), sc);
				break;

			case "3": // 2. 공지사항 작성
				notice_wirte(employee, sc);
				break;

			case "4": // 3. 공지사항 수정
				notice_update(employee, sc);
				break;

			case "5": // 4. 공지사항 삭제
				notice_delete(employee, sc);
				break;

			case "6": // 5. 뒤로가기

				break;

			default:
				System.out.println(">> 메뉴에 없는 번호입니다. <<\n");
				break;
			}// end of switch (menu_no)
		} while (!"6".equals(menu_no)); // end of do~ while()---------------------------------------

	}// end of private void notice_admin(EmployeeDTO employee, Scanner sc)-----------------------------------

	
	
	
	// 공지사항 삭제 메소드 //
	private int notice_delete(EmployeeDTO employee, Scanner sc) {
		int result = 0;

		System.out.println("\n >>> 글 삭제하기 <<<");
		String notice_id = "";
		do {
			System.out.print("▷ 삭제할 글번호 : ");
			notice_id = sc.nextLine(); // "똘똘이" , "1238763294"

			try {
				Integer.parseInt(notice_id);
				break;
			} catch (NumberFormatException e) {
				System.out.println("[경고]삭제할 글번호는 숫자로만 입력하세요!! \n");
			}
		} while (true);

		NoticeDTO ndto = tdao.viewContent(notice_id);

		if (ndto != null) { // 삭제할 글 번호가 글 목록에 존재하는 경우
			if (!(ndto.getFk_writer_emp_id() == employee.getEmp_id())) {
				// 삭제할 글 번호가 다른사람이 쓴 글인경우
				System.out.println("[경고] 다른 사용자의 글은 삭제 불가합니다.!! \n");
			} else {
				// 삭제할 글 번호가 자신이 쓴 글인경우
				System.out.print("▷ 글암호 : ");
				String passwd = sc.nextLine();

				if (ndto.getPasswd() == Integer.parseInt(passwd)) {
					// 글 암호가 일치하는 경우
					System.out.println("-----------------------------------------");
					System.out.println("[공지사항 제목] " + ndto.getTitle());
					System.out.println("[공지사항 내용] " + ndto.getContent());
					System.out.println("-----------------------------------------");
					do {
						System.out.print("▷ 정말로 글을 삭제하시겠습니까?[Y/N] : ");
						String yn = sc.nextLine();

						if ("y".equalsIgnoreCase(yn)) {
							result = tdao.deleteBoard(notice_id);
							// result 1(정상적으로 글이 삭제된 경우) 또는 -1 (SQLException 발생으로 글삭제가 실패한 경우)
							break;
						} else if ("n".equalsIgnoreCase(yn)) {
							System.out.println(">> 글 삭제를 취소하였습니다. <<");
							break;
						} else {
							System.out.println("[경고] Y 또는 N 만 입력하세요!!\n");
						}
					} while (true);
				} else {
					// 글 암호가 일치하지 않는 경우
					System.out.println("[경고] 입력하신 글 암호가 작성시 입력한 글 암호와 일치하지 않으므로 삭제 불가합니다!! \n");
				}
			}
		} else {
			// 삭제할 글 번호가 글 목록에 존재하지 않는경우
			System.out.println("수정할 글 번호 " + notice_id + " 는 글 목록에 존재하지 않습니다.");
		}
		return result;
		// 0 또는 1 또는 -1
	}// end of private int notice_delete(EmployeeDTO employee, Scanner sc)--------------------

	
	
	// 공지사항 수정 메소드
	private int notice_update(EmployeeDTO employee, Scanner sc) {

		int result = 0;

		System.out.println("\n >>> 글 수정하기 <<<");
		String notice_id = "";
		do {
			System.out.print("▷ 수정할 공지번호 : ");
			notice_id = sc.nextLine(); // "똘똘이" , "1238763294"

			try {
				Integer.parseInt(notice_id);
				break;
			} catch (NumberFormatException e) {
				System.out.println("[경고]수정할 글번호는 숫자로만 입력하세요!! \n");
			}
		} while (true);

		NoticeDTO ndto = tdao.viewContent(notice_id);

		if (ndto != null) {
			// 수정할 글 번호가 글 목록에 존재하는 경우

			if (!(ndto.getFk_writer_emp_id() == (employee.getEmp_id()))) {
				// 공지사항을 다른 관리자가 작성한 경우
				System.out.println("[경고] 다른 관리자의 글은 수정 불가합니다.!! \n");
			} else {
				// 수정할 글 번호가 자신이 쓴 글인경우
				System.out.print("▷ 공지사항 암호 : ");
				String notice_passwd = sc.nextLine();
				int n_notice_passwd = 0;
				try {
					n_notice_passwd = Integer.parseInt(notice_passwd);
				} catch (NumberFormatException e) {
					System.out.println();
				}
				if (ndto.getPasswd() == n_notice_passwd) {
					// 글 암호가 일치하는 경우
					System.out.println("-----------------------------------------");
					System.out.println("[수정전 공지사항 제목] " + ndto.getTitle());
					System.out.println("[수정전 공지사항 내용] " + ndto.getContent());
					System.out.println("-----------------------------------------");

					System.out.print("▷ 공지사항 제목[변경하지 않으려면 엔터] : ");
					String subject = sc.nextLine();

					if (subject != null && subject.trim().isEmpty()) {
						subject = ndto.getTitle(); // 변경하지 않으려면 기존 subject 내용을 입력
					}

					System.out.print("▷ 공지사항 내용[변경하지 않으려면 엔터] : ");
					String content = sc.nextLine();

					if (content != null && content.trim().isEmpty()) {
						content = ndto.getContent(); // 변경하지 않으려면 기존 contents 내용을 입력
					}

					if (subject.length() > 20 || content.length() > 500) {
						System.out.println("[경고] 공지사항 제목은 최대 20글자이며, 글내용은 최대 500글자 이내로 작성해야 합니다. ");
					}

					Map<String, String> paraMap = new HashMap<>();
					paraMap.put("title", subject);
					paraMap.put("content", content);
					paraMap.put("notice_id", notice_id);

					result = tdao.notice_update(paraMap); // 글 수정하기
					// 1(글 수정이 성공된 경우 ) or -1(글수정을 하려고 하나 SQLException이 발생한 경우)
				} else {
					// 글 암호가 일치하지 않는 경우
					System.out.println("[경고] 입력하신 글 암호가 작성시 입력한 글 암호와 일치하지 않으므로 수정 불가합니다!! \n");
				}
			}
		} else {
			// 수정할 글 번호가 글 목록에 존재하지 않는경우
			System.out.println("수정할 글 번호 " + notice_id + " 는 글 목록에 존재하지 않습니다.");
		}
		return result;
	}// end of private int notice_update(EmployeeDTO employee, Scanner sc) ---------------------------
	
	

	// 공지사항 글 작성하기 //
	private int notice_wirte(EmployeeDTO employee, Scanner sc) {
		int result = 0;
		System.out.println("\n>> 공지사항 작성 <<");

		String subject;
		String content;
		String passwd;
		int flag = 0;
		char tmp;

		System.out.println("1. 작성자명 : " + employee.getName());
		do {
			System.out.print("2. 제목 : ");
			subject = sc.nextLine();
			if (subject.trim().isEmpty()) {
				System.out.println("[경고]제목을 입력하세요");
			}
		} while (subject.trim().isEmpty());

		do {
			System.out.print("3. 내용 : ");
			content = sc.nextLine();
			if (content.trim().isEmpty()) {
				System.out.println("[경고]내용을 입력하세요");
			}
		} while (content.trim().isEmpty());

		do {
			System.out.print("4. 비밀번호 : ");
			passwd = sc.nextLine();
			if (passwd.trim().isEmpty()) {
				System.out.println("[경고]비밀번호를 입력하세요");
			}

			for (int i = 0; i < passwd.length(); i++) {
				tmp = passwd.charAt(i);
				if (Character.isDigit(tmp)) {
				} else {
					flag = 1;
				}
			} // end of for(int i = 0; i< passwd.length(); i++)
		} while (passwd.trim().isEmpty() && flag == 0);

		flag = 0;
		do {
			System.out.print(">> 공지사항 작성 하시겠습니까?[Y/N] => ");
			String yn = sc.nextLine();
			flag = 0;

			if ("y".equalsIgnoreCase(yn)) {
				flag = 1;
				break;
			} else if ("n".equalsIgnoreCase(yn)) {
				break;
			} else {
				System.out.println(">> Y 또는 N 만 입력하세요!! << \n");
			}
		} while (true);
		int flag2 = 1;
		if (flag == 1) {

			NoticeDTO ndto = new NoticeDTO();
			ndto.setFk_writer_emp_id(employee.getEmp_id());
			ndto.setTitle(subject);
			ndto.setContent(content);
			try {
				ndto.setPasswd(Integer.parseInt(passwd));
			} catch (NumberFormatException e) {
				System.out.println("[경고] 공지사항 작성에 실패하였습니다.");
				flag2 = -1;
			}
			if (flag2 == 1) {
				result = tdao.notice_write(ndto); // 정상이면 1(commit) , 실패하면 -1(rollback)
			} else {
			}

		} else { // 글쓰기를 취소한 경우
			result = 0;
		}

		return result;

	}// end of private int notice_wirte(EmployeeDTO employee, Scanner sc)--------------------------------------------

	
	
	// 공지사항 내용보여주는 메소드 //
	private void notice_content(int emp_id, Scanner sc) {
		StringBuilder sb = new StringBuilder();
		System.out.println("\n>>> 글 내용보기 <<<");

		System.out.print("▷ 글번호 : ");
		String notice_id = sc.nextLine();

		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("notice_id", notice_id);

		NoticeDTO ndto = tdao.viewContent(notice_id);

		if (ndto != null) {
			System.out.println("[공지제목] " + ndto.getTitle());
			System.out.println("[공지내용] " + ndto.getContent());

			if (!(ndto.getFk_writer_emp_id() == emp_id)) {// 다른사람이 쓴 글이라면 update
				// spring 에서는 파라미터로 무조건 한개만 전달해야 하기 때문에 map에 넣어서 전달한다.
				tdao.updateViewCount(notice_id); // 리턴값이 필요 없음(조회수가 오르면 끝)
			}
		} else {
			// 존재하지 않는 글번호를 입력한 경우 "똘똘이" , "1231927361289"
			System.out.println(">>> 공지번호 " + notice_id + " 은 공지사항 목록에 존재하지 않습니다. <<<");
		}
	}// end of private void notice_content(int emp_id, Scanner sc)--------------------------------

	
	
	// 일반사원 로그인 - 사원 게시판 출력 //
	private void menu_employee(EmployeeDTO employee, Scanner sc) {
		String menu_no = ""; // 입력받은 메뉴번호 저장
		do {
			System.out.println("\n====================  " + employee.getName() + "(사원)로그인중..  ====================");
			System.out.println("1. 로그아웃   2.개인메뉴   3.증명서   4.사내게시판   5.조회");
			System.out.println("=============================================================");

			System.out.print("▷ 메뉴번호 선택 : ");
			menu_no = sc.nextLine();

			switch (menu_no) {
			case "1": // 1. 로그아웃

				break;

			case "2": // 2. 개인메뉴
				menu_personal(employee, sc);
				break;

			case "3": // 3. 증명서
				certificate(employee, sc);
				break;

			case "4": // 4. 사내게시판
				board_total(employee, sc);
				break;

			case "5": // 5. 조회
				menu_lookup(employee, sc);
				break;
			default:
				System.out.println(">> 메뉴에 없는 번호입니다. <<\n");
				break;
			}// end of switch (menu_no)
				// -----------------------------------------------------

		} while (!"1".equals(menu_no)); // end of do~ while()---------------------------------------

	}// end of private void menu_Board(EmployeeDTO employee, Scanner sc)

	
	
	
	// 일반사원로그인 - 조회 //
	private void menu_lookup(EmployeeDTO employee2, Scanner sc) {

		String menu_no = ""; // 입력받은 메뉴번호 저장
		do {
			System.out
					.println("\n====================  개인정보" + employee.getName() + "(사원)로그인중..  ====================");
			System.out.println("1.조직도   2.사원정보조회   3 뒤로가기");
			System.out.println("=============================================================");

			System.out.print("▷ 메뉴번호 선택 : ");
			menu_no = sc.nextLine();

			switch (menu_no) {
			case "1": // 1.조직도
				Jojicdo j = new Jojicdo();
				j.jojikdo();
				break;

			case "2": // 2.사원정보조회
				menu_emp_lookup(employee, sc);

				break;

			case "3": // 3.뒤로가기
				break;

			default:
				System.out.println(">> 메뉴에 없는 번호입니다. <<\n");
				break;
			}// end of switch (menu_no)
		} while (!"3".equals(menu_no)); // end of do~ while()---------------------------------------
	}// end of private void menu_lookup(EmployeeDTO employee2, Scanner sc)------------------
	
	

	// 일반사원로그인 -사원정보 조회 //
	private void menu_emp_lookup(EmployeeDTO employee2, Scanner sc) {
		String menu_no = ""; // 입력받은 메뉴번호 저장
		do {
			System.out
					.println("\n====================  개인정보" + employee.getName() + "(사원)로그인중..  ====================");
			System.out.println("1.사원번호 검색   2.이름 검색   3. 부서명 검색   4.뒤로가기");
			System.out.println("=============================================================");

			System.out.print("▷ 메뉴번호 선택 : ");
			menu_no = sc.nextLine();

			switch (menu_no) {
			case "1": // 1.사원번호검색
				emp_id(sc);
				break;

			case "2": // 2.이름검색
				emp_name(sc);
				break;

			case "3": // 3.부서명 검색
				dept_name(sc);
				break;

			case "4": // 4.뒤로가기
				break;

			default:
				System.out.println(">> 메뉴에 없는 번호입니다. <<\n");
				break;
			}// end of switch (menu_no)
		} while (!"4".equals(menu_no));

	}// end of private void menu_emp_lookup(EmployeeDTO employee2, Scanner sc)-------------------
	
	

	// 일반사원 로그인 -부서명으로 검색하기
	private void dept_name(Scanner sc) {

		System.out.println("\n>>> 부서명으로 검색하기 <<<");

		System.out.print("▷ 부서명 : ");
		String dept_name = sc.nextLine();

		List<EmployeeDTO> fk_dept_id = tdao.fk_dept_id(dept_name);

		if (fk_dept_id.size() != 0) {

			System.out.println("\n-------------------------- 사원정보 조회 -----------------------------------\n"
					+ "사원명    직위        이메일\t       연락처\t    입사일자\t      부서명  \n"
					+ "-------------------------------------------------------------------------\n");

			StringBuilder sb = new StringBuilder();

			for (EmployeeDTO empdto : fk_dept_id) {
				sb.append(empdto.getName() + "   " + empdto.getRank() + "   " + empdto.getUq_email() + "   "
						+ empdto.getUq_mobile() + "   " + empdto.getHire_date().substring(0, 10) + "   "
						+ empdto.getDept_name() + "\n");
			}

			System.out.println(sb.toString());
		} else {
			// 존재하지 않는 부서명을 입력한 경우
			System.out.println(">> 부서명 " + dept_name + " 에 해당하는 사원은 사원목록에 존재하지 않습니다. << \n");
		}

	} // end of dept_name(Scanner sc) -----------------------------
	
	

	// 사원명으로 검색하기 0803
	private void emp_name(Scanner sc) {
		System.out.println("\n>>> 사원명으로 검색하기 <<<");

		System.out.print("▷ 사원명 : ");
		String name = sc.nextLine();

		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("name", name);

		EmployeeDTO edto = tdao.emp_name(paraMap);

		if (edto != null) {

			System.out.println("[사원명] " + edto.getName());
			System.out.println("[직위] " + edto.getRank());
			System.out.println("[이메일] " + edto.getUq_email());
			System.out.println("[연락처] " + edto.getUq_mobile());
			System.out.println("[입사일자] " + edto.getHire_date().substring(0, 10));
			System.out.println("[부서명] " + edto.getDept_name());

		}

		else {
			// 존재하지 않는 사원명을 입력한 경우
			System.out.println(">> 사원명 " + name + " 은 사원목록에 존재하지 않습니다. << \n");
		}

	}// end of private void emp_name(Scanner sc)-------------

	
	
	
	// 일반사원로그인 - 사원번호 검색 //
	private void emp_id(Scanner sc) {
		System.out.println("\n>>> 사원번호로 검색하기 <<<");

		System.out.print("▷ 사원번호 : ");
		String emp_id = sc.nextLine();

		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("emp_id", emp_id);

		EmployeeDTO employee = tdao.emp_id(paraMap);

		if (employee != null) {

			System.out.println("[사원명] " + employee.getName());
			System.out.println("[직위] " + employee.getRank());
			System.out.println("[이메일] " + employee.getUq_email());
			System.out.println("[연락처] " + employee.getUq_mobile());
			System.out.println("[입사일자] " + employee.getHire_date().substring(0, 10));
			System.out.println("[부서명] " + employee.getDept_name());

		} else {
			// 존재하지 않는 사원번호를 입력한 경우
			System.out.println(">> 사원번호 " + emp_id + " 은 사원목록에 존재하지 않습니다. << \n");
		}

	}// end of private void menu_EmpLookUP(Scanner sc)-------------

	
	
	
	// 일반사원 로그인 - 2.개인 메뉴 //
	// 인사정보,출퇴근시간입력, 연차신청,뒤로가기 구현 필요
	private void menu_personal(EmployeeDTO employee, Scanner sc) {
		String menu_no = ""; // 입력받은 메뉴번호 저장
		do {
			System.out.println("\n===== 개인메뉴 ( " + employee.getName() + " ) 로그인중.. =====");
			System.out.println("1.인사정보             2.출퇴근시간 입력   \n3.연차신청             "
					+ "4.문서기안\n5.문서처리확인	     6.결재할문서확인(new)" + "\n7.결재할문서확인(우선결재)    8.문서수정\n9.처리문서확인 0.뒤로가기");
			System.out.println("==================================");

			System.out.print("▷ 메뉴번호 선택 : ");
			menu_no = sc.nextLine();

			switch (menu_no) {
			case "1": // 1.인사정보
				emp_info(employee, sc);
				break;

			case "2": // 2.출퇴근시간 입력
				commnute_insert(employee, sc);
				break;

			case "3": // 3.연차신청
				YsinCheong(employee, sc);
				break;

			case "4": // 4.문서기안 (관리자로 옮겨야함)
				Docinsert(employee, sc);
				break;
			case "5": // 5.기안한 문서 처리상태 확인
				DocView(employee, sc);
				break;
			case "6": // 6.결재할 문서 확인
				DocAproval_2(employee, sc);
				break;
			case "7": // 7.우선결재할 문서 확인
				DocAproval(employee, sc);
				break;
			case "8": // .반려 문서 or 아직 승인전 문서 수정
				DocChange(employee, sc);
				break;
			case "9": // 9.내가 처리한 문서 목록 조회
				DocView2(employee, sc);
				break;
			case "0": // 9.내가 처리한 문서 목록 조회
				break;

			default:
				System.out.println(">> 메뉴에 없는 번호입니다. <<\n");
				break;
			}// end of switch (menu_no)
		} while (!"0".equals(menu_no)); // end of do~ while()---------------------------------------
	}// end of private void menu_personal(EmployeeDTO employee, Scanner  sc)---------------

	
	
	
	// 처리한 문서 목록 조회 //
	private void DocView2(EmployeeDTO employee, Scanner sc) {

		StringBuilder sb = new StringBuilder();
		sb = tdao.DocView2(employee, sc);

		if (sb.length() != 0) {

			System.out.println(
					"\n---------------------------------------------- [문서 목록] ----------------------------------------------");
			System.out.println("문서번호\t기안작성자사원번호\t문서내용\t\t작성일자\t\t승인단계\t\t승인사원번호\t승인사원명\t승인여부\t코멘트\t\t\t승인날짜\t승인일");
			System.out.println(
					"---------------------------------------------------------------------------------------------------------");
			System.out.println(sb.toString());

		} else {
			System.out.println("기안한 문서내역이 존재하지 않습니다");
		}
	}// end of private void DocView2(EmployeeDTO employee, Scanner sc)----------------------

	
	
	
	// 출퇴근 시간 입력
	private void commnute_insert(EmployeeDTO employee, Scanner sc) {
		String menu_no = ""; // 입력받은 메뉴번호 저장
		do {
			System.out
					.println("\n====================  개인메뉴" + employee.getName() + "(사원)로그인중..  ====================");
			System.out.println("1.출근시간 입력 2. 퇴근시간 입력   3.뒤로가기");
			System.out.println("=============================================================");

			System.out.print("▷ 메뉴번호 선택 : ");
			menu_no = sc.nextLine();

			switch (menu_no) {
			case "1": // 1.출근시간 입력
				commute_start(employee, sc);

				break;

			case "2": // 2.퇴근시간 입력
				commute_end(employee, sc);
				break;

			case "3": // 3. 뒤로가기
				break;

			default:
				System.out.println(">> 메뉴에 없는 번호입니다. <<\n");
				break;
			}// end of switch (menu_no)
		} while (!"3".equals(menu_no));
	}// private void commnute_insert(EmployeeDTO employee, Scanner sc)-----------------------------
	
	

	// 퇴근시간 입력 메소드 //
	private int commute_end(EmployeeDTO employee, Scanner sc) {
		int result = 0;

		int flag = 0;
		LocalTime now = LocalTime.now();

		do {
			System.out
					.println("\n 지금 시간은 " + now.getHour() + "시 " + now.getMinute() + "분 " + now.getSecond() + "초 입니다.");
			System.out.print(">> 퇴근시간을 입력하시겠습니까??[Y/N] : ");
			String yn = sc.nextLine();
			flag = 0;
			if ("y".equalsIgnoreCase(yn)) {
				System.out.println("퇴근시간이 입력되었습니다.");
				flag = 1;
				break;
			} else if ("n".equalsIgnoreCase(yn)) {
				break;
			} else {
				System.out.println(">> Y 또는 N 만 입력하세요!! << \n");
			}
		} while (true);

		if (flag == 1) {

			int h = 0;
			String m = String.valueOf(now.getMinute());
			String s = String.valueOf(now.getSecond());

			if (now.getMinute() < 10)
				m = "0" + String.valueOf(now.getMinute());

			if (now.getSecond() < 10)
				s = "0" + String.valueOf(now.getSecond());

			List<CommuteDTO> cmtdto = new ArrayList<>();
			CommuteDTO cmmdto = new CommuteDTO();
			cmmdto.setFk_writer_emp_id(employee.getEmp_id());
			cmmdto.setWorking_time(String.valueOf(now.getHour()) + m + s);

			cmtdto = tdao.commute_end(cmmdto);

			if (cmtdto != null) {
				int wtime = Integer.parseInt(cmtdto.get(0).getWorking_time());
				System.out.println(cmmdto.getWorking_time());

				System.out.println();
			}

		} else {
			result = 0;
		}
		return result;

	}// private void commute_end(EmployeeDTO employee2, Scanner sc)---------------------

	
	
	// 출근시간 입력 메소드 //
	private int commute_start(EmployeeDTO employee, Scanner sc) {
		int result = 0;

		int flag = 0;
		LocalTime now = LocalTime.now();

		do {
			System.out
					.println("\n 지금 시간은 " + now.getHour() + "시 " + now.getMinute() + "분 " + now.getSecond() + "초 입니다.");
			System.out.print(">> 출근시간을 입력하시겠습니까??[Y/N] : ");
			String yn = sc.nextLine();
			flag = 0;
			if ("y".equalsIgnoreCase(yn)) {
				flag = 1;
				break;
			} else if ("n".equalsIgnoreCase(yn)) {
				break;
			} else {
				System.out.println(">> Y 또는 N 만 입력하세요!! << \n");
			}
		} while (true);

		if (flag == 1) {

			int h = 0;
			String m = String.valueOf(now.getMinute());
			String s = String.valueOf(now.getSecond());

			if (now.getMinute() < 10)
				m = "0" + String.valueOf(now.getMinute());

			if (now.getSecond() < 10)
				s = "0" + String.valueOf(now.getSecond());

			List<CommuteDTO> cmtdto = new ArrayList<>();
			CommuteDTO cmmdto = new CommuteDTO();
			cmmdto.setFk_writer_emp_id(employee.getEmp_id());
			cmmdto.setWorking_time(String.valueOf(now.getHour()) + m + s);

			cmtdto = tdao.commute_start(cmmdto);

			if (cmtdto != null) {
				int wtime = Integer.parseInt(cmtdto.get(0).getWorking_time());
				System.out.println(cmmdto.getWorking_time());

				if (90000 < wtime) {
					cmtdto = tdao.c_status(cmtdto.get(0), cmmdto.getFk_writer_emp_id());
					System.out.println("지각하셨습니다. ");
				}

				System.out.println();
			}

		} else {
			result = 0;
		}
		return result;
	} // 출근시간 입력 메소드 //

	
	
	
	// 일반사원 로그인 - 3.증명서 게시판 //
	private void certificate(EmployeeDTO employee, Scanner sc) {
		String menu_no = ""; // 입력받은 메뉴번호 저장
		do {
			System.out
					.println("\n====================  개인메뉴" + employee.getName() + "(사원)로그인중..  ====================");
			System.out.println("1.재직증명서  2.경력증명서   3.뒤로가기");
			System.out.println("=============================================================");

			System.out.print("▷ 메뉴번호 선택 : ");
			menu_no = sc.nextLine();

			switch (menu_no) {
			case "1": // 1.재직증명서
				certificate_employment(employee.getEmp_id(), sc);
				break;

			case "2": // 2.경력증명서
				certificate_career(employee.getEmp_id(), sc);
				break;

			case "3": // 4.뒤로가기
				break;
			default:
				System.out.println(">> 메뉴에 없는 번호입니다. <<\n");
				break;
			}// end of switch (menu_no)
		} while (!"3".equals(menu_no)); // end of do~ while()---------------------------------------
	}

	
	
	// 일반사원 로그인 - 4.사내게시판 메뉴 //
	private void board_total(EmployeeDTO employee, Scanner sc) {
		String menu_no = ""; // 입력받은 메뉴번호 저장
		do {
			System.out.println("\n=== 사내게시판( " + employee.getName() + " )로그인중..===");
			System.out.println("1.공지사항  2.일반게시판   \n3.뒤로가기");
			System.out.println("==============================");

			System.out.print("▷ 메뉴번호 선택 : ");
			menu_no = sc.nextLine();

			switch (menu_no) {
			case "1": // 1.공지사항
				board_notice(employee, sc); // 공지사항에 댓글 작성할 경우
				break;

			case "2": // 2.일반게시판
				board_general(employee, sc);
				break;

			case "3": // 뒤로가기
				break;

			default:
				System.out.println(">> 메뉴에 없는 번호입니다. <<\n");
				break;
			}// end of switch (menu_no)
		} while (!"3".equals(menu_no)); // end of do~ while()---------------------------------------
	}// end of private void board_total(EmployeeDTO employee, Scanner sc)---------------------

	// 일반사원,관리자 로그인 - 일반게시판 메뉴 //
	private void board_general(EmployeeDTO employee, Scanner sc) {
		String menu_no = ""; // 입력받은 메뉴번호 저장
		do {
			System.out.println("\n============== 일반게시판 " + employee.getName() + " 로그인중.. ===============");
			System.out.println(
					"1.글 목록 보기   2.글 내용 보기   3.글 작성하기  4.글 수정하기    5.글 삭제하기    	\n6.댓글작성 7.일주일간 일자별 게시글 작성건수   8.이번달 일자별 게시글 작성건수   9.뒤로가기");
			System.out.println("==========================================================");

			System.out.print("▷ 메뉴번호 선택 : ");
			menu_no = sc.nextLine();

			switch (menu_no) {
			case "1": // 1.글목록보기
				boardlist();
				break;

			case "2": // 2.글내용보기
				viewContent(employee.getEmp_id(), sc);
				break;

			case "3": // 3.글작성하기
				int n = boardwrite(employee, sc);
				break;

			case "4": // 4.글수정하기
				n = boardupdate(employee, sc);
				break;

			case "5": // 5.글삭제하기
				n = boardDelete(employee, sc);
				break;

			case "6": // 6.댓글작성
				n = writeComment(employee, sc);
				break;

			case "7": // 7.일주일간 일자별 게시글 작성건수
				statisticsByWeek();
				break;

			case "8": // 8.이번달 일자별 게시글 작성건수
				statisticsByCurrnetMonth();
				break;

			case "9": // 9. 뒤로가기
				break;

			default:
				System.out.println(">> 메뉴에 없는 번호입니다. <<\n");
				break;
			}// end of switch (menu_no)
		} while (!"9".equals(menu_no)); // end of do~ while()---------------------------------------

	}// end of private void board_general(EmployeeDTO employee, Scanner sc)----------------------------

	
	
	// 한달 일자별 게시글 작성 건수
	private void statisticsByCurrnetMonth() {

		Calendar currentDate = Calendar.getInstance();
		// 현재 날짜와 시간을 얻어온다.
		SimpleDateFormat dateft = new SimpleDateFormat("yyyy년 MM월");
		String currentMonth = dateft.format(currentDate.getTime());

		System.out.println("\n>>> [ " + currentMonth + " 일자별 게시글 작성건수 ] <<<");
		System.out.println("==================================");
		System.out.println("작성일자\t작성건수");
		System.out.println("==================================");

		// 이번달 일자별 게시글 작성건수를 DB에서 가져온 결과물
		List<Map<String, String>> mapList = tdao.statisticsByCurrentMonth();

		if (mapList.size() > 0) {
			StringBuilder sb = new StringBuilder();

			for (Map<String, String> map : mapList) {
				sb.append(map.get("REGIST_DATE") + "\t" + map.get("CNT") + "\n");
			}
			System.out.println(sb.toString());

		} else {
			System.out.println("[경고]작성된 게시글이 없습니다.");
		}

	}// end of private void statisticsByCurrnetMonth()------------------------
	
	

	// 일주일간 일자별 게시글 작성건수 //
	private void statisticsByWeek() {

		System.out.println("\n=======================[최근 1주일간 일자별 게시글 작성 건수]==============================");
		String title = "전체\t";
		for (int i = 0; i < 7; i++) {
			title += myutil.addDay(i - 6) + "   "; // -6 -5 -4 -3 -2 -1 0
		} // end of for ----------------------------------
		System.out.println(title);
		System.out.println("\n===================================================================================");
		Map<String, Integer> resultMap = tdao.statisticsByWeek();
		String result = resultMap.get("TOTAL") + "\t" + resultMap.get("PREVIOUS6") + "\t" + resultMap.get("PREVIOUS5")
				+ "\t" + resultMap.get("PREVIOUS4") + "\t" + resultMap.get("PREVIOUS3") + "\t"
				+ resultMap.get("PREVIOUS2") + "\t" + resultMap.get("PREVIOUS1") + "\t" + resultMap.get("TODAY");
		System.out.println(result);
	}// end of private void statisticsByWeek() --------------------------------
	
	

	// 댓글 작성 메소드 //
	private int writeComment(EmployeeDTO employee, Scanner sc) {
		int result = 0;
		boardlist();

		System.out.println("\n>>> 댓글쓰기 <<<");
		System.out.println("1. 작성자명 : " + employee.getName());

		int n_post_id = 0;
		do {
			System.out.print("2. 원글의 글번호 : ");
			String post_id = sc.nextLine(); // 234342 또는 똘똘이 와 같이 존재하지 않는 원글의 글번호를 입력할 수 있다.
			try {
				n_post_id = Integer.parseInt(post_id);

				List<BoardDTO> bdto = tdao.find_board(n_post_id);

				if (bdto == null) // 안담겨있으면
					System.out.println(">> [경고] 입력하신 게시글 번호가 존재하지 않습니다. 다시 입력하세요.<< ");
				else // 담겨있으면
					break;
			} catch (NumberFormatException e) {
				System.out.println(">> [경고] 원글의 글 번호는 정수로만 입력하셔야 합니다. <<\n");
			}

		} while (true);

		String content = "";
		do {
			System.out.print("3. 댓글내용 : ");
			content = sc.nextLine();
			if (content.trim().isEmpty()) { // 그냥 엔터 또는 공백만 입력한 경우
				System.out.print(">> [경고] 댓글내용은 필수로 입력하셔야 합니다. <<\n");
			} else if (content.length() > 50) {
				System.out.print(">> [경고] 댓글내용은 최대 50글자 이내 이어야 합니다. <<\n");
			} else {
				break;
			}
		} while (true);

		String yn = "";
		do {
			System.out.print("=== 정말로 댓글쓰기를 하시겠습니까..? [Y/N] : ");
			yn = sc.nextLine();
			if (("y".equalsIgnoreCase(yn) || "n".equalsIgnoreCase(yn))) {
				break;
			} else {
				System.out.println(" >>[경고] Y 또는 N 만 입력이 가능합니다..! <<\n");
			}
		} while (true);

		if ("y".equalsIgnoreCase(yn)) {
			CommentDTO cmdto = new CommentDTO();
			cmdto.setFk_post_id(n_post_id); // 원글의 글번호
			cmdto.setFk_writer_emp_id(employee.getEmp_id()); // 댓글을 작성하는 사용자 id
			cmdto.setContent(content); // 댓글 내용
			result = tdao.writeComment(cmdto);
			if (result > 0) {
				System.out.println(">> 댓글 입력 성공!! <<\n");
			}

		} else {
			System.out.println(">> 댓글 입력을 취소하였습니다. <<\n");
		}
		return result;
	}// 댓글 작성 메소드 

	
	
	
	// 글 삭제하기 메소드 //
	private int boardDelete(EmployeeDTO employee, Scanner sc) {

		int result = 0;

		System.out.println("\n >>> 글 삭제하기 <<<");
		String post_id = "";
		do {
			System.out.print("▷ 삭제할 글번호 : ");
			post_id = sc.nextLine(); // "똘똘이" , "1238763294"

			try {
				Integer.parseInt(post_id);
				break;
			} catch (NumberFormatException e) {
				System.out.println("[경고]삭제할 글번호는 숫자로만 입력하세요!! \n");
			}
		} while (true);

		BoardDTO bdto = tdao.viewContent2(post_id);

		if (bdto != null) {
			// 삭제할 글 번호가 글 목록에 존재하는 경우
			if (employee.getCk_manager_rank() != 1) {
				if (!(bdto.getFk_writer_emp_id() == employee.getEmp_id())) {
					// 삭제할 글 번호가 다른사람이 쓴 글인경우
					System.out.println("[경고] 다른 사용자의 글은 삭제 불가합니다.!! \n");
				} else {
					// 삭제할 글 번호가 자신이 쓴 글인경우
					System.out.print("▷ 글암호 : ");
					String passwd = sc.nextLine();

					if (bdto.getPasswd() == Integer.parseInt(passwd)) {
						// 글 암호가 일치하는 경우
						System.out.println("=========================================");
						System.out.println("[글제목] " + bdto.getTitle());
						System.out.println("[글내용] " + bdto.getContent());
						System.out.println("=========================================");
						do {
							System.out.print("▷ 정말로 글을 삭제하시겠습니까?[Y/N] : ");
							String yn = sc.nextLine();

							if ("y".equalsIgnoreCase(yn)) {
								result = tdao.deleteBoard2(post_id);
								// result 1(정상적으로 글이 삭제된 경우) 또는 -1 (SQLException 발생으로 글삭제가 실패한 경우)
								break;
							} else if ("n".equalsIgnoreCase(yn)) {
								System.out.println(">> 글 삭제를 취소하였습니다. <<");
								break;
							} else {
								System.out.println("[경고] Y 또는 N 만 입력하세요!!\n");
							}
						} while (true);
					} else {
						// 글 암호가 일치하지 않는 경우
						System.out.println("[경고] 입력하신 글 암호가 작성시 입력한 글 암호와 일치하지 않으므로 삭제 불가합니다!! \n");
					}
				} // end of if -------------------
			} else if (employee.getCk_manager_rank() == 1) {
				System.out.println("=========================================");
				System.out.println("[글제목] " + bdto.getTitle());
				System.out.println("[글내용] " + bdto.getContent());
				System.out.println("=========================================");
				do {
					System.out.print("▷ 정말로 글을 삭제하시겠습니까?[Y/N] : ");
					String yn = sc.nextLine();

					if ("y".equalsIgnoreCase(yn)) {
						result = tdao.deleteBoard2(post_id);
						// result 1(정상적으로 글이 삭제된 경우) 또는 -1 (SQLException 발생으로 글삭제가 실패한 경우)
						break;
					} else if ("n".equalsIgnoreCase(yn)) {
						System.out.println(">> 글 삭제를 취소하였습니다. <<");
						break;
					} else {
						System.out.println("[경고] Y 또는 N 만 입력하세요!!\n");
					}
				} while (true);
			}

		} else {
			// 삭제할 글 번호가 글 목록에 존재하지 않는경우
			System.out.println("수정할 글 번호 " + post_id + " 는 글 목록에 존재하지 않습니다.");
		}
		return result;
		// 0 또는 1 또는 -1

	}// end of private int boardDelete(EmployeeDTO employee, Scanner sc) -------------------
	
	

	// 게시글 수정 메소드 //
	private int boardupdate(EmployeeDTO employee, Scanner sc) {
		int result = 0;

		System.out.println("\n >>> 글 수정하기 <<<");
		String post_id = "";
		do {
			System.out.print("▷ 수정할 글번호 : ");
			post_id = sc.nextLine(); // "똘똘이" , "1238763294"

			try {
				Integer.parseInt(post_id);
				break;
			} catch (NumberFormatException e) {
				System.out.println("[경고]수정할 글번호는 숫자로만 입력하세요!! \n");
			}
		} while (true);

		BoardDTO bdto = tdao.viewContent2(post_id);

		if (bdto != null) {
			// 수정할 글 번호가 글 목록에 존재하는 경우

			if (!(bdto.getFk_writer_emp_id() == (employee.getEmp_id()))) {
				// 수정할 글 번호가 다른사람이 쓴 글인경우
				System.out.println("[경고] 다른 사용자의 글은 수정 불가합니다.!! \n");
			} else {
				// 수정할 글 번호가 자신이 쓴 글인경우
				System.out.print("▷ 글암호 : ");
				String passwd = sc.nextLine();

				if (bdto.getPasswd() == Integer.parseInt(passwd)) {
					// 글 암호가 일치하는 경우
					System.out.println("=========================================");
					System.out.println("[수정전 글제목] " + bdto.getTitle());
					System.out.println("[수정전 글내용] " + bdto.getContent());
					System.out.println("=========================================");

					System.out.print("▷ 글 제목[변경하지 않으려면 엔터] : ");
					String title = sc.nextLine();

					if (title != null && title.trim().isEmpty()) {
						title = bdto.getTitle(); // 변경하지 않으려면 기존 subject 내용을 입력
					}

					System.out.print("▷ 글 내용[변경하지 않으려면 엔터] : ");
					String content = sc.nextLine();

					if (content != null && content.trim().isEmpty()) {
						content = bdto.getContent(); // 변경하지 않으려면 기존 contents 내용을 입력
					}

					if (title.length() > 20 || content.length() > 100) {
						System.out.println("[경고] 글제목은 최대 20글자이며, 글내용은 최대 100글자 이내로 작성해야 합니다. ");
					}

					Map<String, String> paraMap = new HashMap<>();
					paraMap.put("title", title);
					paraMap.put("content", content);
					paraMap.put("post_id", post_id);

					result = tdao.updateBoard(paraMap); // 글 수정하기
					// 1(글 수정이 성공된 경우 ) or -1(글수정을 하려고 하나 SQLException이 발생한 경우)
				} else {
					// 글 암호가 일치하지 않는 경우
					System.out.println("[경고] 입력하신 글 암호가 작성시 입력한 글 암호와 일치하지 않으므로 수정 불가합니다!! \n");
				}
			}
		} else {
			// 수정할 글 번호가 글 목록에 존재하지 않는경우
			System.out.println("수정할 글 번호 " + post_id + " 는 글 목록에 존재하지 않습니다.");
		}
		return result;

	}// end of private int boardupdate(EmployeeDTO employee, Scanner sc)--------------------------

	
	
	
	// 게시글 작성 메소드//
	private int boardwrite(EmployeeDTO employee, Scanner sc) {
		int result = 0;
		String title;
		String content;

		System.out.println("\n>> 글쓰기 <<");

		System.out.println("1. 작성자명 : " + employee.getName());
		do {
			System.out.print("2. 글 제목 : ");
			title = sc.nextLine();
			if (!title.trim().isEmpty()) {
				break;
			} else {
				System.out.println("글 제목을 다시 입력하세요");
			}
		} while (true);

		do {
			System.out.print("3. 글 내용 : ");
			content = sc.nextLine();
			if (!content.trim().isEmpty()) {
				break;
			} else {
				System.out.println("글 내용을 다시 입력하세요");
			}
		} while (true);
		String passwd = "";
		do {
			System.out.print("4. 글 암호(숫자 4자리) : ");
			passwd = sc.nextLine();
			if (!passwd.trim().isEmpty()) {
				break;
			} else {
				System.out.println("글 내용을 다시 입력하세요");
			}
		} while (true);

		int flag = 0;
		do {

			System.out.print(">> 정말로 글쓰기를 하시겠습니까?[Y/N] => ");
			String yn = sc.nextLine();
			flag = 0;
			if ("y".equalsIgnoreCase(yn)) {
				flag = 1;
				break;
			} else if ("n".equalsIgnoreCase(yn)) {
				break;
			} else {
				System.out.println(">> Y 또는 N 만 입력하세요!! << \n");
			}
		} while (true);

		int flag2 = 1;
		if (flag == 1) { // 글쓰기를 하는 하겠다 라는 경우
			BoardDTO bdto = new BoardDTO();
			bdto.setFk_writer_emp_id(employee.getEmp_id());
			bdto.setTitle(title);
			bdto.setContent(content);
			try {
				bdto.setPasswd(Integer.parseInt(passwd));
			} catch (NumberFormatException e) {
				System.out.println("[경고] 게시글 작성 실패!");
				flag2 = -1;
			}
			if (flag2 == 1) {
				result = tdao.write(bdto); // 정상이면 1(commit) , 실패하면 -1(rollback)
			}
		} else { // 글쓰기를 취소한 경우
			result = 0;
		}
		return result;
	}// end of private int boardwrite(EmployeeDTO employee, Scanner sc)--------------------------

	
	
	
	// 일반게시판 글 내용 조회 메소드
	private void viewContent(int emp_id, Scanner sc) {

		System.out.println("\n>>> 글 내용보기 <<<");

		System.out.print("▷ 글번호 : ");
		String post_id = sc.nextLine();

		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("post_id", post_id);
//		paraMap.put("login_userid", login_userid);

		BoardDTO bdto = tdao.viewContent(paraMap);

		if (bdto != null) {
			System.out.println("[글내용] :" + bdto.getContent());

			if (!(bdto.getFk_writer_emp_id() == emp_id)) {// 다른사람이 쓴 글이라면 update
				tdao.updateViewCount(post_id); // 리턴값이 필요 없음(조회수가 오르면 끝)
			}
			//////////////////////////////////////////////////////////////////////
			System.out.println("[댓글]\n============================================");

			List<CommentDTO> commentList = tdao.commentList(post_id);
			// 원글에 대한 댓글을 가져오는 것.(특정 게시글 글 번호에 대한 tbl_comment 테이블 과 tbl_member 테이블을 JOIN 해서
			// 보여준다.)
			// where절에 boardno 로 검색하므로 파라미터로 boardno를 넘겨주고 복수개의 결과값을 저장하기 위해 List 타입으로 저장한다.

			if (commentList != null) {
				// 댓글이 존재하는 원글인 경우
				System.out.println("댓글내용\t\t작성자\t작성일자");
				System.out.println("============================================");

				StringBuilder sb = new StringBuilder();

				for (CommentDTO cmtdto : commentList) {
					sb.append(cmtdto.viewCommentInfo() + "\n");
				} // end of for(CommentDTO cmtdto : commentList)---------------------------------

				System.out.println(sb.toString());

			} else {
				// 댓글이 존재하지 않는 원글인 경우
				System.out.println(">> 댓글내용 없음. <<\n");
			}
		} else {
			// 존재하지 않는 글번호를 입력한 경우 "똘똘이" , "1231927361289"
			System.out.println(">>> 글번호 " + post_id + " 은 글 목록에 존재하지 않습니다. <<<");
		}

	}// end of private void viewContent(int emp_id, Scanner sc)--------------

	
	
	
	// 일반 게시글 목록 출력 메소드 //
	private void boardlist() {

		List<BoardDTO> boardlist = tdao.boardlist();

		if (boardlist.size() > 0) {
			// 작성된 게시글이 있는 경우

			System.out.println("\n======================[게시글 목록]======================");
			System.out.println("글번호\t글제목\t\t작성자명\t작성일자\t\t조회수");
			System.out.println("========================================================");

			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < boardlist.size(); i++) {
				sb.append(boardlist.get(i).showBoardTitle() + "\n");
				// boardList.get(i) 는 BoardDTO 이다.
			} // end of for()--------------------------------------

			System.out.println(sb.toString());

		} else {
			// 작성된 게시글이 하나도 없는 경우
			System.out.println(">> 글목록이 없습니다. <<\n");
		}
	}// end of private void boardlist()--------------------------
	
	

	// 일반사원 로그인 - 공지사항게시판 메뉴
	private void board_notice(EmployeeDTO employee, Scanner sc) {
		String menu_no = ""; // 입력받은 메뉴번호 저장
		do {
			System.out.println("\n============== 공지사항( " + employee.getName() + " )로그인중.. ===============");
			System.out.println("1.공지사항  2.뒤로가기");
			System.out.println("================================================");

			System.out.print("▷ 메뉴번호 선택 : ");
			menu_no = sc.nextLine();

			switch (menu_no) {
			case "1": // 1.일반공지사항
				notice_employee(employee, sc);
				break;

			case "2": // 2.뒤로가기
				break;

			default:
				System.out.println(">> 메뉴에 없는 번호입니다. <<\n");
				break;
			}// end of switch (menu_no)
		} while (!"2".equals(menu_no)); // end of do~ while()---------------------------------------

	}// end of private void board_notice(EmployeeDTO employee, Scanner sc)--------------------------

	
	
	
	// 공지사항 - 일반 사용자용 //
	private void notice_employee(EmployeeDTO employee, Scanner sc) {
		String menu_no = ""; // 입력받은 메뉴번호 저장
		do {
			System.out.println("\n============== 공지사항 " + employee.getName() + "(사원)로그인중.. ===============");
			System.out.println("1.공지사항 목록 보기  2.공지사항 내용 조회   3.뒤로가기");
			System.out.println("================================================");

			System.out.print("▷ 메뉴번호 선택 : ");
			menu_no = sc.nextLine();

			switch (menu_no) {
			case "1": // 1.공지사항 목록 보기
				notice_list();
				break;

			case "2": // 2. 공지사항 내용 조회
				notice_content(employee.getEmp_id(), sc);
				break;

			case "3": // 3.뒤로가기
				break;

			default:
				System.out.println(">> 메뉴에 없는 번호입니다. <<\n");
				break;
			}// end of switch (menu_no)
		} while (!"3".equals(menu_no)); // end of do~ while()---------------------------------------

	}//end of private void notice_employee(EmployeeDTO employee, Scanner sc) ----------------------------------

	
	
	
	// 공지사항 목록보기 메소드 //
	private void notice_list() {

		List<NoticeDTO> noticelist = tdao.noticelist();

		if (noticelist.size() > 0) {
			// 작성된 게시글이 있는 경우

			System.out.println("\n===================== [공지사항 목록] =====================");
			System.out.println("글번호\t글제목\t\t작성자명\t작성일자\t\t조회수");
			System.out.println("=========================================================");

			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < noticelist.size(); i++) {
				sb.append(noticelist.get(i).showNoticeTitle() + "\n");
				// boardList.get(i) 는 BoardDTO 이다.
			} // end of for()--------------------------------------

			System.out.println(sb.toString());

		} else {
			// 작성된 게시글이 하나도 없는 경우
			System.out.println(">> [경고] 공지사항이 없습니다. <<\n");
		}

	}// end of private void notice_list()-----------------------------

	
	
	
	// 재직증명서 출력하는 메소드
	private void certificate_employment(int emp_id, Scanner sc) {

		System.out.println("\n>>>재직증명서 발급<<<");

		while (true) {
			System.out.print("▷ 용도 : ");
			String use = sc.nextLine();

			if (use.length() <= 20) { // end of ouuter if
				CertDTO cdto = new CertDTO();
				cdto.setUse(use);
				cdto.setFk_writer_emp_id(emp_id);

				List<CertDTO> insert_cert1 = tdao.insert_cert1(cdto);

				if (insert_cert1 != null) {
					List<CertDTO> certList = tdao.certList(emp_id, use);

					if (certList != null) {
						StringBuilder sb = new StringBuilder();

						for (CertDTO certdto : certList) {
							sb.append(certdto.cert_1());
						} // end of for

						System.out.println(sb.toString());

						while (true) {
							System.out.print(">> 정말로 출력 하시겠습니까?[Y/N] => ");
							String yn = sc.nextLine();

							if ("y".equalsIgnoreCase(yn)) {
								System.out.println(sb.toString());
								tdao.yn_y(yn);
								break;
							} else if ("n".equalsIgnoreCase(yn)) {
								tdao.yn_y(yn);
								break;
							}
							else
								System.out.print(">> [경고] Y 또는 N 만 입력하세요!! << \n");
						} // end of while
					} // end of inner if
					break;
				} // end of outer if
				else
					System.out.println(">> [경고] 입력 실패 <<");
			} // end of ouuter if
			else
				System.out.println(">> [경고] 용도는 20글자 이하로 입력하세요. <<");
		}
	} // end of 재직증명서 메소드

	
	
	
	// 경력증명서 출력하는 메소드 //
	private void certificate_career(int emp_id, Scanner sc) {
		System.out.println("\n>>>경력증명서 발급<<<");

		while (true) {
			System.out.print("▷ 용도 : ");
			String use = sc.nextLine();

			if (use.length() <= 20) { // end of ouuter if
				CertDTO cdto = new CertDTO();
				cdto.setUse(use);
				cdto.setFk_writer_emp_id(emp_id);

				List<CertDTO> insert_cert1 = tdao.insert_cert2(cdto);

				if (insert_cert1 != null) {
					List<CertDTO> certList = tdao.certList2(emp_id, use);

					if (certList != null) {
						StringBuilder sb = new StringBuilder();

						for (CertDTO certdto : certList) {
							sb.append(certdto.cert_2());
						} // end of for

						System.out.println(sb.toString());

						while (true) {
							System.out.print(">> 정말로 출력 하시겠습니까?[Y/N] => ");
							String yn = sc.nextLine();

							if ("y".equalsIgnoreCase(yn)) {
								System.out.println(sb.toString());
								tdao.yn_y(yn);
								break;
							} else if ("n".equalsIgnoreCase(yn)) {
								tdao.yn_y(yn);
								break;
							} else
								System.out.print(">>[경고] Y 또는 N 만 입력하세요!! << \n");

						} // end of while
					} // end of if(certList != null)----------
					break;
				} // end of outer if
				else
					System.out.println(">> [경고]입력 실패 <<");
			} // end of ouuter if

			else
				System.out.println(">>[경고] 용도는 20글자 이하로 입력하세요. <<");
		} // end of while(true)------------
	}// end of 경력증명서 출력하는 메소드

	
	
	
	// 부서 관리 시작 메뉴
	private void dept_Start(Scanner sc) {

		String dept_manage_no = ""; // 스캐너로 입력받아올 부서관리 번호 선택
		int n = 0;

		do {
			System.out.println("\n======= 부서관리 =========\n" + "1.부서 조회      2.부서 신설   \n3.부서 정보 수정  4.부서 폐쇄   \n5.뒤로가기"
					+ "\n========================");
			System.out.print("▷ 번호선택 : ");
			dept_manage_no = sc.nextLine();

			switch (dept_manage_no) {
			case "1": // 부서 조회
				dept_list();
				break;

			case "2": // 부서 신설
				dept_add(sc);
				break;

			case "3": // 부서 정보 수정
				n = dept_edit(sc);
				if (n == 1) {
					System.out.println(">> 부서 수정 성공 << \n");
				} else if (n == -1) {
					System.out.println(">>[경고] SQL 구문오류 발생으로 부서 수정에 실패하였습니다. << \n");
				}
				break;

			case "4": // 폐쇄
				n = dept_delete(sc);
				if (n == 1) {
					System.out.println(">> 부서 폐쇄 성공 << \n");
				} else if (n == -1) {
					System.out.println(">>[경고] SQL 구문오류 발생으로 부서 폐쇄에 실패하였습니다. << \n");
				}
				break;

			case "5": // 뒤로가기
				break;

			default:
				System.out.println("[경고] 메뉴에 있는 번호를 선택해주세요. \n");
				break;
			} // end of switch

		} while (!"5".equals(dept_manage_no));

	} // end of public void dept_Start(Scanner sc)

	
	
	
	// 부서 조회 메소드
	private void dept_list() {

		List<DeptDTO> deptList = tdao.deptList();

		if (deptList.size() > 0) {
			// 게시글이 존재하는 경우

			System.out.println("\n============= [부서 목록] ===============");
			System.out.println("부서번호\t부서명\t부서장사원번호\t부서장명");
			System.out.println("=======================================");

			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < deptList.size(); i++) {
				sb.append(deptList.get(i).showDeptList() + "\n"); // boardList.get(i) 이게 BoardDTO이다. 왜? ♣♣♣♣♣
			} // end of for

			System.out.println(sb.toString());

		} else {
			// 부서가 한개도 존재하지 않는 경우
			System.out.println(">> 부서 목록이 존재하지 않습니다. << \n");
		}

	} // end of private void dept_list()

	
	
	
	// 부서 추가하는 메소드 //
	private int dept_add(Scanner sc) {

		DeptDTO ddto = new DeptDTO();

		String no_choice = ""; // 부서신설 번호 선택
		int result = 0;

		do {

			System.out.println("\n=== 부서 신설 ===\n" + "1.부서신설     2.취소   " + "\n===============");
			System.out.print("▷ 번호선택 : ");
			no_choice = sc.nextLine();

			switch (no_choice) {
			case "1":

				String s_fk_dept_manager_id = ""; // 스캐너로 입력받아올 매니저 번호
				int fk_dept_manager_id = 0; // dto에 담아줄 매니저 번호(int 타입이라 형변환 후 입력)
				String dept_name = "";
				boolean dept_name_value; // 부서명 빈칸 유무
				boolean manager_id_value; // 부서장 빈칸 유무

				System.out.println("\n>>> 부서 신설 <<<");

				System.out.println("▷ 신규 부서 번호는 자동부여 됩니다. ");

				do {
					dept_name_value = true;

					System.out.print("▷ 신규 부서명 : ");
					dept_name = sc.nextLine();

					if (dept_name.trim().isEmpty()) { // 암호로 쓸 수 없다면
						System.out.println("[경고] 신규 부서명은 공백이나 엔터가 올 수 없는 필수 입력 사항입니다. \n");
						dept_name_value = false;
					}
				} while (!dept_name_value);

				///////////////////////////////////////////////////
				do {
					manager_id_value = true;

					System.out.print("▷ 신규 부서 부서장 사원번호 : ");
					s_fk_dept_manager_id = sc.nextLine();

					if (s_fk_dept_manager_id.trim().isEmpty()) { // 암호로 쓸 수 없다면
						System.out.println("[경고] 신규 부서 부서장 사원번호는 공백이나 엔터가 올 수 없는 필수 입력 사항입니다. \n");
					}

					// 부서장번호는 int 타입이라 형변환
					try {
						fk_dept_manager_id = Integer.parseInt(s_fk_dept_manager_id);
					} catch (NumberFormatException e) {
						System.out.println("[경고] 신규 부서 부서장 사원번호는 숫자로만 입력하세요.\n");
					}

					List<DeptDTO> dept_manager_id_list = tdao.dept_manager_id_list();

					int flag = 0;

					for (int i = 0; i < dept_manager_id_list.size(); i++) {
						// 부서장아이디가 기존의 존재하는 부서장아이디와 같을 경우
						if (fk_dept_manager_id == dept_manager_id_list.get(i).getFk_dept_manager_id()) {
							flag = 1;
							break;
							// 부서장아이디가 기존의 존재하는 부서장아이디와 같지 않을 경우
						} else if (fk_dept_manager_id != dept_manager_id_list.get(i).getFk_dept_manager_id()) {
							flag = 2;
						}
					}

					if (flag == 1) {
						manager_id_value = true;
					} else {
						System.out.println("[경고] 존재하는 부서장 번호를 입력하세요.\n");
						manager_id_value = false;
					}

				} while (!manager_id_value);
				////////////////////////////////////////////////////////////////////////

				int flag = 0;

				do {
					System.out.print(">> 정말로 부서를 신설하시겠습니까? [Y/N] => ");
					String yn = sc.nextLine();

					if ("y".equalsIgnoreCase(yn)) {
						flag = 1;
						break;
					} else if ("n".equalsIgnoreCase(yn)) { // 이렇게 되면 flag가 그대로 0으로 떨어지게 된다.
						break;
					} else {
						System.out.println(">> [Y/N]만 입력하세요. <<");
					}
				} while (true);

				if (flag == 1) { // 부서를 신설하는 경우
					ddto.setDept_id(ddto.getDept_id());
					ddto.setDept_name(dept_name);
					ddto.setFk_dept_manager_id(fk_dept_manager_id);

					result = tdao.dept_insert(ddto); // 정상이면 1, rollback 이면 -1이 나온다.
					// 1 또는 -1

					System.out.println(">> 부서 신설이 정상적으로 완료되었습니다. <<");
					// 부여된 부서번호는 "+ddto.Department_Id+"번 입니다.

				} else { // flag != 1로 글쓰기를 취소한 경우 // else면 result가 0이다.
					result = 0; // 초기값이 0이기 때문에 else는 삭제해도 상관 없다.
					System.out.println(">> 부서 신설이 취소되었습니다. <<");
				}

				break;

			case "2":
				System.out.println(">>> 부서 신설을 취소하셨습니다. <<< \n");
				break;

			default:
				System.out.println("[경고] 메뉴에 있는 번호만 선택하세요.\n");
				break;
			}

		} while (!"2".equals(no_choice));

		return result;

	} // end of private void dept_add(Scanner sc)

	
	
	
	// 부서정보 수정하는 메소드 //
	private int dept_edit(Scanner sc) {

		int result = 0;

		System.out.println("\n=== 부서 정보 수정하기 ===");

		int dept_id = 0; // 입력받은 부서번호
		String s_dept_id = "";
		boolean dept_no_value; // 부서 번호 공백으로 못 넣게 만드는 용
		int fk_dept_manager_id = 0;

		dept_list();

		do {
			do { // 부서 정보 공백이 오지 못하게 유효성 검사
				dept_no_value = true;

				System.out.print("▷ 수정할 부서 번호 : ");
				s_dept_id = sc.nextLine();

				if (s_dept_id.trim().isEmpty()) { // 암호로 쓸 수 없다면
					System.out.println("[경고] 부서 번호는 공백이나 엔터가 올 수 없는 필수 입력 사항입니다. \n");
					dept_no_value = false;
				}
			} while (!dept_no_value);

			try { // 부서 정보 번호만 올 수 있도록 유효성 검사
				dept_id = Integer.parseInt(s_dept_id);
				break; // 숫자가 되어지면 break;
			} catch (NumberFormatException e) {
				System.out.println("[경고] 조회할 부서 번호는 숫자로만 입력하세요.\n");
			} // end of try-catch

		} while (true); // 수정할 부서 번호 검색 유효성 검사

		DeptDTO ddto = tdao.selectDept(dept_id);

		if (ddto != null) {
			// 수정할 글 번호가 글 목록에 존재하는 경우
			ddto.setDept_id(dept_id);

			System.out.println("==========================");
			System.out.println("[수정 전 부서명]" + ddto.getDept_name());
			System.out.println("[수정 전 부서장 사원 번호]" + ddto.getFk_dept_manager_id());
			System.out.println("==========================");

			System.out.print("▷ 부서명[변경하지 않으려면 엔터] : ");
			String dept_name = sc.nextLine();
			if (dept_name != null && !(dept_name.trim().isEmpty())) {
				ddto.setDept_name(dept_name);
			}

			System.out.print("▷ 부서장 사원 번호[변경하지 않으려면 엔터] : ");
			String s_fk_dept_manager_id = sc.nextLine();
			if (!s_fk_dept_manager_id.trim().isEmpty()) {

				try {
					fk_dept_manager_id = Integer.parseInt(s_fk_dept_manager_id);
				} catch (NumberFormatException e) {
					System.out.println("[경고] 부서장 사원 번호는 숫자만 입력할 수 있습니다. ");
				}

				ddto.setFk_dept_manager_id(fk_dept_manager_id);
			}

			int flag = 0;

			do {
				System.out.print(">> 정말로 부서를 수정하시겠습니까? [Y/N] => ");
				String yn = sc.nextLine();

				if ("y".equalsIgnoreCase(yn)) {
					flag = 1;
					break;
				} else if ("n".equalsIgnoreCase(yn)) { // 이렇게 되면 flag가 그대로 0으로 떨어지게 된다.
					break;
				} else {
					System.out.println(">> [Y/N]만 입력하세요. <<");
				}
			} while (true);

			// update sql 날려줘야한다.
			if (flag == 1) {
				result = tdao.updateDept(ddto);
			} else {
				System.out.println(">> 부서정보 수정을 취소하셨습니다. <<");
			}

		} else {
			// 수정할 글 번호가 글 목록에 없는 경우
			System.out.println(">> 수정할 부서번호 " + dept_id + "은(는) 없는 부서입니다. \n");
			// 이렇게 되면 return 값이 0 이다.
		}

		return result;
	} // end of private void dept_edit(Scanner sc)

	
	
	
	// 부서 삭제하는 메소드
	private int dept_delete(Scanner sc) {
		int result = 0;

		System.out.println("\n=== 부서 삭제하기 ===");
		dept_list();

		String s_dept_id = "";
		int dept_id = 0;

		do {
			System.out.print("▷ 삭제할 부서 번호 : ");
			s_dept_id = sc.nextLine();

			try {
				dept_id = Integer.parseInt(s_dept_id);
				break;
			} catch (NumberFormatException e) {
				System.out.println("[경고] 삭제할 부서 번호는 숫자로만 입력하세요.\n");
			}
		} while (true);

		DeptDTO ddto = tdao.selectDept(dept_id); // 글이 무슨 글인지 일단 보여준다.

		if (ddto != null) {
			// 삭제할 부서 번호가 부서 목록에 존재하는 경우
			System.out.println("==========================");
			System.out.println("[삭제할 부서번호]" + ddto.getDept_id());
			System.out.println("[삭제할 부서명]" + ddto.getDept_name());
			System.out.println("[삭제할 부서장ID]" + ddto.getFk_dept_manager_id());
			System.out.println("==========================");

			do {
				System.out.print("▷ 정말로 부서를 삭제하시겠습니까?[Y/N] ");
				String yn = sc.nextLine();

				if ("y".equalsIgnoreCase(yn)) {
					result = tdao.deleteDept(dept_id); // 여기서 result가 0에서 정상이라면 1로 변경, SQL구문 오류의 경우에는 -1 로 변경
					break;
				} else if ("n".equalsIgnoreCase(yn)) {
					System.out.println(">>> 부서 삭제를 취소하셨습니다. <<< \n"); // result의 값은 0 값이 넘어갈 것이다.
					break;
				} else {
					System.out.println("[경고] Y나 N만 입력하세요. \n");
				}

			} while (true);

		} else {
			// 삭제할 글 번호가 글 목록에 없는 경우
			System.out.println(">> 삭제할 부서번호 " + dept_id + "는 부서 목록에 존재하지 않습니다. \n");
			// 이렇게 되면 return 값이 0 이다.
		}

		return result;
		// result는 0 또는 1 또는 -1 이 나온다.

	} // end of private int dept_delete(Scanner sc)

	// 개인정보 조회 및 수정 메뉴
	private void emp_info(EmployeeDTO employee, Scanner sc) {

		String menu_no = ""; // 개인정보조회 및 개인정보수정 메뉴 선택

		do {
			System.out.println("\n================ 개인정보 ==================\n" + "1. 개인정보조회  2. 개인정보수정  3.뒤로가기 \n" // 로그인은
					+ "=========================================");

			int empid = employee.getEmp_id();
			System.out.print("▷ 번호 선택 : ");
			menu_no = sc.nextLine();

			switch (menu_no) {

			case "1": // 개인정보조회
				if (employee != null) { // 로그인을 한 상태
					employee = tdao.select_empInfo(employee);
					System.out.println(employee.toString());
				}
				break;

			case "2": // 개인정보수정
				emp_info_edit(employee, sc);
				break;

			case "3": // 프로그램 종료
				break;

			default:
				System.out.println(">>> 메뉴에 없는 번호 입니다. 다시 선택하세요. <<< \n");
				break;
			} // end of switch(s_choice)

		} while (!("3".equals(menu_no)));

	} // end of private void emp_info(EmployeeDTO employee, Scanner sc)

	// 개인 정보 수정하는 메소드
	private int emp_info_edit(EmployeeDTO employee, Scanner sc) {
		int result = 0;

		System.out.println("\n==========================================================");
		System.out.println("수정할 정보를 입력해주세요.(※ 엔터 입력 시 다음 정보 수정으로 넘어갑니다.)");
		System.out.println("==========================================================");

		do {
			System.out.print("▷ 비밀번호 : ");
			String passwd = sc.nextLine();
			if (myutil.checkPwd(passwd)) {
				employee.setPasswd(passwd);
				break;
			} else {
				System.out.println("[경고] 비밀번호는 대문자,소문자,숫자,특수기호가 혼합된 8글자 이상 15글자 이하로만 입력하셔야 합니다.\n");
			}
		} while (true);

		do {
			System.out.print("▷ 이메일 : ");
			String uq_email = sc.nextLine();

			if (!Pattern.matches("^[a-z0-9A-Z._-]*@[a-z0-9A-Z]*.[a-zA-Z.]*$", uq_email)) {
				System.out.println("[경고] 이메일 형식에 맞게 입력하십시오.\n");
			} else {
				break;
			}
		} while (true); // end of do whlie()-------------------------------------------------

		do {
			System.out.print("▷ 연락처(연락처는 하이픈을 빼고 입력하세요.): ");
			String uq_mobile = sc.nextLine();

			if (uq_mobile.length() != 11) {
				System.out.println("[경고] 연락처는 11자리로 입력해주세요.\n");
			} else if (!Pattern.matches("(02|010)\\d{3,4}\\d{4}", uq_mobile)) {
				System.out.println("[경고] 연락처는 숫자만 입력할 수 있습니다. \n");
			} else {
				break;
			}
		} while (true);

		System.out.print("▷ 주소 : ");
		String address = sc.nextLine();
		if (address != null && !(address.trim().isEmpty())) {
			employee.setAddress(address);
		}

		int flag = 0;

		do {
			System.out.print(">> 정말로 개인 정보를 수정하시겠습니까? [Y/N] => ");
			String yn = sc.nextLine();

			if ("y".equalsIgnoreCase(yn)) {
				flag = 1;
				break;
			} else if ("n".equalsIgnoreCase(yn)) { // 이렇게 되면 flag가 그대로 0으로 떨어지게 된다.
				break;
			} else {
				System.out.println(">> [Y/N]만 입력하세요. <<");
			}
		} while (true);

		// update sql 날려줘야한다.
		if (flag == 1) {
			result = tdao.update_emp_info(employee);

			if (result != -1) {
				System.out.println(">> 개인정보 수정이 정상적으로 완료 되었습니다. <<");
			}

		} else {
			System.out.println(">> 개인정보 수정을 취소하셨습니다. <<");
		}

		return result;

	} // private void emp_info_edit(Scanner
		// sc)------------------------------------------------------
		// *** 입사자 관리 메소드 *** //

	private void new_emp_insert(EmployeeDTO employee, Scanner sc) {

		String name = "";
		String passwd = "";
		String email = "";
		String mobile = "";
		String hire_date = "";
		int fk_dept_id = 0;
		int salary = 0;
		String jubun = "";
		int fk_manager_id = 0;
		String rank = "";
		String address = "";

		boolean check = true;

		int result = 0;

		System.out.println("\n=== 신규 입사자 정보 입력 ===");
		System.out.println("▷ 사원 번호는 자동으로 부여됩니다.");

		// 사원명 유효성 검사
		do {
			System.out.print("▷ 사원명 : ");
			name = sc.nextLine();

			if (name.trim().isEmpty()) {
				System.out.println("[경고] 사원명에는 빈칸이 올 수 없습니다.\n");
			} else if (name.length() < 2) {
				System.out.println("[경고] 사원명은 2글자 이상으로 입력하십시오.\n");
			} else if (!Pattern.matches("[가-힣]*$", name)) {
				System.out.println("[경고] 사원명은 한글만 입력 가능합니다.\n");
			} else {
				break;
			}

		} while (true);

		// 비밀번호 유효성 검사
		do {
			System.out.print("▷ 비밀번호(대문자,소문자,숫자,특수문자를 혼합하여 8글자 이상 15글자 이하로 만드세요): ");
			passwd = sc.nextLine();

			if (passwd.trim().isEmpty()) {
				System.out.println("[경고] 비밀번호에는 빈칸이 올 수 없습니다.\n");
			} else if (!myutil.checkPwd(passwd)) {
				System.out.println("[경고] 비밀번호는 대문자,소문자,숫자,특수기호가 혼합된 8글자 이상 15글자 이하로만 입력하셔야 합니다.\n");
			} else {
				break;
			}

		} while (true);

		// 이메일 유효성 검사
		do {
			System.out.print("▷ 이메일 : ");
			email = sc.nextLine();

			if (email.trim().isEmpty()) {
				System.out.println("[경고] 이메일은 공백이 들어올 수 없습니다.\n ");
			} else if (!Pattern.matches("^[a-z0-9A-Z._-]*@[a-z0-9A-Z]*.[a-zA-Z.]*$", email)) {
				System.out.println("[경고] 이메일 형식에 맞게 입력하십시오.\n");
			} else {
				break;
			}

		} while (true);

		// 연락처 유효성 검사
		do {
			System.out.print("▷ 연락처(연락처는 하이픈을 빼고 입력하세요.): ");
			mobile = sc.nextLine();

			if (mobile.trim().isEmpty()) {
				System.out.println("[경고] 연락처는 공백이 들어올 수 없습니다.\n ");
			} else if (mobile.length() != 11) {
				System.out.println("[경고] 연락처는 11자리로 입력해주세요.\n");
			} else if (!Pattern.matches("(02|010)\\d{3,4}\\d{4}", mobile)) {
				System.out.println("[경고] 연락처는 숫자만 입력할 수 있습니다. \n");
			} else {
				break;
			}

		} while (true);

		// 입사일자 유효성 검사
		do {
			System.out.print("▷ 입사일자 : ");
			hire_date = sc.nextLine();

			if (hire_date.trim().isEmpty()) {
				System.out.println("[경고] 입사일자는 반드시 입력하여야 합니다. \n");
			} else if (!Pattern.matches("^((19|20)\\d\\d)?([- /.])?(0[1-9]|1[012])([- /.])?(0[1-9]|[12][0-9]|3[01])$",
					hire_date)) {
				System.out.println("[경고] 입사일자는 YYYY-MM-DD의 형식으로 입력해주세요. \n");
			} else {
				break;
			}
		} while (true);

		// 부서번호 유효성검사(기존에 존재하는 부서인지 여부도 추가해 주어야 한다.)
		do {
			check = true;

			System.out.print("▷ 부서번호 : ");
			String s_fk_dept_id = sc.nextLine();

			if (s_fk_dept_id.trim().isEmpty()) {
				System.out.println("[경고] 신규 입사자 부서 번호는 공백이나 빈칸이 올 수 없는 필수 입력사항입니다. \n");
			}

			try {
				fk_dept_id = Integer.parseInt(s_fk_dept_id);
			} catch (NumberFormatException e) {
				System.out.println("[경고] 신규 입사자 부서 번호는 숫자로만 입력하세요.\n");
			}

			List<DeptDTO> dept_id_list = tdao.dept_id_list();

			int flag = 0;

			for (int i = 0; i < dept_id_list.size(); i++) {
				if (fk_dept_id == dept_id_list.get(i).getDept_id()) {
					flag = 1;
				}
			}

			if (flag == 1) {
			} else {
				check = false;
				System.out.println("[경고] 존재하는 부서 번호를 입력하세요.\n");
				flag = 0; // 수정0803
			}

		} while (!check);

		// 기본급 유효성 검사
		do {
			check = true;

			System.out.print("▷ 기본급 : ");
			String s_salary = sc.nextLine();

			if (s_salary.trim().isEmpty()) {
				System.out.println("[경고] 신규 입사자 기본급은 공백이나 빈칸이 올 수 없는 필수 입력사항입니다. \n");
			}

			try {
				salary = Integer.parseInt(s_salary);
				check = false;
				break; // 숫자가 되어지면 break;
			} catch (NumberFormatException e) {
				System.out.println("[경고] 신규 입사자 기본급은 숫자로만 입력하세요.\n");
			}

		} while (!check);

		// 주민등록번호 유효성 검사
		do {
			check = false;
			int flag_isempty = 0;
			int flag_length = 0;
			int flag_num = 0;
			int flag_overlap = 0;
			int flag_form = 0;

			System.out.print("▷ 주민등록번호 : ");
			jubun = sc.nextLine();

			if (jubun.trim().isEmpty()) {
				System.out.println("[경고] 주민등록번호는 반드시 입력하여야 합니다. \n");
				flag_isempty = 1;
			}

			if (jubun.length() != 13) {
				System.out.println("[경고] 주민등록번호는 13자리로 입력하십시오. \n");
				flag_length = 1;
			}

			if (!Pattern.matches("^[0-9]*$", jubun)) {
				System.out.println("[경고] 주민등록번호는 숫자만 입력할 수 있습니다. \n");
				flag_num = 1;
			}

			if (!Pattern.matches("\\d\\d(0[1-9]|1[0-2])([0-2][1-9]|3[0-1])[1-4]\\d{6}", jubun)) {
				System.out.println("[경고] 주민등록번호 형식에 맞게 입력해주세요. \n");
				flag_form = 1;
			}

			List<EmployeeDTO> emp_overlap_list = tdao.emp_overlap_list();

			for (int i = 0; i < emp_overlap_list.size(); i++) {
				if (!(jubun.equalsIgnoreCase(emp_overlap_list.get(i).getUq_jubun()))) {
					flag_overlap = 0;
				} else if (jubun.equalsIgnoreCase(emp_overlap_list.get(i).getUq_jubun())) {
					flag_overlap = 1;
					break;
				}
			}

			if (flag_overlap == 1) {
				System.out.println("[경고] 중복된 주민등록번호이니 다시 입력하세요.\n");
			}
			if (flag_isempty == 0 && flag_length == 0 && flag_num == 0 && flag_overlap == 0 && flag_form == 0) {
				check = true;
			} else {
				check = false;
			}
		} while (!check);

		// 직속 상관 사원번호 유효성 검사
		do {
			check = true;

			System.out.print("▷ 직속 상관 사원번호 : ");
			String s_fk_manager_id = sc.nextLine();

			if (s_fk_manager_id.trim().isEmpty()) {
				System.out.println("[경고] 신규 입사자직속 상관 사원번호는 공백이나 빈칸이 올 수 없는 필수 입력사항입니다. \n");
			}

			try {
				fk_manager_id = Integer.parseInt(s_fk_manager_id);
			} catch (NumberFormatException e) {
				System.out.println("[경고] 신규 입사자 직속 상관 사원번호는 숫자로만 입력하세요.\n");
			}
			List<EmployeeDTO> emp_id_list = tdao.emp_id_list();

			int flag = 0;

			for (int i = 0; i < emp_id_list.size(); i++) {
				if (fk_manager_id == emp_id_list.get(i).getEmp_id()) {
					flag = 1;
				}
			}
			if (flag == 1) {
			} else {
				check = false;
				System.out.println("[경고] 존재하는 사원 번호를 입력하세요.\n");
			}
		} while (!check);

		// 직위 유효성 검사
		do {
			System.out.print("▷ 직위 : ");
			rank = sc.nextLine();

			if (rank.trim().isEmpty()) {
				System.out.println("[경고] 직위는 반드시 입력해주어야 합니다. \n");
			} else {
				break;
			}

		} while (true);

		// 주소 유효성 검사
		do {
			System.out.print("▷ 주소 : ");
			address = sc.nextLine();

			if (address.trim().isEmpty()) {
				System.out.println("[경고] 주소는 반드시 입력해주어야 합니다. \n");
			} else {
				break;
			}

		} while (true);

		// 값 입력 완료 후 insert y/n
		int flag = 0;

		do {
			System.out.print(">> 정말로 신규사원을 추가하시겠습니까? [Y/N] => ");
			String yn = sc.nextLine();

			if ("y".equalsIgnoreCase(yn)) {
				flag = 1;
				break;
			} else if ("n".equalsIgnoreCase(yn)) { // 이렇게 되면 flag가 그대로 0으로 떨어지게 된다.
				break;
			} else {
				System.out.println(">> [Y/N]만 입력하세요. <<");
			}
		} while (true);

		if (flag == 1) { // 사원을 추가하는 경우

			employee.setName(name);
			employee.setPasswd(passwd);
			employee.setUq_email(email);
			employee.setUq_mobile(mobile);
			employee.setHire_date(hire_date);
			employee.setFk_dept_id(fk_dept_id);
			employee.setSalary(salary);
			employee.setUq_jubun(jubun);
			employee.setFk_manager_id(fk_manager_id);
			employee.setRank(rank);
			employee.setAddress(address);

			result = tdao.emp_insert_sql(employee); // 정상이면 1, rollback 이면 -1이 나온다.
			// 1 또는 -1

			System.out.println(">> 신규 입사자 추가가 정상적으로 완료되었습니다. <<");
			// 부여된 부서번호는 "+ddto.Department_Id+"번 입니다.

		} else { // flag != 1로 글쓰기를 취소한 경우 // else면 result가 0이다.
			result = 0; // 초기값이 0이기 때문에 else는 삭제해도 상관 없다.
			System.out.println(">> 신규 사원 추가가 취소되었습니다. <<");
		}
	} // end of private void new_emp_insert(EmployeeDTO employee, Scanner sc)

	// 연차신청하는 메소드
	private void YsinCheong(EmployeeDTO employee, Scanner sc) {

		System.out.println("신청자ID : " + employee.getEmp_id());

		// System.out.println("사용가능연차: " + member.getAnual());
		String start_date = "";
		String end_date = "";

		do {
			System.out.println("시작일자 : ");
			start_date = sc.nextLine();

			if (start_date.trim().isEmpty()) {
				System.out.println("[경고] 입사일자는 반드시 입력하여야 합니다. \n");
			} else if (!Pattern.matches("^((19|20)\\d\\d)?([- /.])?(0[1-9]|1[012])([- /.])?(0[1-9]|[12][0-9]|3[01])$",
					start_date)) {
				System.out.println("[경고] 입사일자는 YYYY-MM-DD의 형식으로 입력해주세요. \n");
			} else {
				break;
			}
		} while (true);
		
		do {
			System.out.println("종료일자 : ");
			end_date = sc.nextLine();

			if (end_date.trim().isEmpty()) {
				System.out.println("[경고] 입사일자는 반드시 입력하여야 합니다. \n");
			} else if (!Pattern.matches("^((19|20)\\d\\d)?([- /.])?(0[1-9]|1[012])([- /.])?(0[1-9]|[12][0-9]|3[01])$",
					end_date)) {
				System.out.println("[경고] 입사일자는 YYYY-MM-DD의 형식으로 입력해주세요. \n");
			} else {
				break;
			}
		} while (true);

		Map<String, String> paraMap = new HashMap<>();
		String userid = Integer.toString(employee.getEmp_id());
		paraMap.put("Emp_id", Integer.toString(employee.getEmp_id())); // (key값, value)
		paraMap.put("start_date", start_date);
		paraMap.put("end_date", end_date);

		int result = tdao.YsinCheong(paraMap, sc);

		if (result == 1) {
			System.out.println("연차신청 기안작성을 완료하였습니다!");
		} else if (result == -1) {
			System.out.println("연차신청 기안작성을 취소하였습니다!");
		} else {
			System.out.println("연차신청 기안작성을 실패하였습니다!");
		}

	} // end of private void sinCheong(EmployeeDTO member, Scanner sc) -------------

	// 전직원 연차조회
	private void Ychacheck(EmployeeDTO employee, Scanner sc) {

		String choiceno;
		do {
			System.out.println("\n============================= 연차 정보 조회 =============================");
			System.out.println("1. 연차번호로 검색  2. 사원번호로 검색  3. 부서명으로 검색  4. 전체 조회  5. 뒤로가기");
			System.out.println("=======================================================================");
			System.out.print("▷ 메뉴 번호 입력 : ");
			choiceno = sc.nextLine();
			if (!(choiceno.equals("1") || choiceno.equals("2") || choiceno.equals("3") || choiceno.equals("4")
					|| choiceno.equals("5"))) {
				System.out.println("[경고] 메뉴에 있는 번호만 선택해주세요!");
			}
//				} while (!(choiceno.equals("1")||choiceno.equals("2")||choiceno.equals("3")||choiceno.equals("4")||choiceno.equals("5")));

			if (choiceno.equals("1") || choiceno.equals("2") || choiceno.equals("3")) {

				List<AnnualDTO> employeeList2 = tdao.Ychacheck();
				employeeList2 = tdao.Ychacheck(choiceno, sc);

				if (employeeList2.size() == 0) {
					System.out.println(">> 입력하신 조건에 맞는 연차가 존재하지 않습니다.<<");
				} else {
					System.out.println(
							"------------------------------------------------------------------------------------------------");
					System.out
							.println("연차번호  신청사원번호  신청사원명      부서명      연차시작일         연차종료일         승인상태        연차승인일");
					System.out.println(
							"------------------------------------------------------------------------------------------------");

					StringBuilder sb2 = new StringBuilder();
					for (AnnualDTO emp : employeeList2) {

						sb2.append(emp.getVacation_id() + "       " + emp.getFk_writer_emp_id() + "       "
								+ emp.getEdto().getName() + "        " + emp.getDdto().getDept_name() + "       "
								+ emp.getStart_date() + "      " + emp.getEnd_date() + "      " + emp.getCk_approval()
								+ "   " + emp.getApproval_date() + "\n");
					} // end of for------------------------------

					System.out.println(sb2.toString());
				}
			} else if (choiceno.equals("4")) {
				System.out.println(
						"------------------------------------------------------------------------------------------------");
				System.out.println("연차번호  신청사원번호  신청사원명      부서명      연차시작일         연차종료일         승인상태        연차승인일");
				System.out.println(
						"------------------------------------------------------------------------------------------------");

				List<AnnualDTO> employeeList = tdao.Ychacheck();
				// 연차 내역 전체 확인 후 emplist 로 받아옴

				StringBuilder sb = new StringBuilder();
				for (AnnualDTO emp : employeeList) {

					sb.append(emp.getVacation_id() + "       " + emp.getFk_writer_emp_id() + "       "
							+ emp.getEdto().getName() + "        " + emp.getDdto().getDept_name() + "       "
							+ emp.getStart_date() + "      " + emp.getEnd_date() + "      " + emp.getCk_approval()
							+ "   " + emp.getApproval_date() + "\n");
				} // end of for------------------------------
					// 전체연차 출력

				System.out.println(sb.toString());
			}

		} while (!choiceno.equals("5"));
	}// end of private void Ychacheck(EmployeeDTO employee, Scanner sc)-------------

	// 문서 기안하기$$$$
	private void Docinsert(EmployeeDTO employee, Scanner sc) {
		System.out.println("▷ 문서를 추가하시겠습니까?[Y/N]");
		int result = 0;
		int n = Util.yn(sc);
		if (n == 1) {// 문서 추가
			System.out.println("▷ 내부결재(서면결재) 문서입니까?[Y/N]");
			n = Util.yn(sc);
			if (n == 1) {
				tdao.InnerDocinsert(employee, sc);
			} else {
				result = tdao.Docinsert(employee, sc);
			}
		} else {
			System.out.println("문서를 작성하지 않았습니다.");
		}

	}

	// $$$$
	private void DocView(EmployeeDTO employee, Scanner sc) {

		StringBuilder sb = new StringBuilder();
		sb = tdao.DocView(employee, sc);

		if (sb.length() != 0) {

			System.out.println(
					"\n---------------------------------------------- [문서 목록] ----------------------------------------------");
			System.out.println(Util.cut_data("문서번호",4)+"\t"+Util.cut_data("기안자번호",5)+"\t"+Util.cut_data("문서제목",7)+"\t"+Util.cut_data("문서내용",10)+"\t"+Util.cut_data("작성일자",10)+"\t"+Util.cut_data("승인단계",10)+"\t"+Util.cut_data("승인사원번호",10)+"\t"+Util.cut_data("승인사원명",10)+"\t"+Util.cut_data("승인여부",10)+"\t"+Util.cut_data("코멘트",10)+"\t"+Util.cut_data("승인날짜",10)+"\t"+Util.cut_data("승인일",10));
			System.out.println(
					"---------------------------------------------------------------------------------------------------------");
			System.out.println(sb.toString());

		} else {
			System.out.println("기안한 문서내역이 존재하지 않습니다");
		}
	}

	// 우선결재
	private void DocAproval(EmployeeDTO employee, Scanner sc) {

		StringBuilder sb = new StringBuilder();
		sb = tdao.DocAproval(employee, sc);
		int n = 0;

		if (sb.length() != 0) {

			System.out.println(
					"\n---------------------------------------------- [문서 목록] ----------------------------------------------");
			System.out.println("문서번호\t기안작성자사원번호\t문서내용\t\t작성일자\t\t승인단계\t\t승인사원번호\t승인사원명\t승인여부\t코멘트\t\t\t승인날짜\t");
			System.out.println(
					"---------------------------------------------------------------------------------------------------------");
			System.out.println(sb.toString());
			String choiceno;
			String Docno;

			do {// 승인처리할 문서의 문서번호 입력
				System.out.print("▷ 처리할 문서의 문서번호를 입력해주세요 : ");
				Docno = sc.nextLine();

				try {
					Integer.parseInt(Docno);
					int a = tdao.DocCheck2(employee, sc, Docno);

					if (a != 1) {
						System.out.println(Docno + "번에 대한 처리권한이 없습니다.");
					} else {
						break;
					}
				} catch (NumberFormatException e) {
					System.out.println("[경고] 승인할 문서의 문서번호는 숫자로만 입력하세요!! \n");
				}
			} while (true);

			do {
				System.out.println(Docno + "번의 문서를 어떻게 처리하시겠습니까?");
				System.out.print("1.승인  2.반려	3.보류");
				choiceno = sc.nextLine();

				if (!(choiceno.equals("1") || choiceno.equals("2") || choiceno.equals("3"))) {
					System.out.println(">> 1 또는 2 또는 3 만 선택해주세요! <<");
				}

			} while (!(choiceno.equals("1") || choiceno.equals("2") || choiceno.equals("3")));

			n = tdao.Aproval2(employee, choiceno, Docno, sc);

			if (n == 0) {// 안나와야함
				System.out.println(">> 처리할 문서가 존재하지 않습니다.");
			} else if (n > 0) {
				System.out.println(">> 문서가 성공적으로 처리되었습니다! <<");
			} else {// -1 인 경우
				System.out.println(">> 문서처리에 실패하였습니다! <<");
			}
		} else {
			System.out.println("승인할 문서내역이 존재하지 않습니다");
		}
	}// end of method()---------------------

	// 순서결재
	private void DocAproval_2(EmployeeDTO employee, Scanner sc) {
		// (a)전결자가 (b)같은문서에대해 (c)결재한 문서중 내가 결재자인 문서(d(결재가능상태))만 가져오기
		StringBuilder sb = new StringBuilder();
		sb = tdao.DocAproval2(employee, sc);
		int n = 0;

		if (sb.length() != 0) {

			System.out.println(
					"\n---------------------------------------------- [문서 목록] ----------------------------------------------");
			System.out.println("문서번호\t기안작성자사원번호\t문서내용\t\t작성일자\t\t승인단계\t\t승인사원번호\t승인사원명\t승인여부\t코멘트\t\t\t승인날짜\t");
			System.out.println(
					"---------------------------------------------------------------------------------------------------------");
			System.out.println(sb.toString());
			String choiceno;
			String Docno;

			do {// 승인처리할 문서의 문서번호 입력
				System.out.print("▷ 처리할 문서의 문서번호를 입력해주세요 : ");
				Docno = sc.nextLine();

				try {
					Integer.parseInt(Docno);
					int a = tdao.DocCheck(employee, sc, Docno);
					if (a != 1) {
						System.out.println(Docno + "번에 대한 처리권한이 없습니다.");
					} else {
						break;
					}
				} catch (NumberFormatException e) {
					System.out.println("[경고] 승인할 문서의 문서번호는 숫자로만 입력하세요!! \n");
				}
			} while (true);

			do {
				System.out.println(Docno + "번의 문서를 어떻게 처리하시겠습니까?");
				System.out.print("1.승인  2.반려	3.보류");
				choiceno = sc.nextLine();

				if (!(choiceno.equals("1") || choiceno.equals("2") || choiceno.equals("3"))) {
					System.out.println(">> 1 또는 2 또는 3 만 선택해주세요! <<");
				}

			} while (!(choiceno.equals("1") || choiceno.equals("2") || choiceno.equals("3")));

			n = tdao.Aproval(employee, choiceno, Docno, sc);

			if (n == 0) {// 안나와야함
				System.out.println(">> 처리할 문서가 존재하지 않습니다.");
			} else if (n > 0) {
				System.out.println(">> 문서가 성공적으로 처리되었습니다! <<");
			} else {// -1 인 경우
				System.out.println(">> 문서처리에 실패하였습니다! <<");
			}
		} else {
			System.out.println("승인할 문서내역이 존재하지 않습니다");
		}

	} // 반려, 승인 전 문서 수정

	private void DocChange(EmployeeDTO employee, Scanner sc) {
		System.out.println("* 문서수정은 반려된 문서 혹은 아직 결재가 진행되지 않은 문서에 한하여 가능합니다 * ");
		int n = tdao.DocChange(employee, sc);
		if (n == 1) {
			System.out.println("문서가 성공적으로 수정되었습니다!");
		} else {
			System.out.println("문서가 변경되지 않았습니다!");
		}
	}
}
