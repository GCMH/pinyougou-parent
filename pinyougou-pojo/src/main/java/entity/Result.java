package entity;


/**
 * 操作结果返回类  
* 创建时间：2019年5月14日 下午8:05:02  
* 项目名称：pinyougou-pojo  
* @author hcf  
* @version 1.0   
* @since JDK 1.8.0_201  
* @param success:操作是否成功
* @param info:操作完成携带信息
* 文件名称：Result.java  
* 说       明： 品牌增、删、改返回信息。 
*/


public class Result {
	private boolean success;//是否成功
	private String info;
	
	public Result() {}
	
	public Result(boolean success, String info) {
		super();
		this.success = success;
		this.info = info;
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	
}
