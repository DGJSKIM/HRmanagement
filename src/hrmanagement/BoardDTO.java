package hrmanagement;

public class BoardDTO {

	private String title; // 글제목
	private String content; // 글내용
	private int passwd; // 글비밀번호
	private int fk_writer_emp_id; // 작성자 사원번호
	private String regist_date; // 글 작성일자
	private int board_view; // 조회수
	private int post_id; // 글번호

	private EmployeeDTO employee;

	private int commentcnt;

	public EmployeeDTO getEmployee() {
		return employee;
	}

	public void setEmployee(EmployeeDTO employee) {
		this.employee = employee;
	}

	public int getPost_id() {
		return post_id;
	}

	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getPasswd() {
		return passwd;
	}

	public void setPasswd(int passwd) {
		this.passwd = passwd;
	}

	public int getFk_writer_emp_id() {
		return fk_writer_emp_id;
	}

	public void setFk_writer_emp_id(int fk_writer_emp_id) {
		this.fk_writer_emp_id = fk_writer_emp_id;
	}

	public String getRegist_date() {
		return regist_date;
	}

	public void setRegist_date(String regist_date) {
		this.regist_date = regist_date;
	}

	public int getBoard_view() {
		return board_view;
	}

	public void setBoard_view(int board_view) {
		this.board_view = board_view;
	}

	public String showBoardTitle() {

		// 글번호\t글제목\t\t작성자명\t작성일자\t\t조회수 모양으로 나와야 한다.

		if (title != null && title.length() > 10) {
			title = title.substring(0, 8) + "..";
			// 글제목이 10글자 이상이면 8글자만 보여주고 뒤에 ".."을 찍어준다.
		}
		if (commentcnt > 0) { // 만약 댓글 개수가 0 이상이라면
			title += "[" + commentcnt + "]";// 제목에 [댓글개수] 를 붙여준다.
		}
		String boardTitle = post_id + "\t" + title + "    " + employee.getName() + "\t" + regist_date + "\t"
				+ board_view;

		return boardTitle;
	}

	public int getCommentcnt() {
		return commentcnt;
	}

	public void setCommentcnt(int commentcnt) {
		this.commentcnt = commentcnt;
	}

}
