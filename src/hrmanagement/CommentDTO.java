package hrmanagement;


public class CommentDTO {
	private int comment_id;       // 댓글번호
	private int fk_writer_emp_id; // 댓글작성자 사원번호 
	private int fk_post_id;       // 원글 번호      
	private String content;       // 댓글내용            
	private String regist_date;   // 댓글 작성 일자
	
	// select 용
	private EmployeeDTO employee;
	
	public int getComment_id() {
		return comment_id;
	}

	public void setComment_id(int comment_id) {
		this.comment_id = comment_id;
	}

	public int getFk_writer_emp_id() {
		return fk_writer_emp_id;
	}

	public void setFk_writer_emp_id(int fk_writer_emp_id) {
		this.fk_writer_emp_id = fk_writer_emp_id;
	}

	public int getFk_post_id() {
		return fk_post_id;
	}

	public void setFk_post_id(int fk_post_id) {
		this.fk_post_id = fk_post_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRegist_date() {
		return regist_date;
	}

	public void setRegist_date(String regist_date) {
		this.regist_date = regist_date;
	}

	public EmployeeDTO getEmployee() {
		return employee;
	}

	public void setEmployee(EmployeeDTO employee) {
		this.employee = employee;
	}

	
	public String viewCommentInfo() {
		// 댓글내용\t\t작성자\t작성일자
		return content + "\t\t" + employee.getName() +"\t" + regist_date;
	}
	
	
	

}
