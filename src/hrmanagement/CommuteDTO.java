package hrmanagement;

public class CommuteDTO {
	private int commute_id; // 출근시간 id
	private int fk_writer_emp_id; // 사원번호
	private String working_time; // 출근시간
	private String leave_time; // 퇴근시간
	private int absence; // 지각 여부

	public int getCommute_id() {
		return commute_id;
	}

	public void setCommute_id(int commute_id) {
		this.commute_id = commute_id;
	}

	public int getFk_writer_emp_id() {
		return fk_writer_emp_id;
	}

	public void setFk_writer_emp_id(int fk_writer_emp_id) {
		this.fk_writer_emp_id = fk_writer_emp_id;
	}

	public String getWorking_time() {
		return working_time;
	}

	public void setWorking_time(String working_time) {
		this.working_time = working_time;
	}

	public String getLeave_time() {
		return leave_time;
	}

	public void setLeave_time(String leave_time) {
		this.leave_time = leave_time;
	}

	public int getAbsence() {
		return absence;
	}

	public void setAbsence(int absence) {
		this.absence = absence;
	}

	@Override
	public String toString() {

		return commute_id + "\t" + fk_writer_emp_id + "\t" + working_time + "\t" + leave_time + "\t" + absence;

	}

}
