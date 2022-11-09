package hrmanagement;

import java.sql.*;
import java.util.*;

public class TotalDAO implements InterTotalDAO {

	Connection conn = ProjectDBConnection.getConn();
	PreparedStatement pstmt;
	ResultSet rs;

	// 자원 반납 메소드 //
	public void close() {
		try {
			if (pstmt != null)
				pstmt.close();
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} // end of try catch (SQLException e)----------
	} // end of public void close()----------------

	
	// 관리자 로그인 메소드 재정의 //
	// 일반사원 로그인 메소드 재정의 (인터페이스 -> 클래스)
	@Override
	public EmployeeDTO login_employee(Map<String, String> loginMap) {

		EmployeeDTO employee = null; // 로그인이 안될경우 null 값 반환을 위해 초기값 null로 설정

		try {
			String sql = " select EMP_ID,PASSWD,NAME " + " ,UQ_EMAIL,UQ_MOBILE,HIRE_DATE "
					+ " ,FK_DEPT_ID,CK_RET_STATUS,SALARY,UQ_JUBUN,FK_MANAGER_ID "
					+ " ,RANK,ADDRESS ,REMAIN_ANNUALLEAVE,CK_MANAGER_RANK  " + " from tbl_employees "
					+ " where CK_RET_STATUS = 0 and emp_id = ? and passwd = ? ";

			pstmt = conn.prepareStatement(sql); // SQL문 전달할 객체 생성
			pstmt.setString(1, loginMap.get("emp_id")); // 파라미터로 전달받은 Map에서 employee_id 첫번째 위치홀더에 입력
			pstmt.setString(2, loginMap.get("passwd")); // 파라미터로 전달받은 Map에서 passwd 두번째 위치홀더에 입력

			rs = pstmt.executeQuery(); // select 되어진 실행결과를 ResultSet 객체에 저장

			if (rs.next()) {

				employee = new EmployeeDTO(); // 위에서 null 로 선언했기 때문에 객체 생성

				employee.setEmp_id(rs.getInt("EMP_ID"));
				employee.setPasswd(rs.getString("PASSWD"));
				employee.setName(rs.getString("NAME"));
				employee.setUq_email(rs.getString("UQ_EMAIL"));
				employee.setUq_mobile(rs.getString("UQ_MOBILE"));
				employee.setHire_date(rs.getString("HIRE_DATE"));
				employee.setFk_dept_id(rs.getInt("FK_DEPT_ID"));
				employee.setCk_ret_status(rs.getInt("CK_RET_STATUS"));
				employee.setSalary(rs.getInt("SALARY"));
				employee.setUq_jubun(rs.getString("UQ_JUBUN"));
				employee.setFk_manager_id(rs.getInt("FK_MANAGER_ID"));
				employee.setRank(rs.getString("RANK"));
				employee.setAddress(rs.getString("ADDRESS"));
				employee.setRemain_annualleave(rs.getInt("REMAIN_ANNUALLEAVE"));
				employee.setCk_manager_rank(rs.getInt("CK_MANAGER_RANK"));

			} // end of if()------------------------------------------------

		} catch (SQLSyntaxErrorException e) {
			System.out.println("올바른 아이디, 암호를 입력하세요");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return employee;
	}

	// 공지사항 목록보기를 해주는 메소드 //
	@Override
	public List<NoticeDTO> noticelist() {

		List<NoticeDTO> noticelist = new ArrayList<>();

		try {

			String sql = " select notice_id, fk_writer_emp_id, regist_date,title,content,name "
					+ " from tbl_notice N join tbl_employees E " + " on N.fk_writer_emp_id = E.emp_id "
					+ " order by notice_id desc ";
			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				NoticeDTO ndto = new NoticeDTO();

				ndto.setNotice_id(rs.getInt("NOTICE_ID"));
				ndto.setTitle(rs.getString("TITLE"));
				// 작성자 이름을 bdto 를 담으려 하는데 bdto에는 작성자 아이디만 있을 뿐 이름은 없다..(BoardDTO 주석 참조)
				// join 의 경우 아래와 같이 한다.

				EmployeeDTO employee = new EmployeeDTO();
				employee.setName(rs.getString("NAME"));

				ndto.setEmployee(employee);

				ndto.setRegist_date(rs.getString("REGIST_DATE"));
				//////////////////////////////////////////////////////////

				noticelist.add(ndto);

			} // end of while()------------------------------------

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return noticelist;

	}

	// 공지사항 조회수 1 증가 메소드 //
	@Override
	public void updateViewCount(String notice_id) {
		try {
			String sql = " update tbl_notice set notice_view = notice_view + 1 " + " where notice_id = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, notice_id);

			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	// 공지사항 작성 메소드 //
	@Override
	public int notice_write(NoticeDTO ndto) {
		int result = 0;

		try {

			String sql = " insert into tbl_notice(notice_id, fk_writer_emp_id, regist_date , title, content, passwd) "
					+ " values (seq_notice_id.nextval, ? , sysdate, ?,  ? , ? ) ";

			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, ndto.getFk_writer_emp_id());
			pstmt.setString(2, ndto.getTitle());
			pstmt.setString(3, ndto.getContent());
			pstmt.setInt(4, ndto.getPasswd());

			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println("SQL 구문 에러 발생");
			result = -1;
		}
		try {
			conn.rollback();// 롤백을 해준다.
			result = -1;
		} catch (SQLException e1) {
		} finally {
			close();// 자원 반납하기
		} // end of try catch finally-------------------------------------------------

		return result;

	}

	// 공지사항 수정 메소드(update 처리) //
	@Override
	public int notice_update(Map<String, String> paraMap) {

		int result = 0;

		try {

			String sql = " update tbl_notice set title = ? , content = ? " + " where notice_id = ? ";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, paraMap.get("title")); // 넘어온 원글의 글번호가 존재하지 않는 12312313 이 올 수 있다.
			pstmt.setString(2, paraMap.get("content"));
			pstmt.setString(3, paraMap.get("notice_id"));

			result = pstmt.executeUpdate();
			// update 가 성공되어지면 result 에는 1 이 들어온다.

		} catch (SQLException e) {
			result = -1;
		} finally {
			close(); // 자원 반납하기
		} // end of try catch finally-------------------------------------------------

		return result;

	}

	// 공지사항 삭제 메소드 재정의 //
	@Override
	public int deleteBoard(String notice_id) {

		int result = 0;

		try {
			String sql = " delete from tbl_notice " + " where notice_id = ? ";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, notice_id); // 넘어온 원글의 글번호가 존재하지 않는 23973273 이 올 수 있다.

			result = pstmt.executeUpdate();
			// delete 가 성공되어지면 result 에는 1 이 들어온다.

		} catch (SQLException e) {
			// e.getStackTrace();
			result = -1;
		} finally {
			close(); // 자원 반납하기
		} // end of try catch finally-------------------------------------------------

		return result;
		// 0 또는 1 또는 -1

	}// end of public int deleteBoard(String notice_id)--------------

	// 일반 게시판 글 목록보기 재정의 //
	@Override
	public List<BoardDTO> boardlist() {
		List<BoardDTO> boardlist = new ArrayList<>();

		try {
			String sql = "select post_id, title, name, regist_date, board_view, nvl(CMT.commentcnt, 0) As commentcnt "
					+ " from " + " ( "
					+ " select B.post_id, B.title , E.name, to_char(B.regist_date,'yyyy-mm-dd hh24:mi:ss') AS regist_date, B.board_view "
					+ " from tbl_board B join tbl_employees E " + " ON B.fk_writer_emp_id = E.emp_id " + " )A "
					+ " left join " + " ( " + " select fk_post_id " + "    , count(*) AS commentCNT "
					+ " from tbl_comment " + " group by fk_post_id " + " )CMT " + " ON A.post_id = CMT.fk_post_id "
					+ " order by post_id desc ";
			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				BoardDTO bdto = new BoardDTO();

				bdto.setPost_id(rs.getInt("POST_ID"));
				bdto.setTitle(rs.getString("TITLE"));

				EmployeeDTO employee = new EmployeeDTO();
				employee.setName(rs.getString("NAME"));

				bdto.setEmployee(employee);

				bdto.setRegist_date(rs.getString("REGIST_DATE"));
				bdto.setBoard_view(rs.getInt("board_view"));
				bdto.setCommentcnt(rs.getInt("COMMENTCNT"));
				//////////////////////////////////////////////////////////

				boardlist.add(bdto);

			} // end of while()------------------------------------

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return boardlist;

	}// end of public List<BoardDTO> boardlist()-----------------

	// 일반 게시판 글 내용 보여주는 메소드 //
	@Override
	public BoardDTO viewContent(Map<String, String> paraMap) {

		BoardDTO bdto = null;
		try {
			String sql = " select post_id, title, content, passwd, fk_writer_emp_id " + " from tbl_board "
					+ " where post_id = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("post_id"));

			rs = pstmt.executeQuery();

			if (rs.next()) {
				bdto = new BoardDTO();

				bdto.setTitle(rs.getString("TITLE"));
				bdto.setContent(rs.getString("CONTENT"));
				bdto.setFk_writer_emp_id(rs.getInt("FK_WRITER_EMP_ID"));
				bdto.setPasswd(rs.getInt("PASSWD"));

			} // end of if()------------------------------------

		} catch (SQLException e) {
			if (e.getErrorCode() == 01722) {
				System.out.println("글 번호는 정수로만 입력해주세요");
			}
		} finally {
			close();
		}
		return bdto;
	}

	// 원글에 대한 댓글 가져오는 메소드 //
	@Override
	public List<CommentDTO> commentList(String post_id) {

		List<CommentDTO> commentList = null; // 결과물이 없을 수도 있기 때문에 초기값을 null

		try {

			String sql = " select C.content, E.name, to_char(regist_date,'yyyy-mm-dd hh24:mi:ss') as regist_date "
					+ " from tbl_comment C  join tbl_employees E " + " on C.fk_writer_emp_id = E.emp_id "
					+ " where C.fk_post_id = ?  " + " order by C.Comment_id desc ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, post_id);
			rs = pstmt.executeQuery();

			int cnt = 0;
			while (rs.next()) {
				cnt++;

				CommentDTO cmtdto = new CommentDTO();

				cmtdto.setContent(rs.getString("CONTENT"));

				EmployeeDTO employee = new EmployeeDTO();
				employee.setName(rs.getString("NAME"));// name 값을 얻어와서

				cmtdto.setEmployee(employee); // cmtdto에 넣어준다.

				cmtdto.setRegist_date(rs.getString("REGIST_DATE"));

				if (cnt == 1) {
					commentList = new ArrayList<>();
				}
				commentList.add(cmtdto);

			} // end of while(rs.next())------------------------------

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return commentList; // 원글에 댓글이 없을 경우 null 반환, 있을 경우 댓글 내용이 담긴 commentList 반환
	}

	// 일반 게시판 글 작성 메소드 //
	@Override
	public int write(BoardDTO bdto) {

		int result = 0;

		try {
			// Transaction 처리를 위해서 수동 commit 으로 전환 시킨다.

			String sql = " insert into tbl_board(post_id, title, content, passwd, fk_writer_emp_id) "
					+ " values(seq_post_id.nextval ,?,?,?,? )";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, bdto.getTitle());
			pstmt.setString(2, bdto.getContent());
			pstmt.setInt(3, bdto.getPasswd());
			pstmt.setInt(4, bdto.getFk_writer_emp_id());

			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();// 자원 반납하기
		} // end of try catch finally-------------------------------------------------

		return result;

	}// end of public int write(BoardDTO bdto)-----------------------------------------------------

	// 일반 글 내용보기 메소드
	@Override
	public BoardDTO viewContent2(String post_id) {
		BoardDTO bdto = null;

		try {

			String sql = " select title, content, FK_WRITER_EMP_ID, passwd " + " from tbl_board "
					+ " where post_id = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, post_id);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				bdto = new BoardDTO();

				bdto.setTitle(rs.getString("TITLE"));
				bdto.setContent(rs.getString("CONTENT"));
				bdto.setFk_writer_emp_id(rs.getInt("FK_WRITER_EMP_ID"));
				bdto.setPasswd(rs.getInt("PASSWD"));

			} // end of if()------------------------------------

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return bdto;

	}// end of public BoardDTO viewContent2(String post_id)-----------------------------------------

	// 게시글 수정 메소드 //
	@Override
	public int updateBoard(Map<String, String> paraMap) {

		int result = 0;
		try {
			String sql = " update tbl_board set title = ? , content = ? " + " where post_id = ? ";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, paraMap.get("title")); // 넘어온 원글의 글번호가 존재하지 않는 12312313 이 올 수 있다.
			pstmt.setString(2, paraMap.get("content"));
			pstmt.setString(3, paraMap.get("post_id"));

			result = pstmt.executeUpdate();
			// update 가 성공되어지면 result 에는 1 이 들어온다.

		} catch (SQLException e) {
			// e.getStackTrace();
			result = -1;
		} finally {
			close(); // 자원 반납하기
		} // end of try catch finally-------------------------------------------------

		return result;
		// 0 또는 1 또는 -1

	}// end of public int updateBoard(Map<String, String> paraMap)--------------------------------------------

	// 일반 게시판 글 삭제 메소드 //
	@Override
	public int deleteBoard2(String post_id) {

		int result = 0;

		try {
			String sql = " delete from tbl_board " + " where post_id = ? ";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, post_id); // 넘어온 원글의 글번호가 존재하지 않는 23973273 이 올 수 있다.

			result = pstmt.executeUpdate();
			// delete 가 성공되어지면 result 에는 1 이 들어온다.

		} catch (SQLException e) {
			// e.getStackTrace();
			result = -1;
		} finally {
			close(); // 자원 반납하기
		} // end of try catch finally-------------------------------------------------

		return result;
		// 0 또는 1 또는 -1
	}// end of public int deleteBoard2(String post_id)-----------------

	// 댓글 쓰기 //
	@Override
	public int writeComment(CommentDTO cmdto) {
		int result = 0;

		try {

			String sql = " insert into tbl_comment( comment_id, fk_post_id, fk_writer_emp_id, content ) "
					+ " values(seq_comment_id.nextval ,? ,? ,? ) ";

			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, cmdto.getFk_post_id()); // 넘어온 원글의 글번호가 존재하지 않는 12312313 이 올 수 있다.
			pstmt.setInt(2, cmdto.getFk_writer_emp_id());
			pstmt.setString(3, cmdto.getContent());

			result = pstmt.executeUpdate();
			// insert 가 성공되어지면 result 에는 1 이 들어온다.

		} catch (SQLException e) {
			if (e.getErrorCode() == 2291) {
				// ORA-02291: 무결성 제약조건(JDBC_USER.FK_TBL_COMMENT_FK_BOARDNO)이 위배되었습니다- 부모 키가 없습니다
				System.out.println(">> [경고] 댓글을 작성할 원글의 번호가 존재하지 않습니다.  << \n");
				result = -1;
			} else {
				e.printStackTrace();
			}
		} finally {
			close(); // 자원 반납하기
		} // end of try catch finally-------------------------------------------------

		return result;
	}

	// 공지사항 글 내용 보여주는 메소드
	@Override
	public NoticeDTO viewContent(String notice_id) {

		NoticeDTO ndto = null;

		try {
			String sql = " select notice_id,fk_writer_emp_id,title,content,passwd " + " from tbl_notice "
					+ " where notice_id = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, notice_id);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				ndto = new NoticeDTO();

				ndto.setNotice_id(rs.getInt("notice_id"));
				ndto.setFk_writer_emp_id(rs.getInt("FK_WRITER_EMP_ID"));
				ndto.setTitle(rs.getString("TITLE"));
				ndto.setContent(rs.getString("CONTENT"));
				ndto.setPasswd(rs.getInt("PASSWD"));

			} // end of if()------------------------------------

		} catch (SQLException e) {
			if (e.getErrorCode() == 01722) {
				System.out.println("정수만 입력이 가능합니다.");
			}
		} finally {
			close();
		}

		return ndto;
	}

	// 일주일간 일자별 게시글 작성 건수
	@Override
	public Map<String, Integer> statisticsByWeek() {

		Map<String, Integer> resultmap = new HashMap<>();

		try {
			String sql = " select COUNT(regist_date) AS TOTAL "
					+ "      ,SUM(decode( to_date(to_char(sysdate,'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(REGIST_DATE,'yyyy-mm-dd') ,'yyyy-mm-dd'),6, 1 ,0) )AS PREVIOUS6 "
					+ "      ,SUM(decode( to_date(to_char(sysdate,'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(REGIST_DATE,'yyyy-mm-dd') ,'yyyy-mm-dd'),5, 1 ,0) )AS PREVIOUS5 "
					+ "      ,SUM(decode( to_date(to_char(sysdate,'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(REGIST_DATE,'yyyy-mm-dd') ,'yyyy-mm-dd'),4, 1 ,0) )AS PREVIOUS4 "
					+ "      ,SUM(decode( to_date(to_char(sysdate,'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(REGIST_DATE,'yyyy-mm-dd') ,'yyyy-mm-dd'),3, 1 ,0) )AS PREVIOUS3 "
					+ "      ,SUM(decode( to_date(to_char(sysdate,'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(REGIST_DATE,'yyyy-mm-dd') ,'yyyy-mm-dd'),2, 1 ,0) )AS PREVIOUS2 "
					+ "      ,SUM(decode( to_date(to_char(sysdate,'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(REGIST_DATE,'yyyy-mm-dd') ,'yyyy-mm-dd'),1, 1 ,0) )AS PREVIOUS1 "
					+ "      ,SUM(decode( to_date(to_char(sysdate,'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(REGIST_DATE,'yyyy-mm-dd') ,'yyyy-mm-dd'),0, 1 ,0) )AS TODAY "
					+ " from tbl_board "
					+ " where to_date(to_char(sysdate,'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(to_char(REGIST_DATE,'yyyy-mm-dd') ,'yyyy-mm-dd') < 7 ";

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			rs.next(); // 위에 있던 커서 내리기

			resultmap.put("TOTAL", rs.getInt(1));
			resultmap.put("PREVIOUS6", rs.getInt(2));
			resultmap.put("PREVIOUS5", rs.getInt(3));
			resultmap.put("PREVIOUS4", rs.getInt(4));
			resultmap.put("PREVIOUS3", rs.getInt(5));
			resultmap.put("PREVIOUS2", rs.getInt(6));
			resultmap.put("PREVIOUS1", rs.getInt(7));
			resultmap.put("TODAY", rs.getInt(8));

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return resultmap;

	}

	// 이번달 일자별 게시글 작성건수
	@Override
	public List<Map<String, String>> statisticsByCurrentMonth() {

		List<Map<String, String>> mapList = new ArrayList<>();

		try {
			String sql = " select decode( grouping(to_char(REGIST_DATE,'yyyy-mm-dd')) ,0,to_char(REGIST_DATE,'yyyy-mm-dd'),'전체') AS REGIST_DATE "
					+ "     , count(*) AS cnt " + " from tbl_board "
					+ " where to_char(REGIST_DATE, 'yyyy-mm') = to_char(sysdate,'yyyy-mm') "
					+ " group by rollup (to_char(REGIST_DATE,'yyyy-mm-dd')) ";

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {

				Map<String, String> map = new HashMap<>();

				map.put("REGIST_DATE", rs.getString(1));
				map.put("CNT", rs.getString(2));

				mapList.add(map);
			} // end of while()---------------
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return mapList;
	}

	// 개인 월급 관리 - 수정 //
	@Override
	public EmployeeDTO search_employee_salary(Map<String, String> paraMap) {
		EmployeeDTO employee = new EmployeeDTO();

		try {
			String sql = " select emp_id, name, rank, salary " + " from tbl_employees " + " where emp_id = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("emp_id"));

			rs = pstmt.executeQuery();

			if (rs.next()) {

				employee.setEmp_id(rs.getInt("EMP_ID"));
				employee.setName(rs.getString("NAME"));
				employee.setRank(rs.getString("RANK"));
				employee.setSalary(rs.getInt("SALARY"));

			} // end of if()------------------------------------

		} catch (SQLException e) {

			if (e.getErrorCode() == 01722) {
				System.out.println("올바른 사원번호를 입력하세요 ");
			}
		} finally {
			close();
		}
		return employee;

	}

	// 개인 월급 관리 - 수정 //
	@Override
	public int update_salary(Map<String, String> paraMap) {
		int n = 0;
		try {
			String sql = " update tbl_employees set salary = ? " + " where emp_id = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("salary"));
			pstmt.setString(2, paraMap.get("emp_id"));

			n = pstmt.executeUpdate();
		} catch (SQLException e) {
			if(e.getErrorCode() == 1722) {
				System.out.println("변경할 급여를 올바르게 입력해주세요");
			}
		} finally {
			close();
		}

		return n;
	}

	// 전체 부서 정보 조회 메소드
	@Override
	public List<DeptDTO> deptList() {

		List<DeptDTO> deptList = new ArrayList<>();

		try {
			String sql = " with DPT as\n " + " (\n "
					+ "    select dept_id, dept_name, fk_dept_manager_id, name, ck_dept_status\n "
					+ "    from tbl_departments D JOIN tbl_employees E   \n"
					+ "    ON D.fk_dept_manager_id = E.emp_id\n " + " )\n " + " select dpt.dept_id as dept_id\n "
					+ "     , dpt.dept_name as dept_name\n " + "     , dpt.fk_dept_manager_id as fk_dept_manager_id\n "
					+ "     , dpt.name as name\n " + " from DPT " + " where ck_dept_status = 0 " + " order by dept_id ";

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				DeptDTO ddto = new DeptDTO();

				// 셀렉트 되어진 것을 DeptDTO에 담는데
				ddto.setDept_id(rs.getInt("DEPT_ID"));
				ddto.setDept_name(rs.getString("DEPT_NAME"));
				ddto.setFk_dept_manager_id(rs.getInt("FK_DEPT_MANAGER_ID"));

				// employee테이블이랑 조인 해와서 불러오기
				EmployeeDTO emp = new EmployeeDTO();
				emp.setName(rs.getString("NAME"));
				ddto.setEmp(emp);

				// set 해준 것들을 리스트에 담는다.
				deptList.add(ddto);

			} // end of while

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return deptList;
	} // end of public List<DeptDTO> deptList()

	// 부서 신설하는 메소드
	@Override
	public int dept_insert(DeptDTO ddto) {

		int result = 0;

		try {
			conn = ProjectDBConnection.getConn();

			String sql = " insert into tbl_departments(dept_id, dept_name, ck_dept_status, fk_dept_manager_id) "
					+ " values(SEQ_DEPT_ID.nextval, ?, 0, ?) ";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, ddto.getDept_name());
			pstmt.setInt(2, ddto.getFk_dept_manager_id());

			result = pstmt.executeUpdate(); // 행을 넣어주고 있다.

		} catch (SQLException e) {
			if (e.getErrorCode() == 2291) {
				System.out.println("해당 사원번호의 사원이 없습니다.");
				result = -1;
			}

		} finally { // 무조건 해주어야 한다.
			close(); // 자원 반납
		}

		return result;
	} // end of public int dept_insert(Dept_DTO ddto)

	// 부서 수정에서 부서번호를 select 해오는 메소드
	@Override
	public DeptDTO selectDept(int dept_id) {

		DeptDTO ddto = null; // 부서테이블에 부서가 없을 경우도 있다. 존재하지 않는 부서의 경우에는 null이 넘어간다.

		try {
			conn = ProjectDBConnection.getConn();

			String sql = " select dept_id, dept_name, ck_dept_status, fk_dept_manager_id " + " from tbl_departments "
					+ " where dept_id = ?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, dept_id);

			rs = pstmt.executeQuery();

			if (rs.next()) { // 1개의 값만 나오게 된다.
				ddto = new DeptDTO();
				ddto.setDept_id(rs.getInt("DEPT_ID"));
				ddto.setDept_name(rs.getString("DEPT_NAME"));
				ddto.setCk_dept_status(rs.getInt("CK_DEPT_STATUS"));
				ddto.setFk_dept_manager_id(rs.getInt("FK_DEPT_MANAGER_ID"));

			} // end of if

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return ddto;
	} // end of public DeptDTO view_department_id(int department_Id)

	// 부서 정보 수정하는 메소드
	@Override
	public int updateDept(DeptDTO ddto) {
		int result = 0;

		try {
			String sql = " update tbl_departments set dept_name = ?, fk_dept_manager_id = ? " + " where dept_id = ? ";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, ddto.getDept_name());
			pstmt.setInt(2, ddto.getFk_dept_manager_id());
			pstmt.setInt(3, ddto.getDept_id());

			result = pstmt.executeUpdate(); // 행을 넣어주고 있다.
			// update가 성공되어지면 result에는 1이 들어간다.

		} catch (SQLException e) {
			if (e.getErrorCode() == 2291) {
				System.out.println(ddto.getFk_dept_manager_id() + " 사원번호의 사원이 존재하지 않습니다.");
			}

			result = -1;
		} finally { // 무조건 해주어야 한다.
			close(); // 자원 반납
		}

		return result;

	} // end of public int updateDept(DeptDTO ddto)

	// 부서 폐쇄하는 메소드
	@Override
	public int deleteDept(int dept_id) {

		int result = 0;

		try {
			conn = ProjectDBConnection.getConn();

			String sql = " update tbl_departments set ck_dept_status = 1 " + " where dept_id = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, dept_id);

			result = pstmt.executeUpdate(); // 행을 넣어주고 있다.
			// update가 성공되어지면 result에는 1이 들어간다.

		} catch (SQLException e) {
			// e.printStackTrace();
			result = -1;
		} finally { // 무조건 해주어야 한다.
			close(); // 자원 반납
		}

		return result;
	} // end of public int deleteDept(DeptDTO ddto)

	// 사원 개인 정보 조회 메소드
	@Override
	public EmployeeDTO select_empInfo(EmployeeDTO employee) {

		try {

			String sql = "WITH EMP as \n" + "(\n" + "    select T.*,\n"
					+ "           to_char( trunc(months_between(T.retirement_day, T.hire_date)/12) * T.salary, '9,999,999')  AS retirement_salary \n"
					+ "    from -- T뷰 의 퇴직일 구하기\n" + "    (    \n" + "        select emp_id\n" + "             , RANK\n"
					+ "             , NAME\n" + "             , UQ_JUBUN\n" + "             , UQ_MOBILE\n"
					+ "             , UQ_EMAIL\n" + "             , ADDRESS\n"
					+ "             , salary * 12 as year_salary\n" + "             , salary\n"
					+ "             , REMAIN_ANNUALLEAVE\n" + "             , HIRE_DATE\n"
					+ "             , FK_MANAGER_ID\n" + "             , CK_MANAGER_RANK\n"
					+ "             , FK_DEPT_ID\n" + "             , age\n" + "             , gender    \n"
					+ "             , last_day( \n" + "                       to_date (\n"
					+ "                                to_char( add_months(sysdate, (63-AGE)*12), 'yyyy') || \n"
					+ "                                case when substr(UQ_JUBUN, 3, 2) between '03' and '08' then '-08-01' else '-02-01' end \n"
					+ "                                , 'yyyy-mm-dd') \n" + "                        )\n"
					+ "               AS RETIREMENT_DAY\n" + "        from -- V뷰 의 다른 select 가져오기       \n"
					+ "          (  \n" + "            select emp_id, RANK, NAME, UQ_JUBUN, UQ_MOBILE, UQ_EMAIL\n"
					+ "                 , ADDRESS, salary, REMAIN_ANNUALLEAVE, HIRE_DATE, FK_MANAGER_ID, CK_MANAGER_RANK, FK_DEPT_ID\n"
					+ "                 \n"
					+ "                 , case when substr(uq_jubun, 7, 1) in('1','3') then '남' else '여' end AS GENDER\n"
					+ "                   \n" + "                 , extract(year from sysdate) - \n"
					+ "                  (substr(uq_jubun, 1, 2) + case when substr(uq_jubun, 7, 1) in('1','2') then 1900 else 2000 end) + 1 AS AGE\n"
					+ "            \n" + "            from tbl_employees \n" + "            where salary is not null\n"
					+ "           ) V \n" + "    ) T \n" + "), \n" + "DEPT AS \n" + "(\n"
					+ "    select dept_id, dept_name, fk_dept_manager_id, name\n"
					+ "    from tbl_departments D JOIN tbl_employees E   \n"
					+ "    ON D.fk_dept_manager_id = E.emp_id\n" + ")\n" + "select emp.FK_DEPT_ID as FK_DEPT_ID\n"
					+ "     , dept.dept_name as dept_name\n" + "     , emp.rank as rank\n" + "     , emp.name as name\n"
					+ "     , emp.gender as gender\n" + "     , emp.UQ_JUBUN as UQ_JUBUN\n" + "     , emp.age as age\n"
					+ "     , emp.UQ_MOBILE as UQ_MOBILE\n" + "     , emp.UQ_EMAIL as UQ_EMAIL \n"
					+ "     , emp.ADDRESS as ADDRESS\n" + "     , to_char(emp.year_salary, '999,999') as year_salary\n"
					+ "     , emp.REMAIN_ANNUALLEAVE as REMAIN_ANNUALLEAVE\n" + "     , emp.HIRE_DATE as HIRE_DATE\n"
					+ "     , dept.name as dept_manager_name\n" + "     , emp.RETIREMENT_DAY as RETIREMENT_DAY\n"
					+ "     , emp.retirement_salary as retirement_salary \n" + "from EMP join DEPT\n"
					+ "ON EMP.FK_DEPT_ID = DEPT.dept_id\n" + "where emp_id = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, employee.getEmp_id());

			rs = pstmt.executeQuery(); // select 문이기 때문에 query

			if (rs.next()) { // 값이 있냐?

//			 employee = new EmployeeDTO(); // select 된 게 있으면 member 이걸 넘겨줘야 하기 때문에 객체 하나 새로 생성
				// 만일 값이 없다면 null이 member로 넘어간다.

				employee.setFk_dept_id(rs.getInt("FK_DEPT_ID"));

				DeptDTO ddto = new DeptDTO();
				ddto.setDept_name(rs.getString("DEPT_NAME"));
				employee.setDdto(ddto);

				employee.setRank(rs.getString("RANK"));
				employee.setName(rs.getString("NAME"));
				employee.setGender(rs.getString("GENDER"));
				employee.setUq_jubun(rs.getString("UQ_JUBUN"));
				employee.setAge(rs.getString("AGE"));
				employee.setUq_mobile(rs.getString("UQ_MOBILE"));
				employee.setUq_email(rs.getString("UQ_EMAIL"));
				employee.setAddress(rs.getString("ADDRESS"));
				employee.setYear_salary(rs.getString("YEAR_SALARY"));
				employee.setRemain_annualleave(rs.getInt("REMAIN_ANNUALLEAVE"));
				employee.setHire_date(rs.getString("HIRE_DATE"));
				employee.setDept_manager_name(rs.getString("DEPT_MANAGER_NAME"));
				employee.setRetirement_day(rs.getString("RETIREMENT_DAY"));
				employee.setRetirement_salary(rs.getString("RETIREMENT_SALARY"));

			}

		} catch (SQLException e) { // 제약 조건을 제외한 나머지 모든 sql 관련 에러는 다 여기에 오게 된다.
			e.printStackTrace();
		} finally {
			close(); // 메소드
		}
		System.out.println("확인용");
		return employee;

	} // end of public EmployeeDTO select_empInfo(int emp_id)

	// 사원 개인 정보 수정 메소드
	@Override
	public int update_emp_info(EmployeeDTO employee) {

		int result = 0;

		try {

			String sql = " update tbl_employees set passwd= ?, uq_email = ?, uq_mobile = ?, address = ? "
					+ " where emp_id = ? ";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, employee.getPasswd());
			pstmt.setString(2, employee.getUq_email());
			pstmt.setString(3, employee.getUq_mobile());
			pstmt.setString(4, employee.getAddress());
			pstmt.setInt(5, employee.getEmp_id());

			result = pstmt.executeUpdate(); // 행을 넣어주고 있다.
			// update가 성공되어지면 result에는 1이 들어간다.

		} catch (SQLException e) {

			if (e.getErrorCode() == 00001) {
				result = -1;
				System.out.println("[경고] 이메일과 휴대폰 번호는 중복이 불가합니다. \n");
			} else {
				result = -1;
				e.printStackTrace();
			}

		} finally { // 무조건 해주어야 한다.
			close(); // 자원 반납
		}

		return result;

	} // end of public int update_emp_info(EmployeeDTO employee, Scanner sc)
	
	
	
	

	// 신규 입사자 정보 추가하는 메소드(insert)
	@Override
	public int emp_insert_sql(EmployeeDTO employee) {

		int result = 0;

		try {

			String sql = "insert into tbl_employees(EMP_ID, PASSWD, NAME, UQ_EMAIL, UQ_MOBILE, HIRE_DATE, FK_DEPT_ID, \n"
					   + "            CK_RET_STATUS, SALARY, UQ_JUBUN, FK_MANAGER_ID, RANK, ADDRESS, REMAIN_ANNUALLEAVE, CK_MANAGER_RANK) \n"
				   	   + " values (SEQ_EMP_ID.nextval, ?, ?, ?, ?, ?, ?, 0, ?, ?, ?, ?, ?, 0, 3) ";

			pstmt = conn.prepareStatement(sql);

			// 여기서부터 set-get
			pstmt.setString(1, employee.getPasswd());
			pstmt.setString(2, employee.getName());
			pstmt.setString(3, employee.getUq_email());
			pstmt.setString(4, employee.getUq_mobile());
			pstmt.setString(5, employee.getHire_date());
			pstmt.setInt(6, employee.getFk_dept_id());
			pstmt.setInt(7, employee.getSalary());
			pstmt.setString(8, employee.getUq_jubun());
			pstmt.setInt(9, employee.getFk_manager_id());
			pstmt.setString(10, employee.getRank());
			pstmt.setString(11, employee.getAddress());

			result = pstmt.executeUpdate(); // 행을 넣어주고 있다.

		} catch (SQLException e) {
			e.printStackTrace();
		} finally { // 무조건 해주어야 한다.
			close(); // 자원 반납
		}

		return result;
	} // end of public int emp_insert_sql(EmployeeDTO employee)
	
	
	
	

	// 부서 번호 유무를 확인해주는 메소드 ★★★★★
	@Override
	public List<DeptDTO> dept_id_list() {

		List<DeptDTO> dept_id_list = new ArrayList<>();

		try {
			conn = ProjectDBConnection.getConn();

			String sql = " select dept_id " + " from tbl_departments " + " where ck_dept_status = 0 "; // ♤

			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				DeptDTO ddto = new DeptDTO();

				// 셀렉트 되어진 것을 boardDTO에 담는데
				ddto.setDept_id(rs.getInt("DEPT_ID"));

				// set 해준 것들을 리스트에 담는다.
				dept_id_list.add(ddto);

			} // end of while

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return dept_id_list;

	} // end of public List<DeptDTO> dept_id_list()

	// 사원 번호 유무를 확인해주는 메소드 ★★★★★
	@Override
	public List<EmployeeDTO> emp_id_list() {

		List<EmployeeDTO> emp_id_list = new ArrayList<>();

		try {
			conn = ProjectDBConnection.getConn();

			String sql = " select emp_id " + " from tbl_employees " + " where ck_ret_status = 0 "; // ♤

			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				EmployeeDTO employee = new EmployeeDTO();

				// 셀렉트 되어진 것을 boardDTO에 담는데
				employee.setEmp_id(rs.getInt("EMP_ID"));

				// set 해준 것들을 리스트에 담는다.
				emp_id_list.add(employee);

			} // end of while

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return emp_id_list;

	} // end of public List<EmployeeDTO> emp_id_list()

	// 사원번호로 조회하기
	@Override
	public EmployeeDTO emp_id(Map<String, String> paraMap) {

		EmployeeDTO edto = null;

		try {

			String sql = " select emp_id, name, rank, uq_email, uq_mobile, to_char(hire_date,'yyyy-mm-dd') as hire_date, dept_name "
					+ " from tbl_employees E join tbl_departments D " + " on E.FK_DEPT_ID = D.DEPT_ID "
					+ " where emp_id = ? ";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, paraMap.get("emp_id"));

			rs = pstmt.executeQuery();

			if (rs.next()) {

				edto = new EmployeeDTO();

				edto.setEmp_id(rs.getInt("EMP_ID"));
				edto.setName(rs.getString("NAME"));
				edto.setRank(rs.getString("RANK"));
				edto.setUq_email(rs.getString("UQ_EMAIL"));
				edto.setUq_mobile(rs.getString("UQ_MOBILE"));
				edto.setHire_date(rs.getString("HIRE_DATE"));
				edto.setDept_name(rs.getString("Dept_name"));

			} // end of if(rs.next())--------------------------------------------------------

		} catch (SQLException e) {
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return edto;

	}// end of public BoardDTO viewContents(String
		// boardno)--------------------------------

	// 이메일, 주민등록번호, 휴대폰번호 중복 여부 검사하는 메소드 ♤ ★★★★★
	@Override
	public List<EmployeeDTO> emp_overlap_list() {

		List<EmployeeDTO> emp_overlap_list = new ArrayList<>();

		try {
			conn = ProjectDBConnection.getConn();

			String sql = " select uq_email, uq_mobile, uq_jubun " + " from tbl_employees "
					   + " where ck_ret_status = 0 ";

			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				EmployeeDTO employee = new EmployeeDTO();

				// 셀렉트 되어진 것을 boardDTO에 담는데
				employee.setUq_email(rs.getString("UQ_EMAIL"));
				employee.setUq_mobile(rs.getString("UQ_MOBILE"));
				employee.setUq_jubun(rs.getString("UQ_JUBUN"));

				// set 해준 것들을 리스트에 담는다.
				emp_overlap_list.add(employee);

			} // end of while

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return emp_overlap_list;

	} // end of public List<EmployeeDTO> emp_email_list()

	// 사원명으로 조회하기
	@Override
	public EmployeeDTO emp_name(Map<String, String> paraMap) {

		EmployeeDTO edto = null;

		try {
			conn = ProjectDBConnection.getConn();

			String sql = " select emp_id, name, rank, uq_email, uq_mobile, to_char(hire_date,'yyyy-mm-dd') as hire_date, dept_name "
					+ " from tbl_employees E join tbl_departments D " + " on E.FK_DEPT_ID = D.DEPT_ID "
					+ " where name = ? ";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, paraMap.get("name"));

			rs = pstmt.executeQuery();

			if (rs.next()) {

				edto = new EmployeeDTO();

				edto.setEmp_id(rs.getInt("EMP_ID"));
				edto.setName(rs.getString("NAME"));
				edto.setRank(rs.getString("RANK"));
				edto.setUq_email(rs.getString("UQ_EMAIL"));
				edto.setUq_mobile(rs.getString("UQ_MOBILE"));
				edto.setHire_date(rs.getString("HIRE_DATE"));
				edto.setDept_name(rs.getString("Dept_name"));
			} // end of if(rs.next())--------------------------------------------------------
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return edto;

	}// end of public BoardDTO viewContents(String
		// boardno)--------------------------------

	// 부서명으로 조회하기
	@Override
	public List<EmployeeDTO> fk_dept_id(String dept_name) {

		List<EmployeeDTO> fk_dept_id = new ArrayList<EmployeeDTO>();

		try {
			conn = ProjectDBConnection.getConn();

			String sql = " select emp_id, name, rank, uq_email, uq_mobile, to_char(hire_date,'yyyy-mm-dd') as hire_date, dept_name "
					+ " from tbl_employees E join tbl_departments D " + " on E.FK_DEPT_ID = D.DEPT_ID "
					+ " where dept_name = ? ";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, dept_name);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				EmployeeDTO edto = new EmployeeDTO();

				edto.setEmp_id(rs.getInt("EMP_ID"));
				edto.setName(rs.getString("NAME"));
				edto.setRank(rs.getString("RANK"));
				edto.setUq_email(rs.getString("UQ_EMAIL"));
				edto.setUq_mobile(rs.getString("UQ_MOBILE"));
				edto.setHire_date(rs.getString("HIRE_DATE"));
				edto.setDept_name(rs.getString("Dept_name"));

				fk_dept_id.add(edto);

			} // end of if(rs.next())--------------------------------------------------------

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return fk_dept_id;
	}// end of public List<EmployeeDTO> fk_dept_id(String
		// dept_name)()-------------------------------

	@Override
	// 지각시 tbl_commutetime 테이블 absence 컬럼에 1을 넣어주는 메소드
	public List<CommuteDTO> c_status(CommuteDTO cmmdto, int fk_writer_emp_id) {
		List<CommuteDTO> cmtdto = new ArrayList<>();

		try {
			String sql = "";
			sql = " update tbl_commutetime set absence = 1 "
					+ " where fk_writer_emp_id = ? and working_time = sysdate ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, fk_writer_emp_id);

			int n = pstmt.executeUpdate();

			if (n == 1) {
				cmtdto.add(cmmdto);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return cmtdto;
	} // 지각시 tbl_commutetime 테이블 absence 컬럼에 +1 해주는 메소드

	// 출근시간 입력 메소드 //
	@Override
	public List<CommuteDTO> commute_start(CommuteDTO cmmdto) {

		List<CommuteDTO> cmtdto = new ArrayList<>();

		int result = 0;

		try {
			// Transaction 처리를 위해서 수동 commit 으로 전환 시킨다.

			String sql = " insert into tbl_commutetime(commute_id, fk_writer_emp_id, working_time) "
					+ " values( seq_commute_id.nextval, ?, sysdate) ";

			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, cmmdto.getFk_writer_emp_id());
			result = pstmt.executeUpdate();
			cmtdto.add(cmmdto);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();// 자원 반납하기
		} // end of try catch finally-------------------------------------------------

		return cmtdto;

	}// end of commute_start(CommuteDTO cmmdto)----------------------------

	// 출퇴근시간 전체조회 //
	@Override
	public List<CommuteDTO> view_commute_list() {

		List<CommuteDTO> view_commute_list = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		
		System.out.println("\n---------------------[공지사항 목록]---------------------");
		System.out.println("기본번호\t사원번호\t출근시간\t\t퇴근시간\t\t지각여부");
		System.out.println("-------------------------------------------------------");

		try {
			String sql = " select commute_id, fk_writer_emp_id, to_char(working_time, 'yyyy-mm-dd hh24:mi:ss') as work_time, to_char(working_time, 'yyyy-mm-dd hh24:mi:ss') as leave_time, absence\n "
					+ " from tbl_commutetime \n" + " order by 1";

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				CommuteDTO cdto = new CommuteDTO();

//				cdto.setCommute_id(rs.getInt("COMMUTE_ID"));
//				cdto.setFk_writer_emp_id(rs.getInt("FK_WRITER_EMP_ID"));
//				cdto.setWorking_time(rs.getString("WORK_TIME"));
//				cdto.setLeave_time(rs.getString("LEAVE_TIME"));
				
				sb.append(rs.getInt("COMMUTE_ID")+"\t");
				sb.append(rs.getInt("FK_WRITER_EMP_ID")+"\t");
				sb.append(rs.getString("WORK_TIME")+"\t");
				sb.append(rs.getString("LEAVE_TIME")+"\t");
				if(rs.getInt("ABSENCE") == 1) {
					sb.append("지각\t\n");
				}else {
					sb.append("정상출근\t\n");
				}
//				cdto.setAbsence(rs.getInt("ABSENCE"));
				view_commute_list.add(cdto);
			} // end of while
			System.out.println(sb.toString());

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return view_commute_list;
	}

	@Override
	// 재직증명서 발급할 사원의 정보 입력 (insert) 및 select
	public List<CertDTO> insert_cert1(CertDTO cdto) {
		List<CertDTO> insert_cert1 = new ArrayList<>();

		try {
			conn = ProjectDBConnection.getConn();
			conn.setAutoCommit(false);

			String sql = "insert into tbl_certificate(seq_certificate_id, certificate_type, fk_writer_emp_id, use, issue_date, retirement_date, work_date)\n"
					+ "	values(seq_certificate_id.nextval, '재직증명서', ? , ?, sysdate, '2022-07-31', 0) ";
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, cdto.getFk_writer_emp_id());
			pstmt.setString(2, cdto.getUse());

			int n = pstmt.executeUpdate();

			if (n == 1)
				insert_cert1.add(cdto);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return insert_cert1;

	}// end of inser_cert1 method.

	// 출퇴근시간 검색조회 //
	@Override //
	public List<CommuteDTO> view_commute_list(String emp_id) {
		List<CommuteDTO> view_commute_list = new ArrayList<>();
		try {
			String sql = " select commute_id, fk_writer_emp_id, to_char(working_time, 'yyyy-mm-dd hh24:mi:ss') as work_time, to_char(working_time, 'yyyy-mm-dd hh24:mi:ss') as leave_time, absence\n "
					+ " from tbl_commutetime \n" + " where fk_writer_emp_id = ? " + " order by 1";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, emp_id);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				CommuteDTO cdto = new CommuteDTO();

				cdto.setCommute_id(rs.getInt("COMMUTE_ID"));
				cdto.setFk_writer_emp_id(rs.getInt("FK_WRITER_EMP_ID"));
				cdto.setWorking_time(rs.getString("WORK_TIME"));
				cdto.setLeave_time(rs.getString("LEAVE_TIME"));
				cdto.setAbsence(rs.getInt("ABSENCE"));
				view_commute_list.add(cdto);
			} // end of while

		} catch (SQLException e) {
			System.out.println(emp_id + " 는 없는 사원번호입니다.");
		}

		finally {
			close();
		}
		return view_commute_list;
	}

	@Override
	public List<CertDTO> certList(int emp_id, String use) {
		List<CertDTO> certList = new ArrayList<>();

		try {
			conn = ProjectDBConnection.getConn();

			String sql = "select seq_certificate_id, fk_writer_emp_id, rank, name, uq_jubun, use, hire_date, issue_date\n"
					+ "from tbl_employees E join tbl_certificate C\n" + "on E.emp_id = C.fk_writer_emp_id \n"
					+ "where C.fk_writer_emp_id = ? and use = ?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, emp_id);
			pstmt.setString(2, use);

			rs = pstmt.executeQuery();

			if (rs.next()) {

				CertDTO cdto = new CertDTO();
				cdto.setSeq_certificate_id(rs.getInt("SEQ_CERTIFICATE_ID"));
				cdto.setFk_writer_emp_id(rs.getInt("fk_writer_emp_id"));
				cdto.setIssue_date(rs.getString("ISSUE_DATE"));
				cdto.setUse(rs.getString("USE"));

				EmployeeDTO employee = new EmployeeDTO();
				employee.setName(rs.getString("NAME"));
				employee.setRank(rs.getString("RANK"));
				employee.setUq_jubun(rs.getString("UQ_JUBUN"));
				employee.setHire_date(rs.getString("HIRE_DATE"));

				cdto.edto1(employee);
				certList.add(cdto);

			} // end of if-------------------------------------------

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return certList;

	}

	// 경력증명서 발급할 사원의 정보 입력 (insert) 및 select
	@Override
	public List<CertDTO> insert_cert2(CertDTO cdto) {
		List<CertDTO> insert_cert1 = new ArrayList<>();

		try {
			conn = ProjectDBConnection.getConn();
			conn.setAutoCommit(false);

			String sql = "insert into tbl_certificate(seq_certificate_id, certificate_type, fk_writer_emp_id, use, issue_date, retirement_date, work_date)\n"
					+ "	values(seq_certificate_id.nextval, '경력증명서', ? , ?, sysdate, sysdate , 0) ";
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, cdto.getFk_writer_emp_id());
			pstmt.setString(2, cdto.getUse());

			int n = pstmt.executeUpdate();

			if (n == 1)
				insert_cert1.add(cdto);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return insert_cert1;

	}// end of inser_cert1 method()-------------------------------

	// 경력증명서 출력 //
	@Override
	public List<CertDTO> certList2(int emp_id, String use) {
		List<CertDTO> certList = new ArrayList<>();

		try {
			conn = ProjectDBConnection.getConn();

			String sql = "select seq_certificate_id, fk_writer_emp_id, rank, name, uq_jubun, use, hire_date, retirement_date, issue_date\n"
					+ "from tbl_employees E join tbl_certificate C\n" + "on E.emp_id = C.fk_writer_emp_id \n"
					+ "where C.fk_writer_emp_id = ? and use = ?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, emp_id);
			pstmt.setString(2, use);

			rs = pstmt.executeQuery();

			if (rs.next()) {

				CertDTO cdto = new CertDTO();
				cdto.setSeq_certificate_id(rs.getInt("SEQ_CERTIFICATE_ID"));
				cdto.setFk_writer_emp_id(rs.getInt("fk_writer_emp_id"));
				cdto.setIssue_date(rs.getString("ISSUE_DATE"));
				cdto.setUse(rs.getString("USE"));
				cdto.setRetirement_date(rs.getString("RETIREMENT_DATE"));

				EmployeeDTO employee = new EmployeeDTO();
				employee.setName(rs.getString("NAME"));
				employee.setRank(rs.getString("RANK"));
				employee.setUq_jubun(rs.getString("UQ_JUBUN"));
				employee.setHire_date(rs.getString("HIRE_DATE"));

				cdto.edto1(employee);
				certList.add(cdto);

			} // end of if-------------------------------------------

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return certList;

	}// end of public List<CertDTO> certList2(int emp_id, String use)
		// ------------------------

	// 경력증명서 출력 yes or no //
	@Override
	public void yn_y(String yn) {
		try {
			if ("y".equalsIgnoreCase(yn)) {
				System.out.println("출력을 성공하였습니다.");
				conn.commit();
			} else if ("n".equalsIgnoreCase(yn)) {
				System.out.println("출력을 취소하였습니다.");
				conn.rollback();
			}
			conn.setAutoCommit(true);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}// end of public void yn_y(String yn)-------------------------------------------

	// 부서명 넣으면 있는 부서인지 체크해주는 메소드 ($$$)
	@Override
	public int dept_name_check(String dept_name) {
		int result = 0;
		try {

			String sql = " select * from tbl_departments " + " where dept_name = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dept_name);

			rs = pstmt.executeQuery();

			if (rs.next()) {// 하나라도 있다면

				result = 1;

			} // end of while()------------------------------------

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return result;

	}

	// 면접 일정 추가 //
	@Override
	public int management_inerview_insert(Map<String, String> paraMap) {

		int result = 0;
		try {
			String sql = "  select Dept_id from tbl_departments " + " where dept_name = ? ";

			pstmt = conn.prepareStatement(sql); // SQL문 전달할 객체 생성
			pstmt.setString(1, paraMap.get("dept_name"));

			rs = pstmt.executeQuery();
			rs.next();

			// insert
			sql = " insert into TBL_INTERVIEW(APLLICANT_ID, FK_DEPT_ID, DUTY, APPLICANT_NAME,APPLICANT_MOBILE,INTERVIEW_DATE ) "
					+ "                    values(SEQ_APLLICANT_ID.nextval, ? ,? ,? , ? ,?) ";

			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, rs.getInt("dept_id"));
			pstmt.setString(2, paraMap.get("duty"));
			pstmt.setString(3, paraMap.get("applicant_name"));
			pstmt.setString(4, paraMap.get("applicant_mobile"));
			pstmt.setString(5, paraMap.get("interview_date"));

			result = pstmt.executeUpdate();// 0 or 1

		} catch (SQLException e) {
			result = -1;
			System.out.println("SQL 구문 에러 발생");
		} finally {
			close();// 자원 반납하기

		} // end of try catch finally-------------------------------------------------
		return result;
	}

	
	@Override
	public int management_inerview_view() { // 면접내역 조회
		int result = 0;

		try {
			String sql = " select I.apllicant_id, D.dept_name, I.duty, I.applicant_name, i.applicant_mobile, to_char(I.interview_date,'yyyy-mm-dd')as interview_date, E.name "
					+ " from tbl_interview I join tbl_departments D " + " on I.FK_dept_id = D.dept_id "
					+ " join tbl_employees E " + " on D.FK_dept_manager_id = E.emp_id " + " order by 1 desc";

			pstmt = conn.prepareStatement(sql); // SQL문 전달할 객체 생성
			rs = pstmt.executeQuery(); // select 되어진 실행결과를 ResultSet 객체에 저장

			StringBuilder sb = new StringBuilder();
			int cnt = 0;
			while (rs.next()) { // select 되어진 결과가 있을 경우 실행 결과값은 무조건 1행이므로 while 대신 if 사용
				cnt++;
				if (cnt == 1) {
					System.out.println(
							"----------------------------------------------------------------------");
					System.out.println("면접번호\t부서명\t직무\t면접자명\t면접자휴대폰번호\t면접일\t\t면접관명\t");
					System.out.println(
							"----------------------------------------------------------------------");
				}
				sb.append(Integer.toString(rs.getInt("apllicant_id")) + "\t" + rs.getString("dept_name") + "\t"
						+ rs.getString("duty") + "\t" + rs.getString("applicant_name") + "\t"
						+ rs.getString("applicant_mobile") + "\t" + rs.getString("interview_date") + "\t"
						+ rs.getString("name") + "\n");
			} // end of while

			if (cnt > 0) {
				result = 1;
				System.out.println(sb.toString());
			}
		} catch (SQLSyntaxErrorException e) {
			System.out.println("올바른 아이디, 암호를 입력하세요");
		} catch (SQLException e) {
			result = -1;
			e.printStackTrace();
		} finally {
			close();
		}
		return result;
	}

	// 면접 일정 변경($$$)
	@Override
	public int management_inerview_update(Scanner sc) {
		int result = 0;
		String apllicant_id = "";
		String menu_no = "";
		String where = "";
		String insert = "";
		try {

			System.out.print("▷ 수정하실 면접일정의 면접번호를 입력해주세요 : ");
			apllicant_id = sc.nextLine();

			String sql = " select apllicant_id, duty,applicant_name, APPLICANT_MOBILE, to_char(interview_date,'yyyy-mm-dd')as interview_date,FK_dept_id "
					+ " from tbl_interview " + " where apllicant_id = ? ";

			pstmt = conn.prepareStatement(sql); // SQL문 전달할 객체 생성
			pstmt.setString(1, apllicant_id);

			rs = pstmt.executeQuery(); // select 되어진 실행결과를 ResultSet 객체에 저장
			rs.next();

			Map<String, String> paraMap = new HashMap<>();
			
			paraMap.put("apllicant_id", rs.getString("apllicant_id"));
			paraMap.put("duty", rs.getString("duty"));
			paraMap.put("applicant_name", rs.getString("applicant_name"));
			paraMap.put("APPLICANT_MOBILE", rs.getString("APPLICANT_MOBILE"));
			paraMap.put("interview_date", rs.getString("interview_date"));
			paraMap.put("FK_dept_id", rs.getString("FK_dept_id"));

			if (paraMap.size() != 0) {
				System.out.println("---------------------------------------------");
				System.out.println("면접번호\t면접자명\t직무\t면접자휴대폰번호\t면접일\t부서번호");
				System.out.println(apllicant_id + "\t" + paraMap.get("applicant_name") + "\t" + paraMap.get("duty")
						+ "\t" + paraMap.get("APPLICANT_MOBILE") + "\t" + paraMap.get("interview_date") + "\t"
						+ paraMap.get("FK_dept_id") + "\n");
				System.out.println("----------------------------------------------");

				do {
					System.out.println("======  면접일정 변경  =====");
					System.out.println("1.면접자명    2.직무    \n3.휴대폰번호    4.면접일    \n5.부서번호    6.뒤로가기");
					System.out.println("=========================");

					System.out.print("▷ 변경하실 정보를 선택해주세요 : ");
					menu_no = sc.nextLine();
					
					switch (menu_no) {
					case "1":// 면접자명 변경
						int n = change_name(paraMap,sc);
							if(n == 1){
								System.out.println("정보 변경이 완료되었습니다.");
							}else {
								System.out.println("정보 변경에 실패하였습니다.");
							}
						break;
					case "2":// 직무 변경
						change_duty(paraMap,sc);
						break;
					case "3":// 휴대폰번호 변경
						change_mobile(paraMap,sc);
						break;
					case "4":// 면접일 변경 
						change_date(paraMap,sc);
						break;
					case "5": // 부서번호 변경
						change_dept_id(paraMap,sc);
						break;
					case "6": // 뒤로가기
						break;

					default:
						System.out.println("[경고] 정수로만 입력해야합니다.");
						break;
					}
					
				}while(!"6".equals(menu_no));
			}//----
		}//----

		 catch (SQLException e) {
			if( e.getErrorCode() == 01722) {
				System.out.println("잘못된 값 입력!");
				result = -1;
			}
//			e.printStackTrace();
		} finally {
			close();
		}
		return result;
	}
	
	// 면접자 이름 변경 (완) // 
	private int change_name(Map<String, String> paraMap, Scanner sc) {
		int result = 0;
		String name = "";
		String sql = "";
		
		try {
			System.out.println("현재 : " + paraMap.get("applicant_name"));
			System.out.println("변경값을 입력해주세요 :");
			name = sc.nextLine();
			
			sql = " update tbl_interview set applicant_name  = ?   "
				+ " where apllicant_id = ? ";
			
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2, paraMap.get("apllicant_id"));
			
			result = pstmt.executeUpdate();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}//end of private int change_name(Map<String, String> paraMap, Scanner sc)------------
	
	
	// 면접자 직무 변경 // 완
	private int change_duty(Map<String, String> paraMap, Scanner sc) {
		int result = 0;
		String duty = "";
		String sql = "";
		
		try {
			System.out.println("현재 : " + paraMap.get("duty"));
			System.out.println("변경값을 입력해주세요 :");
			duty = sc.nextLine();
			
			sql = " update tbl_interview set duty  = ?   "
				+ " where apllicant_id = ? ";
			
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, duty);
			pstmt.setString(2, paraMap.get("apllicant_id"));
			
			result = pstmt.executeUpdate();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}// end of private int change_duty(Map<String, String> paraMap, Scanner sc)------------
	
	
	
	// 면접자 연락처 변경 (완) //
	private int change_mobile(Map<String, String> paraMap, Scanner sc) {
		int result = 0;
		String APPLICANT_MOBILE = "";
		String sql = "";
		
		try {
			System.out.println("현재 : " + paraMap.get("APPLICANT_MOBILE"));
			System.out.println("변경값을 입력해주세요 :");
			APPLICANT_MOBILE = sc.nextLine();
			
			sql = " update tbl_interview set APPLICANT_MOBILE  = ?   "
				+ " where apllicant_id = ? ";
			
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, APPLICANT_MOBILE);
			pstmt.setString(2, paraMap.get("apllicant_id"));
			
			result = pstmt.executeUpdate();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}// end of private int change_mobile(Map<String, String> paraMap, Scanner sc)------------
	
	
	
	
	// 면접 일자 변경  (완) //
	private int change_date(Map<String, String> paraMap, Scanner sc) {
		int result = 0;
		String interview_date = "";
		String sql = "";
		
		try {
			System.out.println("현재 : " + paraMap.get("interview_date"));
			System.out.println("변경값을 입력해주세요 :");
			interview_date = sc.nextLine();
			
			sql = " update tbl_interview set interview_date  = ?   "
				+ " where apllicant_id = ? ";
			
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, interview_date);
			pstmt.setString(2, paraMap.get("apllicant_id"));
			
			result = pstmt.executeUpdate();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}// end of private int change_date(Map<String, String> paraMap, Scanner sc)--------------
	
	
	// 면접 부서 변경  (완) //
	private int change_dept_id(Map<String, String> paraMap, Scanner sc) {
		int result = 0;
		String FK_dept_id = "";
		String sql = "";
		
		try {
			System.out.println("현재 : " + paraMap.get("applicant_name"));
			System.out.println("변경값을 입력해주세요 :");
			FK_dept_id = sc.nextLine();
			
			sql = " update tbl_interview set applicant_name  = ?   "
				+ " where apllicant_id = ? ";
			
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, FK_dept_id);
			pstmt.setString(2, paraMap.get("apllicant_id"));
			
			result = pstmt.executeUpdate();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}// end of private int change_dept_id(Map<String, String> paraMap, Scanner sc)--------------
	

	// 사원 전체조회
	@Override
	public void management_empinfo_allview(Scanner sc) {
		try {

			String sql = " select emp_id, dept_name, rank, name "
					+ " ,case when substr(uq_jubun, 7, 1) in ('1','3') then '남' else '여' end AS GENDER                "
					+ " ,extract(year from sysdate) - ( to_number( substr(uq_jubun, 1, 2) ) + case when substr(uq_jubun, 7, 1)"
					+ " in('1','2') then 1900 else 2000 end ) + 1 AS age "
					+ " ,UQ_MOBILE , UQ_EMAIL,  address ,  hire_date, FK_MANAGER_ID "
					+ " from tbl_employees E left join tbl_departments D " + " on E.FK_DEPT_ID = D.DEPT_ID "
					+ " where CK_RET_STATUS = 0 and emp_id > 1000 " + " order by 1 ";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			StringBuilder sb = new StringBuilder();
			int cnt = 0;
			while (rs.next()) {
				cnt++;
				if (cnt == 1) {
					System.out.println(
							"---------------------------------------------------------------------------------------------------------------------------------");
					System.out.println("직원번호\t부서\t직급\t이름\t성별\t나이\t연락처\t\t이메일주소\t\t\t주소\t\t입사일자\t\t\t직속상관번호");
					System.out.println(
							"---------------------------------------------------------------------------------------------------------------------------------");
				}
				sb.append(Integer.toString(rs.getInt("emp_id")) + "\t" + rs.getString("dept_name") + "\t"
						+ rs.getString("rank") + "\t" + rs.getString("name") + "\t" + rs.getString("gender") + "\t"
						+ rs.getString("age") + "\t" + rs.getString("UQ_MOBILE") + "\t" + rs.getString("UQ_EMAIL")
						+ "\t\t" + rs.getString("address") + "\t" + rs.getString("hire_date") + "\t"
						+ Integer.toString(rs.getInt("FK_MANAGER_ID")) + "\n");

			}
			System.out.println(sb.toString());
		} catch (SQLSyntaxErrorException e) {
			e.getStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}// end of public void management_empinfo_allview()

	// === 연차신청을 해주는 메소드 ($$$$$$)=== //
	@Override
	public int YsinCheong(Map<String, String> paraMap, Scanner sc) {
		int result = 0;
		String sql = null;
		try {

			System.out.print(">> 정말 연차신청을 하시겠습니까? [Y/N] => ");
			int n = Util.yn(sc);
			if (n != 1) {
				result = -1; // yn n 선택 연차취소
			} else { // Y 연차 신청
				sql = " insert into tbl_annual(VACATION_ID,FK_WRITER_EMP_ID,START_DATE,END_DATE,CK_APPROVAL,APPROVAL_DATE ) "
						+ " values (SEQ_VACATION_ID.nextval,?, ?, ?, 1 , sysdate) ";

				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, paraMap.get("Emp_id"));
				pstmt.setString(2, paraMap.get("start_date"));
				pstmt.setString(3, paraMap.get("end_date"));

				result = pstmt.executeUpdate(); // 여기서 result = 0 or 1
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return result;
	}

	// 연차 조회할 때 검색 부분
	@Override
	public List<AnnualDTO> Ychacheck(String choiceno, Scanner sc) {
		List<AnnualDTO> yeonchaList = new ArrayList<>();
		
		switch (choiceno) {
		case "1":
			break;
			
		case "2":
			break;
			
		case "3":
			break;
		default:
			System.out.println("1 또는 2 또는 3 의 값만 입력해 주세요.");
			break;	
			
		}
		
		
		try {
			if(choiceno.equals("1") ) {
				System.out.print(">> 검색할 연차번호 입력: ");
				String search = sc.nextLine();
				
				conn = ProjectDBConnection.getConn();
				
				String sql = " select  Y.VACATION_ID as 연차번호, E.EMP_id as 사원번호, E.NAME as 사원명,D.DEPT_NAME as 부서명 "+
						"       ,to_char(Y.START_DATE, 'yyyy-mm-dd') as 연차시작일자, to_char(Y.END_DATE,'yyyy-mm-dd') as 연차종료일자 "+
						"       ,decode(Y.CK_APPROVAL , 0 , '반려', 1, 'admin 결재대기', 2,'최종결재완료','') as 결재진행상황 "+
						"       ,to_char(Y.APPROVAL_DATE,'yyyy-mm-dd') as 승인일자 "+
						" from tbl_annual Y  "+
						" left join tbl_employees E "+
						" on E.EMP_ID = Y.FK_WRITER_EMP_ID "+
						" JOIN tbl_departments D "+
						" on E.FK_DEPT_ID = D.DEPT_ID"+
						" where VACATION_ID  = ? " + 
						" order by 1 desc ";			
				 pstmt = conn.prepareStatement(sql);
				 pstmt.setString(1,search);
			
				 rs = pstmt.executeQuery();
				}
			else if(choiceno.equals("2") ) {
				System.out.print(">> 검색할 사원번호 입력 : ");
				String search = sc.nextLine();
				
				conn = ProjectDBConnection.getConn();
				
				String sql = " select  Y.VACATION_ID as 연차번호, E.EMP_id as 사원번호, E.NAME as 사원명,D.DEPT_NAME as 부서명 "+
						"       ,to_char(Y.START_DATE, 'yyyy-mm-dd') as 연차시작일자, to_char(Y.END_DATE, 'yyyy-mm-dd') as 연차종료일자 "+
						"       ,decode(Y.CK_APPROVAL , 0 , '반려', 1, 'admin 결재대기', 2,'최종결재완료','') as 결재진행상황 "+
						"       ,to_char(Y.APPROVAL_DATE, 'yyyy-mm-dd') as 승인일자 "+
						" from tbl_annual Y  "+
						" left join tbl_employees E "+
						" on E.EMP_ID = Y.FK_WRITER_EMP_ID "+
						" JOIN tbl_departments D "+
						" on E.FK_DEPT_ID = D.DEPT_ID "+
						" where emp_id = ? " +
						" order by 1 desc ";			
				 pstmt = conn.prepareStatement(sql);
				 pstmt.setString(1,search);
			
				 rs = pstmt.executeQuery();
				}
			else {
				System.out.print(">> 검색할 부서명 입력 : ");
				String search = sc.nextLine();
				conn = ProjectDBConnection.getConn();
				
				String sql = " select  Y.VACATION_ID as 연차번호, E.EMP_id as 사원번호, E.NAME as 사원명,D.DEPT_NAME as 부서명 "+
		                  "       ,to_char(Y.START_DATE , 'yyyy-mm-dd') as 연차시작일자 , to_char(Y.END_DATE,'yyyy-mm-dd') as 연차종료일자 "+
		                  "       ,decode(Y.CK_APPROVAL , 0 , '반려', 1, 'admin 결재대기', 2,'최종결재완료','') as 결재진행상황 "+
		                  "       ,to_char(Y.APPROVAL_DATE, 'yyyy-mm-dd') as 승인일자 "+
		                  " from tbl_annual Y  "+
		                  " left join tbl_employees E "+
		                  " on E.EMP_ID = Y.FK_WRITER_EMP_ID "+
		                  " JOIN tbl_departments D "+
		                  " on E.FK_DEPT_ID = D.DEPT_ID "+
		                  " where dept_name = ? " +
		                  " order by 1 desc ";                     		
				 pstmt = conn.prepareStatement(sql);
				 pstmt.setString(1,search);
					
				 rs = pstmt.executeQuery();
			}
			 while(rs.next()) {			 
				 AnnualDTO ydto = new AnnualDTO();

				 ydto.setVacation_id(rs.getInt("연차번호"));
				 ydto.setFk_writer_emp_id(rs.getInt("사원번호"));
				 ydto.setStart_date(rs.getString("연차시작일자"));
				 ydto.setEnd_date(rs.getString("연차종료일자"));
				 ydto.setCk_approval(rs.getString("결재진행상황"));
				 ydto.setApproval_date(rs.getString("승인일자"));
				 
				 EmployeeDTO edto = new EmployeeDTO(); 
				 edto.setName( rs.getString("사원명") );
				 
				 DeptDTO ddto = new  DeptDTO();
				 ddto.setDept_name( rs.getString("부서명") );
				 
				 ydto.setEdto(edto);
				 ydto.setDdto(ddto);

				 yeonchaList.add(ydto);
			 }

		} catch (SQLException e) {
			if(e.getErrorCode() == 907) {
//				System.out.println("[경고] 올바른 부서명을 입력하세요.");
			}
//			e.printStackTrace();
		} finally {
			close();
		}
		
		return yeonchaList;
	}// end of public List<AnnualDTO> Ychacheck(String choiceno, Scanner sc)------------

	// 연차 조회할 때 전체 조회
	@Override
	public List<AnnualDTO> Ychacheck() {
		List<AnnualDTO> yeonchaList = new ArrayList<>();
		
		try {
			conn = ProjectDBConnection.getConn();
			
			String sql = "select  Y.VACATION_ID as 연차번호, E.EMP_id as 사원번호, E.NAME as 사원명,D.DEPT_NAME as 부서명 "+
					"       ,to_char(Y.START_DATE, 'yyyy-mm-dd') as 연차시작일자, to_char(Y.END_DATE, 'yyyy-mm-dd') as 연차종료일자 "+
					"       ,decode(Y.CK_APPROVAL , 0 , '반려', 1, 'admin 결재대기', 2,'최종결재완료','') as 결재진행상황 "+
					"       ,to_char(Y.APPROVAL_DATE, 'yyyy-mm-dd') as 승인일자 "+
					" from tbl_annual Y "+
					" left join tbl_employees E "+
					" on E.EMP_ID = Y.FK_WRITER_EMP_ID "+
					" JOIN tbl_departments D "+
					" on E.FK_DEPT_ID = D.DEPT_ID "+
					" order by 1 desc ";
			 
			 pstmt = conn.prepareStatement(sql);
			 
			 rs = pstmt.executeQuery();
			 
			 while(rs.next()) {			 
				 AnnualDTO ydto = new AnnualDTO();
				 
				 ydto.setVacation_id(rs.getInt("연차번호"));
				 ydto.setFk_writer_emp_id(rs.getInt("사원번호"));
				 ydto.setStart_date(rs.getString("연차시작일자"));
				 ydto.setEnd_date(rs.getString("연차종료일자"));
				 ydto.setCk_approval(rs.getString("결재진행상황"));
				 ydto.setApproval_date(rs.getString("승인일자"));
				 
				 EmployeeDTO edto = new EmployeeDTO(); 
				 edto.setName( rs.getString("사원명") );
				 
				 DeptDTO ddto = new  DeptDTO();
				 ddto.setDept_name( rs.getString("부서명") );
				 
				 ydto.setEdto(edto);
				 ydto.setDdto(ddto);

				 yeonchaList.add(ydto);
			 }
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return yeonchaList;
	}// end of public List<AnnualDTO> Ychacheck()

	@Override
	public int Docinsert(EmployeeDTO member, Scanner sc) {
		String doc_id = "";
		String doc_title = "";
		String doc_contents = "";
		int result = 0;
		int n = 0;
		String level_id = "";
		String FK_Decision_Emp_Id = "";

		System.out.println("기안자 사원번호 : " + member.getEmp_id());
		System.out.println("문서 제목 : ");
		doc_title = sc.nextLine();
		System.out.println("문서 내용 : ");
		doc_contents = sc.nextLine();

		try {
			conn = ProjectDBConnection.getConn();
			String sql = " select seq_doc_id.nextval as doc_id " + " from dual ";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				doc_id = Integer.toString(rs.getInt("doc_id"));
			}
			sql = " select level - 1 AS gradeno " + "     , emp_id" + " from tbl_employees " + " where level > 1 "
					+ " start with emp_id = ? " + " connect by prior FK_manager_id = emp_id ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, member.getEmp_id());

			rs = pstmt.executeQuery();
			int cnt = 0;
			int cnt2 = 0;
			Map<String, String> paraMap = new HashMap<>();

			while (rs.next()) {
				cnt2++;
				cnt++;

				level_id = Integer.toString(rs.getInt("gradeno"));
				FK_Decision_Emp_Id = Integer.toString(rs.getInt("emp_id"));
				// levelid = "level_id"+Integer.toString(cnt);

				paraMap.put("level_id" + Integer.toString(cnt), level_id);
				paraMap.put("FK_Decision_Emp_Id" + Integer.toString(cnt), FK_Decision_Emp_Id);

			}

			sql = " insert into tbl_documents(doc_id, fk_writer_emp_id, doc_title, doc_contents) "
					+ " values(? , ?, ?, ?) ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, doc_id);
			pstmt.setInt(2, member.getEmp_id());
			pstmt.setString(3, doc_title);
			pstmt.setString(4, doc_contents);

			n = pstmt.executeUpdate(); // 실행 --> 1 or 0
			int k = 0;
			if (n == 1) { // insert 성공시
				cnt = 0;

				System.out.println("확인용" + paraMap.get("level_id" + Integer.toString(1)));
				System.out.println("확인용" + paraMap.get("FK_Decision_Emp_Id" + Integer.toString(1)));
				for (int i = 0; i < cnt2; i++) {
					cnt++;
					sql = " insert into tbl_documents_decision(decision_id, fk_doc_id, level_id, fk_decision_emp_id) "
							+ " values(seq_decision_id.nextval, ?, ?, ?) ";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, doc_id);
					pstmt.setString(2, paraMap.get("level_id" + Integer.toString(cnt)));
					pstmt.setString(3, paraMap.get("FK_Decision_Emp_Id" + Integer.toString(cnt)));

					k = pstmt.executeUpdate();
					if (k == 1) {
						System.out.println("승인이 활성화 되었습니다.");
						result = 1;
					} else {// insert 안되면
						k = 0;
						break;
					}

				}
			} else if (n == 0) {
				System.out.println("문서 기안에 실패했습니다.");
			}

			if (result == 1) {
				System.out.println("문서 기안에 성공했습니다.");
				// 커밋
			} else {
				// rollback
			}

		} catch (SQLIntegrityConstraintViolationException e) {// 제약조건에 위배되는 애들
			e.printStackTrace();
			result = -1;
		} catch (SQLException e) {
			e.printStackTrace();
			result = -1;
		} finally {
			close();
		}
		return result;
	}

	// 자기가 쓴 문서 조회 (수정완)
	@Override
	public StringBuilder DocView(EmployeeDTO member, Scanner sc) {
		StringBuilder sb = new StringBuilder();
		try {
			conn = ProjectDBConnection.getConn();
			
			String sql = " WITH  "+
					" V1 AS ( "+
					"       select   A.DOC_ID "+
					"              , A.fk_writer_emp_id "+
					"              , E1.name as WRITER_NAME "+
					"              , A.DOC_TITLE "+
					"              , A.DOC_CONTENTS "+
					"              , A.writeday "+
					"       from TBL_DOCUMENTS A JOIN TBL_EMPLOYEES E1 "+
					"       on A.fk_writer_emp_id = E1.emp_id "+
					"       WHERE A.FK_WRITER_EMP_ID = ?) "+
					" ,V2 AS (  "+
					"         select C.FK_DOC_ID "+
					"              , C.LEVEL_ID "+
					"              , C.FK_DECISION_EMP_ID "+
					"              , E2.name AS DECISION_NAME "+
					"              , C.CK_APPROVAL "+
					"              , C.COMMENTS "+
					"              , C.DECISION_DAY "+
					"         from tbl_documents_decision C JOIN TBL_EMPLOYEES E2 "+
					"         on C.fk_decision_emp_id = E2.emp_id ) "+
					" select   V1.DOC_ID AS 문서번호 "+
					"       , V1.fk_writer_emp_id AS 기안작성자사원번호 "+
					"       , V1.WRITER_NAME AS 기안작성자사원명 "+
					"       , V1.DOC_TITLE AS 문서제목 "+
					"       , V1.DOC_CONTENTS AS 문서내용 "+
					"       , V1.writeday AS 작성일자 "+
					"       , V2.LEVEL_ID AS 승인단계 "+
					"       , V2.FK_DECISION_EMP_ID AS 승인사원번호 "+
					"       , V2.DECISION_NAME AS 승인사원명 "+
					" , decode(V2.CK_APPROVAL, 0, '결재대기중',1,'승인',2,'반려','-') AS 승인여부 "+
					"       , V2.comments AS 코멘트 "+
					"       , V2.decision_day AS 승인날짜 "+
					" from V1 JOIN V2 "+
					" on V1.DOC_ID = V2.FK_DOC_ID "+
					" order by 문서번호 desc, 승인단계 asc ";
			 
			 pstmt = conn.prepareStatement(sql);
			 pstmt.setInt(1,member.getEmp_id());
			 
			 
			 rs = pstmt.executeQuery();
			 
			 
			 while(rs.next()) {			 
				 sb.append(  Util.cut_data(Integer.toString(rs.getInt("문서번호")),4)+ "\t" +
						 Util.cut_data(Integer.toString(rs.getInt("기안작성자사원번호")),5)+ "\t" +
						 Util.cut_data(rs.getString("문서제목"), 7)  + "\t" +
						 Util.cut_data(rs.getString("문서내용"),10)+ "\t" +
						 Util.cut_data((rs.getString("작성일자").substring(0,10)),10)+ "\t" +
						 Util.cut_data(Integer.toString(rs.getInt("승인단계")),10)+ "\t" +			 
						 Util.cut_data(Integer.toString(rs.getInt("승인사원번호")),10)+ "\t" +
						 Util.cut_data(rs.getString("승인사원명"),10)+ "\t" +
						 Util.cut_data(rs.getString("승인여부"),10)+ "\t" +
						 Util.cut_data(rs.getString("코멘트"),10)+ "\t" +
						 Util.cut_data(rs.getString("승인날짜"),10) + "\n");
			 }
				 
			 
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return sb;
		
		
	}

	// &&& 승인해야할 목록(전 사람이 결재 안한것만)(수정완)
		 @Override
		public StringBuilder DocAproval(EmployeeDTO employee, Scanner sc) {
			StringBuilder sb = new StringBuilder();
			try {
				conn = ProjectDBConnection.getConn();
				
				String sql = " WITH  "+
						" V1 AS (\n"+
						" select   A.DOC_ID "+
						"    , A.fk_writer_emp_id "+
						"    , E1.name as WRITER_NAME "+
						"    , A.DOC_TITLE "+
						"    , A.DOC_CONTENTS "+
						"    , A.writeday "+
						" from TBL_DOCUMENTS A JOIN TBL_EMPLOYEES E1 "+
						" on A.FK_WRITER_EMP_ID = E1.emp_id) "+
						" ,V2 AS ( "+
						" select C.FK_DOC_ID "+
						"    , C.LEVEL_ID "+
						"    , C.FK_DECISION_EMP_ID "+
						"    , E2.name AS DECISION_NAME "+
						"    , C.CK_APPROVAL "+
						"    , C.COMMENTS "+
						"    , C.DECISION_DAY "+
						"    , C.DECISION_ID "+
						"            ,case when (((lead(CK_APPROVAL, 1)over(order by DECISION_ID desc)) =1 and "+
						"             ( FK_DOC_ID =(lead(FK_DOC_ID, 1)over(order by DECISION_ID desc))) and "+
						"             ((level_id - 1 =(lead(level_id, 1)over(order by DECISION_ID desc))) and "+
						"             (CK_APPROVAL)=0)) or level_id =1 )then 1 else 0 end  as checks "+
						" from tbl_documents_decision C JOIN TBL_EMPLOYEES E2 "+
						" on C.fk_decision_emp_id = E2.emp_id "+
						" ) "+
						" select   V1.DOC_ID AS 문서번호 "+
						"    , V1.fk_writer_emp_id AS 기안작성자사원번호 "+
						"    , V1.WRITER_NAME AS 기안작성자사원명 "+
						"    , V1.DOC_TITLE AS 문서제목 "+
						"    , V1.DOC_CONTENTS AS 문서내용 "+
						"    , V1.writeday AS 작성일자 "+
						"    , V2.LEVEL_ID AS 승인단계 "+
						"    , V2.FK_DECISION_EMP_ID AS 승인사원번호 "+
						"    , V2.DECISION_NAME AS 승인사원명 "+
						"    , V2.CK_APPROVAL AS 승인여부 "+
						"    , V2.comments AS 코멘트 "+
						"    , V2.decision_day AS 승인날짜"+
						"    , V2.checks as 체크 "+
						"    from V1 JOIN V2 "+
						" on V1.DOC_ID = V2.FK_DOC_ID "+
						" WHERE  V2.FK_DECISION_EMP_ID = ?  and "+
						"        V2.checks  = 0  and V2.CK_APPROVAL=0 "+
						" order by 문서번호 desc, 승인단계 asc ";;
				 
				 pstmt = conn.prepareStatement(sql);
				 pstmt.setInt(1,employee.getEmp_id());
				 
				 rs = pstmt.executeQuery();

				 while(rs.next()) {	
					 
					 sb.append(  Integer.toString(rs.getInt("문서번호"))+ "\t" +
								 Integer.toString(rs.getInt("기안작성자사원번호"))+ "\t" +
								 rs.getString("문서제목")+ "\t" +
								 rs.getString("문서내용")+ "\t" +
								 rs.getString("작성일자")+ "\t" +
								 Integer.toString(rs.getInt("승인단계"))+ "\t" +			 
								 Integer.toString(rs.getInt("승인사원번호"))+ "\t" +
								 rs.getString("승인사원명")+ "\t" +
								 Integer.toString(rs.getInt("승인여부"))+ "\t" +
								 rs.getString("코멘트")+ "\t" +
								 rs.getString("승인날짜")+ "\n");
				 }			 	 
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				close();
			}
			
			return sb;
		}

		 // 승인 or 반려 or 보류(수정완)
		@Override
		public int Aproval(EmployeeDTO member, String choiceno, String docno, Scanner sc) {
			int result = 0;
			String approval = "";
			String comments = "";
			try {
				if(choiceno.equals("1") || choiceno.equals("2")) {
				
					if(choiceno.equals("1")) { // 승인
						approval = "1"; //  
						
					}
					else if(choiceno.equals("2")){// 반려
						approval = "2";
					}					
					System.out.println("코멘트 입력 : ");
					comments = sc.nextLine();
					
					conn = ProjectDBConnection.getConn();
					String sql = " update tbl_documents_decision set ck_approval = ? "+
							"                                , comments = ? "+
							"                                , decision_day = sysdate " +
							" where fk_doc_id = ? and fk_decision_emp_id = ? ";
					
					pstmt = conn.prepareStatement(sql);
					
					pstmt.setInt(1, Integer.parseInt(approval));
					pstmt.setString(2, comments);  
					pstmt.setString(3, docno);  
					pstmt.setInt(4, member.getEmp_id());
					
					result = pstmt.executeUpdate();
					if(result >= 0) {
						System.out.println("result : " + result);
						result = 1;
					}			
				}
				else if(choiceno.equals("3")) {
					System.out.println(">> 문서처리를 보류하셨습니다! <<");
				}
			
			} catch(SQLException e) {
			 //	e.printStackTrace();
				result = -1;
			} finally {
				close(); // 자원반납하기
			}
			return result;	
		}

	// 퇴직처리하기 전 한 번 보여주는 메소드
	@Override
	public EmployeeDTO manage_retirement(Map<String, String> paraMap) {
		EmployeeDTO edto = null;

		try {
			String sql = " select emp_id, Fk_dept_id, name, rank, uq_jubun, uq_mobile, hire_date, salary, FK_MANAGER_ID "
					+ " from tbl_employees " + " where emp_id = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("emp_id"));

			rs = pstmt.executeQuery();

			if (rs.next()) {
				edto = new EmployeeDTO();

				edto.setEmp_id(rs.getInt("EMP_ID"));
				edto.setFk_dept_id(rs.getInt("FK_DEPT_ID"));
				edto.setName(rs.getString("NAME"));
				edto.setRank(rs.getString("RANK"));
				edto.setUq_jubun(rs.getString("UQ_JUBUN"));
				edto.setUq_mobile(rs.getString("UQ_MOBILE"));
				edto.setHire_date(rs.getString("HIRE_DATE"));
				edto.setSalary(rs.getInt("SALARY"));
				edto.setFk_manager_id(rs.getInt("FK_MANAGER_ID"));

			} // end of if()------------------------------------

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return edto;
	} // 퇴직처리하기 전 한 번 보여주는 메소드

	// status 0 > 1 로 업데이트해주는 메소드
	@Override
	public int delete_emp(int emp_id) {
		int n = 0;
		try {
			String sql = " update tbl_employees set CK_RET_STATUS = 1 " + " where emp_id = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, emp_id);
			n = pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return n;
	} // status 0 > 1 로 업데이트해주는 메소드


	// 퇴사 처리 전 사수번호 update 구분
		@Override
		public int update_manager_id(EmployeeDTO employee) {
			
			String s_emp_id = "";
			int n = 0;
		      try {
		    	  // 선택한 
		    	  String sql = "select emp_id, fk_manager_id\n"+
			    			  "from tbl_employees\n"+
			    			  "where fk_manager_id = ?";
		            
		         pstmt = conn.prepareStatement(sql);
		         pstmt.setInt(1, employee.getEmp_id());
		         rs = pstmt.executeQuery();

		         StringBuilder sb = new StringBuilder();

		         while(rs.next()) {
		        	 s_emp_id = Integer.toString(rs.getInt("EMP_ID"));
		        	 sb.append(s_emp_id+","); 
		         }
		         
		         
		         String emp_update_list = sb.toString();
				 System.out.println(emp_update_list);
				
				 // 해당 부서의 사원이 1명일 경우와 여러명일 경우로 나누어서 sql 쿼리문 전달
				 if(emp_update_list.length() <= 4) {
					 sql = " update tbl_employees set fk_manager_id = ? " +
			        	   " where emp_id = ? ";
				 } else {
					 sql = " update tbl_employees set fk_manager_id = ? " +
			        	   " where emp_id in(?) ";
				 }
				
		         pstmt = conn.prepareStatement(sql);
		         pstmt.setInt(1, employee.getFk_manager_id()); 
		         pstmt.setString(2, emp_update_list); 
		         rs = pstmt.executeQuery();
		         
		      } catch (SQLException e) {
		         e.printStackTrace();
		      } finally {
		         close();
		      }
		      
		      return n;
			
		} // end of public void update_manager_id(EmployeeDTO employee)
	
		
		@Override
		//&&& 체크(수정완)
		public int DocCheck2(EmployeeDTO employee, Scanner sc, String docno) {
			int result = 0;
			try {
				String sql = " with V as( "
						+ " select FK_DECISION_EMP_ID, fk_DOC_ID, CK_APPROVAL, "
						+ " case when (((lead(CK_APPROVAL, 1)over(order by DECISION_ID desc)) =1 and "+
						"             ( FK_DOC_ID =(lead(FK_DOC_ID, 1)over(order by DECISION_ID desc))) and "+
						"             ((level_id - 1 =(lead(level_id, 1)over(order by DECISION_ID desc))) and "+
						"             (CK_APPROVAL)=0)) or  level_id =1  )then 1 else 0 end  as checks "+
						" from TBL_DOCUMENTS_DECISION "
						+ " order by DECISION_ID desc "
						+ " ) "
						+ " select V.* "
						+ " from V "
						+ " where FK_DECISION_EMP_ID = ? and fk_DOC_ID = ? and checks = 0 and CK_APPROVAL = 0 ";
				
				pstmt = conn.prepareStatement(sql);              
				pstmt.setInt(1, employee.getEmp_id()); 	 
				pstmt.setString(2, docno);    
				
				rs = pstmt.executeQuery();   
				
				if(rs.next()) {// 값이 있다면
					result = 1;
				}// end of if()------------------------------------------------
				
			}catch (SQLException e) {
				e.printStackTrace();
			} finally {
				close();
			}
			return result; 
		}

		
		
		// 우선결재 처리 //(수정완)
		@Override
		public int Aproval2(EmployeeDTO member, String choiceno, String docno, Scanner sc) {
			int result = 0;
			String approval = "";
			String comments = "";
			try {
				if(choiceno.equals("1") || choiceno.equals("2")) {
				
					if(choiceno.equals("1")) { // 승인
						approval = "1"; //  
						
					}
					else if(choiceno.equals("2")){// 반려
						approval = "2";
					}					
					System.out.println("코멘트 입력 : ");
					comments = sc.nextLine();
					
					int level_id = get_level(member, docno) ;
					if(level_id != 0) {
					
						conn = ProjectDBConnection.getConn();
						String sql = " update tbl_documents_decision set ck_approval = ? "+
								"                                , comments = '['|| ? || ' 우선결재]'|| ? "+
								"                                , decision_day = sysdate " +
								" where fk_doc_id = ?  and level_id <= ? ";
						
						pstmt = conn.prepareStatement(sql);
						
						pstmt.setInt(1, Integer.parseInt(approval));
						pstmt.setInt(2,member.getEmp_id());
						pstmt.setString(3, comments);  
						pstmt.setString(4, docno);  
						pstmt.setInt(5, level_id);
						
						result = pstmt.executeUpdate();
						if(result >= 0) {
							System.out.println("result : " + result);
							result = 1;
					}
					}			
				}
				else if(choiceno.equals("3")) {
					System.out.println(">> 문서처리를 보류하셨습니다! <<");
				}
			
			} catch(SQLException e) {
			 //	e.printStackTrace();
				result = -1;
			} finally {
				close(); // 자원반납하기
			}
			return result;	
		}

		
		
		
		//#####
		private int get_level(EmployeeDTO member, String docno) {
			int level_id=0;
			try {
				String sql = " select FK_DOC_ID, level_id, FK_DECISION_EMP_ID "+
						"                        from tbl_documents_decision "+
						"                        where FK_DOC_ID = ? and FK_DECISION_EMP_ID = ? ";
				
				pstmt = conn.prepareStatement(sql);             
				pstmt.setString(1, docno); 	 
				pstmt.setInt(2, member.getEmp_id());     
				
				rs = pstmt.executeQuery();                       
				
				if(rs.next()) {
					level_id=rs.getInt("level_id");
					
				}// end of if()------------------------------------------------
				else {
					System.out.println("없습니다");
				}
			}catch(SQLSyntaxErrorException e) {
				System.out.println("올바른 아이디, 암호를 입력하세요");
			} 
			catch (SQLException e) {
				e.printStackTrace();
			} finally {
				close();
			}
			return level_id; 
		}

	
		// 승인해야할 목록(수정완)
		 @Override
		public StringBuilder DocAproval2(EmployeeDTO employee, Scanner sc) {
			StringBuilder sb = new StringBuilder();
			try {
				conn = ProjectDBConnection.getConn();
				
				String sql = " WITH  "+
						" V1 AS (\n"+
						" select   A.DOC_ID "+
						"    , A.fk_writer_emp_id "+
						"    , E1.name as WRITER_NAME "+
						"    , A.DOC_TITLE "+
						"    , A.DOC_CONTENTS "+
						"    , A.writeday "+
						" from TBL_DOCUMENTS A JOIN TBL_EMPLOYEES E1 "+
						" on A.FK_WRITER_EMP_ID = E1.emp_id) "+
						" ,V2 AS ( "+
						" select C.FK_DOC_ID "+
						"    , C.LEVEL_ID "+
						"    , C.FK_DECISION_EMP_ID "+
						"    , E2.name AS DECISION_NAME "+
						"    , C.CK_APPROVAL "+
						"    , C.COMMENTS "+
						"    , C.DECISION_DAY "+
						"    , C.DECISION_ID "+
						"            ,case when (((lead(CK_APPROVAL, 1)over(order by DECISION_ID desc)) =1 and "+
						"             ( FK_DOC_ID =(lead(FK_DOC_ID, 1)over(order by DECISION_ID desc))) and "+
						"             ((level_id - 1 =(lead(level_id, 1)over(order by DECISION_ID desc))) and "+
						"             (CK_APPROVAL)=0)) or level_id =1 )then 1 else 0 end  as checks "+
						" from tbl_documents_decision C JOIN TBL_EMPLOYEES E2 "+
						" on C.fk_decision_emp_id = E2.emp_id "+
						" ) "+
						" select   V1.DOC_ID AS 문서번호 "+
						"    , V1.fk_writer_emp_id AS 기안작성자사원번호 "+
						"    , V1.WRITER_NAME AS 기안작성자사원명 "+
						"    , V1.DOC_TITLE AS 문서제목 "+
						"    , V1.DOC_CONTENTS AS 문서내용 "+
						"    , V1.writeday AS 작성일자 "+
						"    , V2.LEVEL_ID AS 승인단계 "+
						"    , V2.FK_DECISION_EMP_ID AS 승인사원번호 "+
						"    , V2.DECISION_NAME AS 승인사원명 "+
						"    , V2.CK_APPROVAL AS 승인여부 "+
						"    , V2.comments AS 코멘트 "+
						"    , V2.decision_day AS 승인날짜"+
						"    , V2.checks as 체크 "+
						"    from V1 JOIN V2 "+
						" on V1.DOC_ID = V2.FK_DOC_ID "+
						" WHERE  V2.FK_DECISION_EMP_ID = ?  and "+
						"        V2.checks  = 1  and V2.CK_APPROVAL=0 "+
						" order by 문서번호 desc, 승인단계 asc ";;
				 
				 pstmt = conn.prepareStatement(sql);
				 pstmt.setInt(1,employee.getEmp_id());
				 
				 rs = pstmt.executeQuery();
				 
			
				 while(rs.next()) {	
					 sb.append(  Integer.toString(rs.getInt("문서번호"))+ "\t" +
								 Integer.toString(rs.getInt("기안작성자사원번호"))+ "\t" +
								 rs.getString("문서제목")+ "\t" +
								 rs.getString("문서내용")+ "\t" +
								 rs.getString("작성일자")+ "\t" +
								 Integer.toString(rs.getInt("승인단계"))+ "\t" +			 
								 Integer.toString(rs.getInt("승인사원번호"))+ "\t" +
								 rs.getString("승인사원명")+ "\t" +
								 Integer.toString(rs.getInt("승인여부"))+ "\t" +
								 rs.getString("코멘트")+ "\t" +
								 rs.getString("승인날짜")+ "\n");
				 }			 	 
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				close();
			}
			
			return sb;
		}

		 @Override
			// 결제 가능 확인 //
			public int DocCheck(EmployeeDTO employee, Scanner sc, String docno) {
				int result = 0;
				try {
					String sql = " with V as( "
							+ " select FK_DECISION_EMP_ID, fk_DOC_ID, CK_APPROVAL, "
							+ " case when (((lead(CK_APPROVAL, 1)over(order by DECISION_ID desc)) =1 and "+
							"             ( FK_DOC_ID =(lead(FK_DOC_ID, 1)over(order by DECISION_ID desc))) and "+
							"             ((level_id - 1 =(lead(level_id, 1)over(order by DECISION_ID desc))) and "+
							"             (CK_APPROVAL)=0)) or  level_id =1  )then 1 else 0 end  as checks "+
							" from TBL_DOCUMENTS_DECISION "
							+ " order by DECISION_ID desc "
							+ " ) "
							+ " select V.* "
							+ " from V "
							+ " where FK_DECISION_EMP_ID = ? and fk_DOC_ID = ? and checks = 1 and CK_APPROVAL = 0 ";
					
					pstmt = conn.prepareStatement(sql);              
					pstmt.setInt(1, employee.getEmp_id()); 	 
					pstmt.setString(2, docno);    
					
					rs = pstmt.executeQuery();   
					
					if(rs.next()) {// 값이 있다면
						result = 1;
					}// end of if()------------------------------------------------
					
				}catch (SQLException e) {
					e.printStackTrace();
				} finally {
					close();
				}
				return result; 
			}

	@Override
	public void InnerDocinsert(EmployeeDTO employee, Scanner sc) {

		String doc_id = "";
		int n = 0;
		int result = 0;
		System.out.println("기안자 사원번호 : " + employee.getEmp_id());
		System.out.println("문서 제목 : ");
		String doc_title = sc.nextLine();
		System.out.println("문서 내용 : ");
		String doc_contents = sc.nextLine();
		System.out.println("최종 결재자 id : ");
		String doc_final = sc.nextLine();

		int n2 = emp_id_annual(Integer.parseInt(doc_final));
		if(n2 == 1) {
			
		try {
			conn = ProjectDBConnection.getConn();
			conn.setAutoCommit(false);
			String sql = " select seq_doc_id.nextval as doc_id " + " from dual ";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				doc_id = Integer.toString(rs.getInt("doc_id"));
			}

			sql = " insert into tbl_documents(doc_id, fk_writer_emp_id, doc_title, doc_contents) "
					+ " values(?,?, ?, ?) ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, doc_id);
			pstmt.setInt(2, employee.getEmp_id());
			pstmt.setString(3, doc_title);
			pstmt.setString(4, doc_contents);

			n = pstmt.executeUpdate(); // 실행 --> 1 or 0

			if (n == 1) { // insert 성공시
				System.out.println("문서 기안에 성공했습니다.");

				sql = " insert into tbl_documents_decision(decision_id, fk_doc_id, level_id, fk_decision_emp_id,CK_APPROVAL) "
						+ " values(seq_decision_id.nextval, ?, 0, ?,3) ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, doc_id);
				pstmt.setString(2, doc_final);
				result = pstmt.executeUpdate();
				if (result == 1) {
					conn.commit();
				}

			} else if (n == 0) {
				System.out.println("문서 기안에 실패했습니다.");
				result = -1;
			}
			if (result != 1) {
				conn.rollback(); // 롤백을 해준다.
			}
		} catch (SQLIntegrityConstraintViolationException e) {// 제약조건에 위배되는 애들
			System.out.println("제약 조건 위배!");
			e.printStackTrace();
		} catch (SQLException e) {
			if(e.getErrorCode() == 2291) {
				System.out.println("[경고] 존재하지 않는 사원번호입니다. ");
			}else if (e.getErrorCode() == 1438) {
				System.out.println("[경고] 존재하지 않는 사원번호입니다. ");
			}

			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			close();
		}
		}else {
			System.out.println("[경고] 존재하지 않는 사원번호입니다. ");
		}

	}

	// 반려, 승인하기전 문서 수정(수정완)
		@Override
		public int DocChange(EmployeeDTO employee, Scanner sc) {
			int result = 0;
			String DOC_TITLE = "";
			String DOC_CONTENTS = "";
			System.out.println("▷ 수정할 문서의 문서번호를 입력하세요 : ");
			
			String doc_id = "";
			do {
				doc_id =sc.nextLine();
				try {
					int doc_no = Integer.parseInt(doc_id);
					break;
				} catch (NumberFormatException e) {
					System.out.println("문서의 문서번호는 숫자만 입력가능합니다. ");
				}
			} while (true);
			
			try {// 문서 있는지부터 확인
				conn = ProjectDBConnection.getConn(); 
				conn.setAutoCommit(false);
				String sql = " select DOC_ID, DOC_TITLE, DOC_CONTENTS,  FK_WRITER_EMP_ID, CK_1, CK_2 "+
						" from TBL_DOCUMENTS D "+
						" join ( "+
						" select FK_DOC_ID, sum(CK_APPROVAL) as CK_1 "+
						" from TBL_DOCUMENTS_decision "+
						" group by FK_DOC_ID "+
						" )DD "+
						" on D.DOC_ID = DD.FK_DOC_ID "+
						" join ( "+
						" select FK_DOC_ID, decode(CK_APPROVAL , 2 , 1 ,0) as CK_2 "+
						" from TBL_DOCUMENTS_decision "+
						" )DDD " +
						" on DDD.FK_DOC_ID = D.DOC_ID "+
						" where (CK_1 = 0 or CK_2 = 1) and  FK_WRITER_EMP_ID = ? and DOC_ID = ? ";
				
				pstmt = conn.prepareStatement(sql);              
				pstmt.setInt(1, employee.getEmp_id()); 	
				pstmt.setString(2, doc_id);    
				
				rs = pstmt.executeQuery();   
				
				if(rs.next()) {// 값이 있다면
					
					DOC_CONTENTS = rs.getString("DOC_CONTENTS");
					DOC_TITLE = rs.getString("DOC_TITLE");
					System.out.print("▷ 문서명 (현재: "+DOC_TITLE + " ) [변경하지 않으려면 엔터] : ");
					DOC_TITLE = sc.nextLine();
					if( DOC_TITLE != null && (DOC_TITLE.trim().isEmpty()) ) {
						DOC_TITLE = rs.getString("DOC_TITLE");
					}
					System.out.print("▷ 문서명 (현재: "+DOC_CONTENTS + " ) [변경하지 않으려면 엔터] : ");
					DOC_CONTENTS = sc.nextLine();
					if( DOC_CONTENTS != null && (DOC_CONTENTS.trim().isEmpty()))  {
						DOC_CONTENTS = rs.getString("DOC_CONTENTS");
					}
						
						sql = " update TBL_DOCUMENTS set DOC_TITLE = ? , DOC_CONTENTS = ? "+
							  " where DOC_ID = ? ";	
						
						pstmt = conn.prepareStatement(sql);              
							
						pstmt.setString(1, DOC_TITLE);
						pstmt.setString(2, DOC_CONTENTS);
						pstmt.setString(3, doc_id); 
						System.out.println("2");
						
						int n = pstmt.executeUpdate();
						System.out.println("3");
						if(n==1) {
							System.out.println("4");
							sql = " update TBL_DOCUMENTS_DECISION   set CK_APPROVAL = 0   "+
								  " where fk_DOC_ID = ?  ";
							pstmt = conn.prepareStatement(sql); 
							
							pstmt.setString(1,doc_id);
							
							result = pstmt.executeUpdate();
							
							System.out.println("5");
							if(result != 0) {
								System.out.println("1");
								conn.commit();
								result = 1;
							}	
						}
						conn.rollback();
					}
				else{
					System.out.println(doc_id + "번의 문서에 대한 처리권한이 없거나 문서가 존재하지 않습니다.");
				}

			} catch (SQLIntegrityConstraintViolationException e) {// 제약조건에 위배되는 애들
				System.out.println("제약 조건 위배!");
				e.printStackTrace();
			}catch (SQLException e) {
				e.printStackTrace();
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			} finally {
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				close();
			}
			return result;
			
		}

	// 퇴근시간 입력 메소드
	@Override
	public List<CommuteDTO> commute_end(CommuteDTO cmmdto) {
		List<CommuteDTO> cmtdto = new ArrayList<>();

		int result = 0;

		try {

			String sql = " insert into tbl_commutetime(commute_id, fk_writer_emp_id, leave_time) "
					+ " values( seq_commute_id.nextval, ?, sysdate) ";

			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, cmmdto.getFk_writer_emp_id());
			result = pstmt.executeUpdate();
			cmtdto.add(cmmdto);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();// 자원 반납하기
		} // end of try catch finally-------------------------------------------------

		return cmtdto;

	}
	
	
	// 부서장 번호 유무를 확인해주는 메소드
	   @Override
	   public List<DeptDTO> dept_manager_id_list() {

	      List<DeptDTO> dept_manager_id_list = new ArrayList<>();
	      
	      try {
	         conn = ProjectDBConnection.getConn();
	         
	         String sql = " select fk_dept_manager_id "+
	                   " from tbl_departments " + 
	                   " where ck_dept_status = 0 "; 
	         
	         pstmt = conn.prepareStatement(sql);
	         
	         rs = pstmt.executeQuery();
	         
	         while(rs.next()) {
	            DeptDTO ddto = new DeptDTO();
	            
	            // 셀렉트 되어진 것을 boardDTO에 담는데
	            ddto.setFk_dept_manager_id(rs.getInt("FK_DEPT_MANAGER_ID"));
	            
	            
	            // set 해준 것들을 리스트에 담는다.
	            dept_manager_id_list.add(ddto);
	            
	         } // end of while
	            
	      } catch(SQLException e) {
	         e.printStackTrace();
	      } finally {
	         close();
	      }
	      
	      return dept_manager_id_list;
	      
	   } // end of public List<EmployeeDTO> emp_id_list()


	   // 원글의 글번호 유무 확인 메소드
	   @Override
	   public List<BoardDTO> find_board(int n_post_id) {
	      
	      List<BoardDTO> boardlist = null;

	      try {
	         String sql = "select post_id "
	               + " from tbl_board "
	               + " where post_id = ? ";
	         pstmt = conn.prepareStatement(sql);
	         pstmt.setInt(1, n_post_id);
	         
	         rs = pstmt.executeQuery();

	         if (rs.next()) {
	            boardlist = new ArrayList<>();
	            BoardDTO bdto = new BoardDTO();
	            bdto.setPost_id(rs.getInt("POST_ID"));
	            //////////////////////////////////////////////////////////
	            boardlist.add(bdto);
	         } // end of if()------------------------------------
	         
	      } catch (SQLException e) {
	         e.printStackTrace();
	      } finally {
	         close();
	      }
	      
	      return boardlist;
	   }   // 원글의 글번호 유무 확인 메소드

	   
	   //연차승인 //
	   @Override
		public int annual_approval(String vacation_id, Scanner sc) {
			int result = 0;
			try {
				conn.setAutoCommit(false);
	            String sql = " select VACATION_ID, FK_WRITER_EMP_ID, START_DATE, "
	            		+ " END_DATE, CK_APPROVAL, APPROVAL_DATE, COMMENTS "
	            		+ " from tbl_annual "
	            		+ "	where  vacation_id = ? and CK_APPROVAL = 1 " ; 
				
				pstmt = conn.prepareStatement(sql);              
				pstmt.setString(1, vacation_id); 	 	
				rs = pstmt.executeQuery(); 
				
				if(rs.next()) {
				
				String start_date = rs.getString("START_DATE");
				String end_date = rs.getString("END_DATE");
				int fk_writer_emp_id = rs.getInt("FK_WRITER_EMP_ID");
				
				int remain_annualleave = emp_id_annual(fk_writer_emp_id);
				int n = 0;
				// 결재대기중인 연차 확인
					
					String choiceno = "";
					String approval = "";
					String comments = "";
					
					// 일단 연차 얼마뺄지 계산
					int diff = Util.work_day(start_date, end_date);
					
					System.out.println("사용할 연차 : " + diff + "일");
					
						do {
							System.out.println( vacation_id + " 번의 연차를 어떻게 처리하시겠습니까?");
							System.out.println("1.최종결재  2.반려	3.보류");
							System.out.print("> 메뉴번호 입력 : ");
						    choiceno = sc.nextLine();
							
							if(!(choiceno.equals("1") ||choiceno.equals("2")||choiceno.equals("3"))) {
								System.out.println(">> 1 또는 2 또는 3 만 선택해주세요! <<");
							}		
						} while (!(choiceno.equals("1") ||choiceno.equals("2")||choiceno.equals("3")));
			
						if(choiceno.equals("1") || choiceno.equals("2")) {
						
							if(choiceno.equals("1")) { // 승인
								approval = "2"; //  
								
							}
							else if(choiceno.equals("2")){// 반려
								approval = "0";
							}					
							System.out.println("코멘트 입력 : ");
							comments = sc.nextLine();
							
							conn = ProjectDBConnection.getConn();
							sql = " update  tbl_annual set CK_APPROVAL = ?, COMMENTS = ? "+
								  " where VACATION_ID  = ? ";
							
							pstmt = conn.prepareStatement(sql);
							
							pstmt.setInt(1, Integer.parseInt(approval));
							pstmt.setString(2, comments);  
							pstmt.setString(3, vacation_id);
							
							int no = pstmt.executeUpdate();
							
								if(no == 1) {
								
									if(choiceno.equals("1")) {
									
										if(diff > remain_annualleave) {
											
											System.out.println("[경고] 남은 연차보다 많은 연차를 사용할 수 없습니다!");
										}
										else {
											sql = " update tbl_employees set REMAIN_ANNUALLEAVE = REMAIN_ANNUALLEAVE - ? "+
													" where emp_id = ? ";
											
											pstmt = conn.prepareStatement(sql);
											
											pstmt.setInt(1, diff );
											pstmt.setInt(2, fk_writer_emp_id); 
											
											result = pstmt.executeUpdate();
												if(result != 0) {
													result = 1;
													conn.commit();
											}
										} // 남은 연차와 비교해 많으면 실행 if 종료 if(diff > remain_annualleave) ------
								} // 승인절차
								if(choiceno.equals("2")) {
									result = 1;
									conn.commit();
									}
									
								}
								else {
									System.out.println("연차 승인에 실패하였습니다! 값을 확인해주세요");
								}
							}
						else if(choiceno.equals("3")) {
							System.out.println(">> 문서처리를 보류하셨습니다! <<");
						}
				}	
				else {
					result = -1;
					System.out.println("해당하는 연차번호의 연차는 승인처리를 할 수 없는 상태입니다.");
				}
				}catch(SQLSyntaxErrorException e) {
				e.printStackTrace();
			} 
			catch (SQLException e) {
				e.printStackTrace();
				
			} finally {
				try {
					conn.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				close();
			}
				
			return result; 
		}// end of method()------------
	   
		//id 넣으면 남은연차 반환해주는 메소드
	    public int emp_id_annual(int emp_id) {

	       int result = 0;
	       try {
	          conn = ProjectDBConnection.getConn();
	          
	          String sql = " select emp_id,REMAIN_ANNUALLEAVE  "+
	                    " from tbl_employees " + 
	                    " where  emp_id= ? "; 
	          
	          pstmt = conn.prepareStatement(sql);
	          pstmt.setInt(1, emp_id );
	          rs = pstmt.executeQuery();
	          
	          if(rs.next()) {
	        	  result = rs.getInt("REMAIN_ANNUALLEAVE");
	        	  System.out.println("남은연차 : " + result);

	          } // end of while
	             
	       } catch(SQLException e) {
	          e.printStackTrace();
	       } finally {
	          close();
	       }
	       
	       return result;
	       
	    } // end of public List<EmployeeDTO> emp_id_list()


	   
	   

	// 자기가 결재한 문서 조회(수정완)
		@Override
		public StringBuilder DocView2(EmployeeDTO member, Scanner sc) {
			StringBuilder sb = new StringBuilder();
			try {
				conn = ProjectDBConnection.getConn();
				String sql = " WITH  "+
		                  " V1 AS ( "+
		                  "       select   A.DOC_ID "+
		                  "              , A.fk_writer_emp_id "+
		                  "              , E1.name as WRITER_NAME "+
		                  "              , A.DOC_TITLE "+
		                  "              , A.DOC_CONTENTS "+
		                  "              , A.writeday "+
		                  "       from TBL_DOCUMENTS A JOIN TBL_EMPLOYEES E1 "+
		                  "       on A.fk_writer_emp_id = E1.emp_id )"+
		                  " ,V2 AS (  "+
		                  "         select C.FK_DOC_ID "+
		                  "            , C.LEVEL_ID \n"+
		                  "              , C.FK_DECISION_EMP_ID "+
		                  "              , E2.name AS DECISION_NAME "+
		                  "            , C.CK_APPROVAL "+
		                  "            , C.COMMENTS "+
		                  "            , C.DECISION_DAY "+
		                  "              , CC.last_check "+
		                  "          from tbl_documents_decision C "+
		                  "          join( "+
		                  "          select FK_DOC_ID, sum(CK_APPROVAL), STDDEV(CK_APPROVAL), max(LEVEL_ID),(case when ((sum(CK_APPROVAL)= max(LEVEL_ID))and STDDEV(CK_APPROVAL) = 0 )then '결재' else '미결재' end) as last_check "+
		                  "          from tbl_documents_decision "+
		                  "          group by FK_DOC_ID "+
		                  "          ) CC\n"+
		                  "          on C.FK_DOC_ID = CC.FK_DOC_ID "+
		                  "          JOIN TBL_EMPLOYEES E2 "+
		                  "          on C.fk_decision_emp_id = E2.emp_id where fk_decision_emp_id = ? ) "+
		                  " select   V1.DOC_ID AS 문서번호 "+
		                  "       , V1.fk_writer_emp_id AS 기안작성자사원번호 "+
		                  "       , V1.WRITER_NAME AS 기안작성자사원명 "+
		                  "       , V1.DOC_TITLE AS 문서제목 "+
		                  "       , V1.DOC_CONTENTS AS 문서내용 "+
		                  "       , V1.writeday AS 작성일자 "+
		                  "       , V2.LEVEL_ID AS 승인단계 "+
		                  "       , V2.FK_DECISION_EMP_ID AS 승인사원번호 "+
		                  "       , V2.DECISION_NAME AS 승인사원명 "+
		                  " , decode(V2.CK_APPROVAL, 0, '결재대기중',1,'승인',2,'반려','-') AS 승인여부 "+
		                  "       , V2.comments AS 코멘트 "+
		                  "       , V2.decision_day AS 승인날짜 "+
		                  "       , V2.last_check AS 전체승인" +
		                  " from V1 JOIN V2 "+
		                  " on V1.DOC_ID = V2.FK_DOC_ID and V2.LEVEL_ID !=0 "+
		                  " order by 문서번호 desc, 승인단계 asc ";
		             
				 pstmt = conn.prepareStatement(sql);
				 pstmt.setInt(1,member.getEmp_id());
				 
				 rs = pstmt.executeQuery();
				 
				 while(rs.next()) {			 
					 sb.append(  Integer.toString(rs.getInt("문서번호"))+ "\t" +
							 Integer.toString(rs.getInt("기안작성자사원번호"))+ "\t\t\t" +
							 rs.getString("문서제목")+ "\t" +
							 rs.getString("문서내용")+ "\t" +
							 rs.getString("작성일자")+ "\t" +
							 Integer.toString(rs.getInt("승인단계"))+ "\t" +			 
							 Integer.toString(rs.getInt("승인사원번호"))+ "\t" +
							 rs.getString("승인사원명")+ "\t" +
							 rs.getString("승인여부")+ "\t" +
							 rs.getString("코멘트")+ "\t" +
							 rs.getString("승인날짜")+ "\t"+
							 rs.getString("전체승인")+ "\n");				 }
					 
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				close();
			}
			
			return sb;
			
			
		}

	
	

}// end of public class TotalDAO implements
	// InterTotalDAO------------------------------------------------------------------
