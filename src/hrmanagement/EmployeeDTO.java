package hrmanagement;

// 사원 정보를 전달하는 클래스 EmployeeDTO
public class EmployeeDTO { 
	
	private int emp_id;            // 사원번호(로그인 아이디)
	private String passwd;         // 사원 비밀번호
	private String name;           // 사원이름
	private String uq_email;       // 이메일
	private String uq_mobile;      // 연락처
	private String hire_date;      // 입사일
	private int fk_dept_id;  	   // 부서번호
	private int ck_ret_status;     // 퇴사여부(재직상태)
	private int salary;			   // 기본급(만원)
	private String uq_jubun;       // 주민등록번호
	private int fk_manager_id;     // 직속상관번호
	private String rank;           // 직위
	private String address;        // 사원집주소
	private String ret_date;       // 퇴사예정일
	private int remain_annualleave;// 잔여연차일수
	private int ck_manager_rank;   // 관리자등급
	/////////////////////////////////////////
	
	// 개인 사원 정보 select 용
   private String gender;
   private String age; 
   private String year_salary;
   private String dept_manager_name;
   private String retirement_day;
   private String retirement_salary;

	
	
   ///////select 용
   private String dept_name;
	
	

	public String getGender() {
	return gender;
}

public void setGender(String gender) {
	this.gender = gender;
}

public String getAge() {
	return age;
}

public void setAge(String age) {
	this.age = age;
}

public String getYear_salary() {
	return year_salary;
}

public void setYear_salary(String year_salary) {
	this.year_salary = year_salary;
}

public String getDept_manager_name() {
	return dept_manager_name;
}

public void setDept_manager_name(String dept_manager_name) {
	this.dept_manager_name = dept_manager_name;
}

public String getRetirement_day() {
	return retirement_day;
}

public void setRetirement_day(String retirement_day) {
	this.retirement_day = retirement_day;
}

public String getRetirement_salary() {
	return retirement_salary;
}

public void setRetirement_salary(String retirement_salary) {
	this.retirement_salary = retirement_salary;
}
	//select 용
	private DeptDTO ddto;
	
	
	
	
	public DeptDTO getDdto() {
		return ddto;
	}

	public void setDdto(DeptDTO ddto) {
		this.ddto = ddto;
	}

	public int getSalary() {
		return salary;
	}
	
	public void setSalary(int salary) {
		this.salary = salary;
	}
	
	public int getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(int empl_id) {
		this.emp_id = empl_id;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUq_email() {
		return uq_email;
	}
	public void setUq_email(String uq_email) {
		this.uq_email = uq_email;
	}
	public String getUq_mobile() {
		return uq_mobile;
	}
	public void setUq_mobile(String uq_mobile) {
		this.uq_mobile = uq_mobile;
	}
	public String getHire_date() {
		return hire_date;
	}
	public void setHire_date(String hire_date) {
		this.hire_date = hire_date;
	}
	public int getFk_dept_id() {
		return fk_dept_id;
	}
	public void setFk_dept_id(int fk_dept_id) {
		this.fk_dept_id = fk_dept_id;
	}
	public int getCk_ret_status() {
		return ck_ret_status;
	}
	public void setCk_ret_status(int ck_ret_status) {
		this.ck_ret_status = ck_ret_status;
	}
	public String getUq_jubun() {
		return uq_jubun;
	}
	public void setUq_jubun(String uq_jubun) {
		this.uq_jubun = uq_jubun;
	}
	public int getFk_manager_id() {
		return fk_manager_id;
	}
	public void setFk_manager_id(int fk_manager_id) {
		this.fk_manager_id = fk_manager_id;
	}
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getRet_date() {
		return ret_date;
	}
	public void setRet_date(String ret_date) {
		this.ret_date = ret_date;
	}
	public int getRemain_annualleave() {
		return remain_annualleave;
	}
	public void setRemain_annualleave(int remain_annualleave) {
		this.remain_annualleave = remain_annualleave;
	}
	public int getCk_manager_rank() {
		return ck_manager_rank;
	}
	public void setCk_manager_rank(int ck_manager_rank) {
		this.ck_manager_rank = ck_manager_rank;
	}
	
	
	///////select 용
	public String getDept_name() {
		return dept_name;
	}

	public void setDept_name(String dept_name) {
		this.dept_name = dept_name;
	}
	/////////////////////////////////////
	
	@Override
	   public String toString() {
	      
	      // DAO에서 select 해온 애들 리턴해주기
	      return "\n == 나의 정보 == \n"
	          + "◇ 부서번호 : " + fk_dept_id + "\n"
	          + "◇ 부서명 : " + ddto.getDept_name() + "\n"
	          + "◇ 직위 : " + rank + "\n"
	          + "◇ 사원명 : " + name + "\n"
	          + "◇ 성별 : " + gender + "\n"
	          + "◇ 나이 : " + age + "\n"
	          + "◇ 주민번호 : " + uq_jubun + "\n"
	          + "◇ 연락처 : " + uq_mobile + "\n"
	          + "◇ 이메일 : " + uq_email + "\n"
	          + "◇ 주소 : " + address + "\n"
	          + "◇ 연봉 : " + year_salary + "\n"
	          + "◇ 남은 연차 일수 : " + remain_annualleave + "\n"
	          + "◇ 입사일자 : " + hire_date.substring(0,10) + "\n"
	          + "◇ 부서장명 : " + dept_manager_name + "\n"
	          + "◇ 퇴직일 : " + retirement_day.substring(0,10) + "\n"
	          + "◇ 퇴직금 : " + retirement_salary + "\n" ; 
	   } // end of toString()------------------------------------------
}
