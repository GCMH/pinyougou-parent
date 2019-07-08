//品牌服务
app.service("brandService",function($http){
		this.findAll = function(){
			return $http.get("../brand/findAll.do");
		}
		
		this.findPage = function(pageNum, pageSize){
			return $http.get("../brand/findPage.do?pageNum=" + pageNum + "&pageSize=" + pageSize);
		}
		
		this.add = function(entity){
			//alert("into brandService add");
			return $http.post("../brand/add.do",entity);
		}
		
		this.update = function(entity){
			//alert("into brandService update");
			return $http.post("../brand/update.do",entity);
		}
		
		this.findOne = function(id){
			return $http.get("../brand/findOne.do?id=" + id);
		}
		
		this.delete = function(selectIds){
			return $http.get("../brand/delete.do?ids=" + selectIds);
		}
		
		this.query = function(currentPage,itemsPerPage,queryEntity){
			return $http.post("../brand/query.do?pageNum=" + currentPage + 
				  	 		  "&pageSize=" +itemsPerPage, queryEntity);
		}
		
		this.selectOptionList = function(){
			return $http.get("../brand/selectOptionList.do");
		}
	});