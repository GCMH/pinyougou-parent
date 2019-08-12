app.controller("searchController",function($scope,$location,searchService){
	
	//定义搜索对象
	//keywords关键字，category商品分类，brand品牌，brand规格,pageNo当前显示页码，pageSize当前页显示记录数,sort排序规则（升序，降序） sortField排序字段
	$scope.searchMap = {'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sort':'','sortField':''};
	
	//搜索
	$scope.search = function(){
		$scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
		searchService.search($scope.searchMap).success(
			function(response){
				$scope.resultMap = response;
				//
				buildPageLabel();//构建分页栏
			}
		);
	}
	
	buildPageLabel = function(){
		$scope.pageLabel=[];
		var beginPage = 1;
		var endPage = $scope.resultMap.totalPages;
		
		$scope.beginEllipsis = true;//开始省略号
		$scope.endEllipsis = true;//结束省略号
		
		
		
		//如果页码大于5则需要对显示页码进行处理
		if($scope.resultMap.totalPages > 5){//显示开始五页
			if($scope.searchMap.pageNo < 3){//1* 2 3 4 5
				endPage = 5;
				$scope.beginEllipsis = false;
			}else if($scope.searchMap.pageNo >= $scope.resultMap.totalPages - 2){//显示最后五页    6 7 8 9* 10
				beginPage =$scope.resultMap.totalPages - 4;
				$scope.endEllipsis = false;
			}else{//以当前页码为中心显示5页       2 3 4* 5 6 
				beginPage =$scope.searchMap.pageNo - 2;
				endPage =$scope.searchMap.pageNo + 2;
			}
		}else{//总页面小于5，不显示省略号
			$scope.beginEllipsis = false;//开始省略号
			$scope.endEllipsis = false;//结束省略号
		}
		
		//alert(beginPage + ":" + endPage);
		for(var i = beginPage; i <= endPage; i++){
			$scope.pageLabel.push(i);
		}
	}
	
	
	//添加搜索条件，key为标识项（如品牌、分类） value为具体项
	$scope.addSearchItem = function(key,value){
		
		
		if(key == 'category' || key == 'brand' || key == 'price'){//添加品牌、分类
			//alert($scope.resultMap[key] + ":" + value);
			$scope.searchMap[key] = value;
		}else{//添加规格
			$scope.searchMap.spec[key] = value;
		}
		$scope.search();//查询
	}
	
	//删除搜索条件
	$scope.deleteSearchItem = function(key){
		if(key == 'category' || key == 'brand' || key == 'price'){//添加品牌、分类
			//alert($scope.resultMap[key] + ":" + value);
			$scope.searchMap[key] = "";
		}else{//添加规格
			delete $scope.searchMap.spec[key];
		}
		$scope.search();//查询
	}
	
	//分页查询
	$scope.queryByPage = function(pageNo){
		if(pageNo < 1 || pageNo > $scope.resultMap.totalPages){
			return;
		}
		$scope.searchMap.pageNo = pageNo;
		$scope.search();//查询
	}
	
	//判断当前页是否为第一页
	$scope.isBeginPage = function(){
		if($scope.searchMap.pageNo == 1){
			return true;
		}else{
			return false;
		}
	}
	
	//判断当前页是否为最后一页
	$scope.isEndPage = function(){
		if($scope.searchMap.pageNo == $scope.resultMap.totalPages){
			return true;
		}else{
			return false;
		}
	}
	
	//排序 sort 排序规则，sortField排序字段
	$scope.sortSearch = function(sort,sortField){
		//alert(sort + ":" + sortField);
		$scope.searchMap.sort = sort;
		$scope.searchMap.sortField = sortField;
		$scope.search();//查询
	}
	
	//判断关键字是否包含品牌
	$scope.keywordsIsBrand = function(){
		
		for(var i = 0 ; i < $scope.resultMap.brandList.length; i++){
			if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)){
				return true;
			}
		}
		return false;
	}
	
	
	//接收首页传递关键字，并查询
	$scope.loadKeywords = function(){
		$scope.searchMap.keywords = $location.search()['keywords'];
		$scope.search();//查询
	}
	
});