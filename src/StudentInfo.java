
public class StudentInfo {
	
	int id;
	String major;
	String gender;
	double score;
	
	public StudentInfo(){}
	
	public StudentInfo(int id, String major, String gender, double score){
		this.id = id;
		this.major = major;
		this.gender = gender;
		this.score = score;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public void setMajor(String major){
		this.major = major;
	}
	
	public void setGender(String gender){
		this.gender = gender;
	}
	
	public void setScore(double score){
		this.score = score;
	}
	
	public String toString(){
		return id + ", " + major + ", " + gender + ", " + score;
	}
}
