package org.hustunique.lockscreen.tools;

public class LockContent {

	private String body;  //单词或公式
	private String mark;	//音标或注解
	private String mean;	//中文意思或解释
	private int id;		//在数据库中的id
	
	public void setBody(String body){
		this.body = body;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public void setMean(String mean) {
		this.mean = mean;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getBody() {
		return body;
	}
	public String getMark() {
		return mark;
	}
	public String getMean() {
		return mean;
	}
	public int getId() {
		return id;
	}
}
