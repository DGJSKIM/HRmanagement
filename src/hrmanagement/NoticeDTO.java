package hrmanagement;

public class NoticeDTO {
	private int notice_id; 		  // 공지사항 번호
	private int fk_writer_emp_id; // 작성자 사원번호
	private String regist_date;   // 작성일자
	private String title;         // 공지사항 제목
	private String content;       // 공지사항 내용
	private int passwd;			  // 비밀번호
	private int notice_view;      // 조회수
	
	public int getNotice_view() {
		return notice_view;
	}
	public void setNotice_view(int notice_view) {
		this.notice_view = notice_view;
	}
	public int getPasswd() {
		return passwd;
	}
	public void setPasswd(int passwd) {
		this.passwd = passwd;
	}
	//select
	private EmployeeDTO employee;
	
	//private 
	
	public int getNotice_id() {
		return notice_id;
	}
	public void setNotice_id(int notice_id) {
		this.notice_id = notice_id;
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
	
	public String showNoticeTitle() {
		
		// 글번호\t글제목\t\t작성자명\t작성일자\t\t조회수 모양으로 나와야 한다.
		
		if(title != null && title.length() > 10 ) {
			title = title.substring(0,8)+"..";
			// 글제목이 10글자 이상이면 8글자만 보여주고 뒤에 ".."을 찍어준다.
		}
		
		String boardTitle = notice_id+"\t"+title+"\t"+employee.getName()+"\t"+regist_date+"\t"+notice_view;
		
		return boardTitle;
	}
	public EmployeeDTO getEmployee() {
		return employee;
	}
	public void setEmployee(EmployeeDTO employee) {
		this.employee = employee;
	}
	
	

}
