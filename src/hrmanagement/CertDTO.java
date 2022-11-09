package hrmanagement;

import hrmanagement.EmployeeDTO;

public class CertDTO {


	private int seq_certificate_id;   	// 
	private String certificate_type;		// 
	private int	   fk_writer_emp_id;	// 
	private String use; 		// 
	private String issue_date;		// 
	private String retirement_date;		// 
	private String work_date;		//
	
	
	/////////////////////////////////////
	// 			select 용	(읽기용)			//
	private EmployeeDTO edto;   // tbl_cert 테이블과 tbl_employees 테이블을 join. 글쓴이에 대한 모든 정보
								// MemberDTO member은 오라클의 tbl_member 테이블(부모 테이블)에 해당.
	
	public void edto1(EmployeeDTO edto) {
		this.edto = edto;
	}
	
	public int getSeq_certificate_id() {
		return seq_certificate_id;
	}

	public void setSeq_certificate_id(int seq_certificate_id) {
		this.seq_certificate_id = seq_certificate_id;
	}

	public String getCertificate_type() {
		return certificate_type;
	}

	public void setCertificate_type(String certificate_type) {
		this.certificate_type = certificate_type;
	}

	public int getFk_writer_emp_id() {
		return fk_writer_emp_id;
	}

	public void setFk_writer_emp_id(int fk_writer_emp_id) {
		this.fk_writer_emp_id = fk_writer_emp_id;
	}

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	public String getIssue_date() {
		return issue_date;
	}

	public void setIssue_date(String issue_date) {
		this.issue_date = issue_date;
	}
	
	public void getIssue_date(String issue_date) {
		this.issue_date = issue_date;
	}

	public String getRetirement_date() {
		return retirement_date;
	}

	public void setRetirement_date(String retirement_date) {
		this.retirement_date = retirement_date;
	}

	public String getWork_date() {
		return work_date;
	}

	public void setWork_date(String work_date) {
		this.work_date = work_date;
	}

	public EmployeeDTO getEdto() {
		return edto;
	}

	public void setEdto(EmployeeDTO edto) {
		this.edto = edto;
	}

	public String cert_1() {
		
		String info = "";
		info =  "\n====================================================== \n"
				+ " \t\t     재직증명서 \n"
				+ "\t\t\t\t 증명서번호 : " + seq_certificate_id +"\n"
				+ "사원번호 : " + fk_writer_emp_id + "\n"
				+ "직위 : " + edto.getRank()+ "\n"
				+ "성명 : " + edto.getName() + "\n"
				+ "주민등록번호 : " + edto.getUq_jubun().substring(0, 6) + "-" + edto.getUq_jubun().substring(6, 13)+ "\n"
				+ "회사명	: 쌍용기업 \n"
				+ "입사일자	 : "+ edto.getHire_date().substring(0, 10) + "\n\n"
				+ " \t\t 상기와 같이 재직하고 있음을 확인합니다. \n"
				+ "용도 : " + use + "\n"
				+ "발행일자	: " + issue_date.substring(0, 10) +"\n"
				+ "======================================================== \n\n";
		return info;
	}

	public Object cert_2() {
		
		int n1 = 0;
		int n2 = 0;
		
		if ((Integer.parseInt(getRetirement_date().substring(5,7)) - Integer.parseInt(edto.getHire_date().substring(5,7) ) ) >= 0 ) {
			n1 = (Integer.parseInt(getRetirement_date().substring(0,4)) - Integer.parseInt(edto.getHire_date().substring(0,4) ) );
		}
		else 
			n1 = (Integer.parseInt(getRetirement_date().substring(0,4)) - Integer.parseInt(edto.getHire_date().substring(0,4) ) - 1);
			
		if  ((Integer.parseInt(getRetirement_date().substring(8,10)) - Integer.parseInt(edto.getHire_date().substring(8,10) ) ) >= 0 ) 
			n2 = (Integer.parseInt(getRetirement_date().substring(5,7)) - Integer.parseInt(edto.getHire_date().substring(5,7) ) + 12 );
			
		else
			n2 = (Integer.parseInt(getRetirement_date().substring(5,7)) - Integer.parseInt(edto.getHire_date().substring(5,7) ) + -1 );
		
		
		String info = "";
		if(n1 == 0 ) {
			info =  "\n====================================================== \n"
					+ " \t\t     경력증명서 \n"
					+ "\t\t\t\t 증명서번호 : " + seq_certificate_id +"\n"
					+ "사원번호 : " + fk_writer_emp_id + "\n"
					+ "직위 : " + edto.getRank()+ "\n"
					+ "성명 : " + edto.getName() + "\n"
					+ "주민등록번호 : " + edto.getUq_jubun().substring(0, 6) + "-" + edto.getUq_jubun().substring(6, 13)+ "\n"
					+ "회사명	: 쌍용기업 \n"
					+ "입사일자	 : "+ edto.getHire_date().substring(0, 10) + "\n"
					+ "경력기간 : " + n2 + "개월 \n"								
					+ " \t\t 상기와 같은 경력이 있음을 확인합니다. \n"
					+ "용도 : " + use + "\n"
					+ "발행일자	: " + issue_date.substring(0, 10) +"\n"
					+ "======================================================== \n\n";

			return info;

		}else {
		}
		info =  "\n====================================================== \n"
				+ " \t\t     경력증명서 \n"
				+ "\t\t\t\t 증명서번호 : " + seq_certificate_id +"\n"
				+ "사원번호 : " + fk_writer_emp_id + "\n"
				+ "직위 : " + edto.getRank()+ "\n"
				+ "성명 : " + edto.getName() + "\n"
				+ "주민등록번호 : " + edto.getUq_jubun().substring(0, 6) + "-" + edto.getUq_jubun().substring(6, 13)+ "\n"
				+ "회사명	: 쌍용기업 \n"
				+ "입사일자	 : "+ edto.getHire_date().substring(0, 10) + "\n"
				+ "경력기간 : " + n1 + " 년 " + n2 + "개월 \n"								
				+ " \t\t 상기와 같은 경력이 있음을 확인합니다. \n"
				+ "용도 : " + use + "\n"
				+ "발행일자	: " + issue_date.substring(0, 10) +"\n"
				+ "======================================================== \n\n";

		return info;
		
	}
	












}
