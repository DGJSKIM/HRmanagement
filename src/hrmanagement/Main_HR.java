package hrmanagement;

import java.util.*;

public class Main_HR {

	public static void main(String[] args) {
		Controller_HR ctrlhr = new Controller_HR();
		Scanner sc = new Scanner(System.in);

		ctrlhr.login(sc);

		sc.close();
		System.out.println("\n>>> 프로그램 종료 <<<");
	}

}
