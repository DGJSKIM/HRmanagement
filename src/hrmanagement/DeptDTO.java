package hrmanagement;

public class DeptDTO {

	// field
	private int dept_id; 			// 부서번호
	private String dept_name; 		// 부서명
	private int ck_dept_status; 	// 부서폐쇄여부            
	private int fk_dept_manager_id;    // 부서장번호         
	
	// 부서장 이름하고 join하기 위해서 employee 불러옴
	private EmployeeDTO emp;
	
	// select 용 // 

	// getter-setter
	public int getDept_id() {
		return dept_id;
	}
	
	public void setDept_id(int dept_id) {
		this.dept_id = dept_id;
	}
	
	public String getDept_name() {
		return dept_name;
	}
	
	public void setDept_name(String dept_name) {
		this.dept_name = dept_name;
	}
	
	public int getCk_dept_status() {
		return ck_dept_status;
	}
	
	public void setCk_dept_status(int ck_dept_status) {
		this.ck_dept_status = ck_dept_status;
	}

	public int getFk_dept_manager_id() {
		return fk_dept_manager_id;
	}

	public void setFk_dept_manager_id(int fk_dept_manager_id) {
		this.fk_dept_manager_id = fk_dept_manager_id;
	}
	
	public EmployeeDTO getEmp() {
		return emp;
	}

	public void setEmp(EmployeeDTO emp) {
		this.emp = emp;
	}

	/////////////////////////////////////////////////////
	// 부서 정보 보기를 해주는 메소드
	public String showDeptList() {
		
		String BoardTitle = dept_id+"\t"+dept_name+"\t"+fk_dept_manager_id+"\t         "+emp.getName()+"\t";
		// 여기서 emp는 select 용으로 받아온 emp 이다. 
		
		return BoardTitle;
	} // end of public String showDeptList()
	
} // end of class
