app.controller("itemController",function($scope,$http){

	
	$scope.num = 1;
	//数量操作
	$scope.addNum=function(x){
		$scope.num=$scope.num+x;
		if($scope.num<1){
			$scope.num=1;
		}
	}	

	$scope.specificationItems={};//记录用户选择的规格
	//用户选择规格
	$scope.selectSpecification=function(name,value){	
		$scope.specificationItems[name]=value;
		searchSku();
	}	
	
	//判断某规格选项是否被用户选中
	$scope.isSelected=function(name,value){
		if($scope.specificationItems[name]==value){
			return true;
		}else{
			return false;
		}		
	}
	
	$scope.sku = {};
	//加载默认sku，即初始化选中项
	$scope.loadSku = function(){
		$scope.sku = skuList[0];
		//将json对象转换为字符串，然后再将字符串转换为对象，实现深克隆。
		$scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
	}
	
	//匹配两个对象是否相等
	matchObject = function(obj1,obj2){
		for(var k in obj1){
			if(obj1[k] != obj2[k]){
				return false;
			}
		}
		for(var k in obj2){
			if(obj1[k] != obj2[k]){
				return false;
			}
		}
		return true;
	}
	
	//从页面skuList中查询当前选中($scope.specificationItems)的sku，然后设置sku为skuList中选中的sku，$scope.sku用于显示价格标题
	searchSku = function(){
		for(var i = 0; i < skuList.length; i++){
			if(matchObject(skuList[i].spec,$scope.specificationItems)){
				$scope.sku = skuList[i];
				return;
			}
		}
		$scope.sku={id:0,prece:0,title:'--该商品无货--'}
	}
	
	//添加商品到购物车
	$scope.addToCart=function(){
		//alert('skuid:'+$scope.sku.id);	
		$http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='+ $scope.sku.id +'&num='+$scope.num,
				{'withCredentials':true}).success(
			 function(response){
				 if(response.success){
					 location.href='http://localhost:9107/cart.html';//跳转到购物车页面
				 }else{
					 alert(response.info);
				 }					 
			 }				
		);

	}

});