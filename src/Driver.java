import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
 
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.Collections;

import org.json.simple.JSONObject;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class Driver {
	
	static ArrayList<StudentInfo> studentInfo = new ArrayList<StudentInfo>();
	
	public static void main(String[] args){

		File studentInfoFile = new File("Student Info.xlsx");
		File testScoresFile = new File("Test Scores.xlsx");
		File retakeTestScoresFile = new File("Test Retake Scores.xlsx");
		readFile(studentInfoFile);
		readFile(testScoresFile);
		readFile(retakeTestScoresFile);
		
		
		//get average test score
		double total = 0;
		for(int i = 0; i < studentInfo.size(); i++){
			total += studentInfo.get(i).score;
		}
		int average = (int)Math.round(total/studentInfo.size());
		
		
		//get computer science females
		ArrayList<Integer> girlsWhoCode = new ArrayList<Integer>();
		for(int i = 0; i < studentInfo.size(); i++){
			if(studentInfo.get(i).major.equals("computer science") && studentInfo.get(i).gender.equals("F")){
				girlsWhoCode.add(studentInfo.get(i).id);
			}
		}
		Collections.sort(girlsWhoCode);
		
		
		//create JSON object and post to server
		JSONObject obj = new JSONObject();
		obj.put("id", "lhannema@butler.edu");
		obj.put("name", "Lauren Hannemann");
		obj.put("average", average);
		obj.put("studentIds", girlsWhoCode);
		
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		try{
			HttpPost request = new HttpPost("http://3.86.140.38:5000/challenge");
			
			StringEntity params = new StringEntity(obj.toString());
		    request.addHeader("content-type", "application/json");
		    request.setEntity(params);
		    httpClient.execute(request);
		    
		} catch(Exception x){
			x.printStackTrace();
		}
		
	    
	}
	
	//create ArrayList of Student objects using data from Excel files
	public static void readFile(File fileName){
		try{
			
			FileInputStream inputStream = null;
			try {
				inputStream = new FileInputStream(fileName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		
			Workbook workbook = new XSSFWorkbook(inputStream);

			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();
        
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
            
				if(nextRow.getRowNum() == 0){
					continue;
				}
            
				StudentInfo studentInfoRow = new StudentInfo();
            
				int i = 0;
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
                            	
					if(fileName.getName().equals("Student Info.xlsx")){
						switch (i) {
                    		case 0:
                    			studentInfoRow.setId((int)cell.getNumericCellValue());
                    			break;
                    		case 1:
                    			studentInfoRow.setMajor(cell.getStringCellValue());
                    			break;
                    		case 2:
                    			studentInfoRow.setGender(cell.getStringCellValue());
                    			studentInfo.add(studentInfoRow);
                    			break;
                    		default:
                    			break;
                    	
						}
                	
					}
					else if(fileName.getName().equals("Test Scores.xlsx")){
						switch (i){
						case 0:
							for(int j = 0; j < studentInfo.size(); j++){
								if(studentInfo.get(j).id == cell.getNumericCellValue()){                				
									studentInfo.get(j).score = cellIterator.next().getNumericCellValue();
								}
							}
						}
					}
					else{
						Cell nextCell = cellIterator.next();
						switch (i){
						case 0:
							for(int n = 0; n < studentInfo.size(); n++){
								if(studentInfo.get(n).id == cell.getNumericCellValue() && studentInfo.get(n).score < nextCell.getNumericCellValue()){
									studentInfo.get(n).score = nextCell.getNumericCellValue();
								}
							}
						}
					}
                 
					i++;
				}
            
			}
         
			workbook.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
