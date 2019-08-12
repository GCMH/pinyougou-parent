app.controller("searchController",function($scope,searchService){
	
	//定义搜索对象
	//keywords关键字，category商品分类，brand品牌，brand规格
	$scope.searchMap = {'keywords':'','category':'','brand':'','spec':{}};
	
	//搜索
	$scope.search = function(){
		searchService.search($scope.searchMap).success(
			function(response){
				$scope.resultMap = response;
			}
		);
	}
	
	//添加搜索条件，key为标识项（如品牌、分类） value为具体项
	$scope.addSearchItem = function(key,value){
		
		
		if(key == 'category' || key == 'brand'){//添加品牌、分类
			//alert($scope.resultMap[key] + ":" + value);
			$scope.searchMap[key] = value;
		}else{//添加规格
			$scope.searchMap.spec[key] = value;
		}
		$scope.search();//查询
	}
	
	//删除搜索条件
	$scope.deleteSearchItem = function(key){
		if(key == 'category' || key == 'brand'){//添加品牌、分类
			//alert($scope.resultMap[key] + ":" + value);
			$scope.searchMap[key] = "";
		}else{//添加规格
			delete $scope.searchMap.spec[key];
		}
		$scope.search();//查询
	}
});