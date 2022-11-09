package hrmanagement;

public class AnnualDTO {
	
	private int vacation_id;        
	private int fk_writer_emp_id;      
	private String start_date;               
	private String end_date;                   
	private String ck_approval;          
	private String approval_date;              
	private String comments;
	// *** select 용 *** //
	
	private EmployeeDTO edto ;
	private DeptDTO ddto;
	//*** 겟셋 ***//
	public int getVacation_id() {
		return vacation_id;
	}
	public void setVacation_id(int vacation_id) {
		this.vacation_id = vacation_id;
	}
	public int getFk_writer_emp_id() {
		return fk_writer_emp_id;
	}
	public void setFk_writer_emp_id(int fk_writer_emp_id) {
		this.fk_writer_emp_id = fk_writer_emp_id;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getCk_approval() {
		return ck_approval;
	}
	public void setCk_approval(String ck_approval) {
		this.ck_approval = ck_approval;
	}
	public String getApproval_date() {
		return approval_date;
	}
	public void setApproval_date(String approval_date) {
		this.approval_date = approval_date;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public EmployeeDTO getEdto() {
		return edto;
	}
	public void setEdto(EmployeeDTO edto) {
		this.edto = edto;
	}
	public DeptDTO getDdto() {
		return ddto;
	}
	public void setDdto(DeptDTO ddto) {
		this.ddto = ddto;
	}
	
	public String showYeonchaTitle() {
		// "연차번호\t사원번호\t연차시작일자\t\t연차종료일자\t\t결재진행상황\t\t승인일자
		
		String showYeonchaTitle = vacation_id+"\t"+fk_writer_emp_id+"\t"+start_date+"\t"
							    + end_date+"\t"+ck_approval+"\t"+approval_date+"\n";
		return showYeonchaTitle ;
	}
	
		
}
