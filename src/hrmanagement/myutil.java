package hrmanagement;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class myutil {
	
		//암호는 대문자,소문자,숫자,특수기호가 혼합된 8글자 이상 15글자 이하인지 아닌지 검사를 해주는 메소드 생성하기
		// 파라미터 String pwd 값이 대문자,소문자,숫자,특수기호가 혼합된 8글자 이상 15글자 이하 이라면 true 를 리턴해주고,
		// 파라미터 String pwd 값이 위의 조건에 만족하지 않으면 false 를 리턴해주겠다.
		public static boolean checkPwd(String pwd) {
			
			// 입력받은 String pwd 의 글자길이 알아오기
			// System.out.println("입력받은 암호 길이 : " + pwd.length());
			
			boolean upperFlag = false;    // 대문자인지  기록하는 용도
			boolean lowerFlag = false;    // 소문자인지  기록하는 용도
			boolean digitFlag = false;    // 숫자인지    기록하는 용도
			boolean specialFlag = false;  // 특수기호인지 기록하는 용도
			
			int pwd_length = pwd.length(); // 비밀번호의 글자수
			
			if( pwd_length < 8 || pwd_length > 15 ) {
				return false;
			}
			else { // 비밀번호의 글자수가 8글자 이상 15글자 이하인 경우
				   // 암호가 어떤글자로 이루어졌는지 검사를 시도해야 한다.
				
				// pwd   ==> "qwEr1234$"
				// index ==>  012345678
				for(int i=0; i<pwd_length; i++) { // 입력받은 글자의 길이 만큼 검사를 해야 한다.
					
					char ch = pwd.charAt(i);
					
					if( Character.isUpperCase(ch) ) {  // 대문자이라면 
						upperFlag = true;
					}
					
					else if( Character.isLowerCase(ch) ) { // 소문자이라면 
						lowerFlag = true;
					}
					
					else if( Character.isDigit(ch) ) { // 숫자이라면
						digitFlag = true;
					}
					
					else { // 특수문자인 경우 
						specialFlag = true;
					}
					
				}// end of for---------------------
				
				if( upperFlag && lowerFlag && digitFlag && specialFlag ) {
					return true;
				}
				
				else {
					return false;
				}
			}
			
		}// end of boolean checkPwd(String pwd)---------------------------

		public static String addDay(int n) {
			Calendar currentDate = Calendar.getInstance();
			// 현재 날짜와 시간을 얻어온다.
			currentDate.add(Calendar.DATE, n);
			// currentDate.add(Calendar.DATE, 1); 
	        // ==> currentDate(현재 날짜)에서 두번째 파라미터에 입력해준 숫자(그 단위는 첫번째 파라미터인 것이다. 지금은 Calendar.DATE 이므로 날짜수이다)만큼 더한다.
	        // ==> 위의 결과는  currentDate 값이 1일 더한 값으로 변한다.   
			
			SimpleDateFormat dateft = new SimpleDateFormat("yyyy-MM-dd"); 
			return dateft.format(currentDate.getTime());
		
		} // end of public static String addDay(int n)----------------------------- 

		
}
