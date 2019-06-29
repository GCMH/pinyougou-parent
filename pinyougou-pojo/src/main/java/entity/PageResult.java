package entity;

import java.io.Serializable;
import java.util.List;


/**  
* 创建时间：2019年5月8日 下午9:25:32  
* 项目名称：pinyougou-pojo  
* @author hcf  
* @version 1.0   
* @since JDK 1.8.0_201  
* 文件名称：PageResult.java  
* 说       明：  分页结果类
*/
public class PageResult implements Serializable{
	private long total;//总条数
	private List rows;//当前页记录
	public PageResult(long total, List rows) {
		super();
		this.total = total;
		this.rows = rows;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public List getRows() {
		return rows;
	}
	public void setRows(List rows) {
		this.rows = rows;
	}	
}
