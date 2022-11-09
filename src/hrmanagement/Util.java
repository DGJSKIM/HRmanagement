package hrmanagement;

import java.text.SimpleDateFormat;
import java.util.*;

public class Util {

	public static int yn(Scanner sc) { // Y 면 1 N 이면 0
	// sc 포함되어 있으니 질문다음에 그냥 바로 int n = Util.yn(); 치시면 됩니다.	

	int result = 0;
	do {
		String yn =sc.nextLine();
		if("y".equalsIgnoreCase(yn)) { 
			result = 1; 
			break;
		}
		else if("n".equalsIgnoreCase(yn)) {
			break;
		}
		else {
			System.out.println(">> 'Y' 또는 'N'만 입력하세요!! << \n");
		}
	} while (true);
	
		
    return result;
    
	
	} // end of public static int yn() ------------------------------


	public static String addDay(int n) {
    
	    Calendar currentDate =  Calendar.getInstance();
	    // 현재 날짜와 시간을 얻어온다.
	    
	    currentDate.add(Calendar.DATE, n);
	    // currentDate.add(Calendar.DATE, 1); 
	     // ==> currentDate(현재 날짜)에서 두번째 파라미터에 입력해준 숫자(그 단위는 첫번째 파라미터인 것이다. 지금은 Calendar.DATE 이므로 날짜수이다)만큼 더한다.
	     // ==> 위의 결과는  currentDate 값이 1일 더한 값으로 변한다.   
	    
	    SimpleDateFormat dateft = new SimpleDateFormat("yyyy-MM-dd");
	    
	    return dateft.format(currentDate.getTime());
    
	}// end of public static String addDay(int n)-------------------------------
	
	
	  public static int work_day(String start_date, String end_date) {
          int work_day = 0; int a = 0; 
          try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date Start_date = formatter.parse(start_date);
            Date End_date = formatter.parse(end_date);
            Calendar cal = Calendar.getInstance();
            
            cal.setTime(End_date);
            int dayNum = cal.get(Calendar.DAY_OF_WEEK);
            
           
	         Date format1 = new SimpleDateFormat("yyyy-MM-dd").parse(start_date);
	         Date format2 = new SimpleDateFormat("yyyy-MM-dd").parse(end_date);
	         long diffSec = (format2.getTime() - format1.getTime()) / 1000; //초 차이
	         long diffDays = diffSec / (24*60*60); //일자수 차이  
	          
	         for(int i = 0 ; i <diffDays+1; i++) {           
	           cal.setTime(Start_date);
	           cal.add(Calendar.DATE, i);
	           dayNum = cal.get(Calendar.DAY_OF_WEEK);
	
	           if(dayNum >=2 && dayNum <=6) {
	        	   work_day++;
               }
            }         
         }catch(Exception e) {
             System.out.println("입력값 오류");
       }
      return work_day;
   }// end of public static String addDay(int n)-------------------------------
	  
	  
	  
	  public static String cut_data(String data, int cut_no) {
		  
		  String result = data;
		  if(result == null){
			  result = "-";
		  }
		  else {
			  if(result.length() > cut_no) {
				  result =  result.substring(0,cut_no) + "...";
			  }	  
			  else {
				  for(int i=0; i<(cut_no + 3 - result.length()); i++) {				  
					  result += " ";
				  }			  
			  }			  
		  }
		  return result;
	  } // end of  public static String cut_data(String result, int cut_no) {
	

}
