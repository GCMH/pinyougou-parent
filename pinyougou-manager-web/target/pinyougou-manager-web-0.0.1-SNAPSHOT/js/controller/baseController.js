app.controller("baseController",function($scope){//父类控制器,其他控制器共享该$scope,提高代码复用
	//分页控件设置
	   $scope.paginationConf = {
			currentPage:1, 		//当前页
			totalItems:10,		//总记录数
			itemsPerPage:10,	//当前页显示记录数
			perPageOptions: [10,20,30,40,50],//页面显示记录数
			onChange:function(){
				//alert("into onChange");
				$scope.reloadList();//页面加载分页栏时调用
			}
		};
	   
	 //选中元素集合
		$scope.selectIds = [];
		$scope.selectUpdate = function($event,id){
			//alert("into selectupdate");
			if($event.target.checked){//选中品牌复选框则添加，取消则删除
				$scope.selectIds.push(id);
			}else{
				var index = $scope.selectIds.indexOf(id);
				$scope.selectIds.splice(index,1);//删除index开始删除一个元素。
			}
		}
		
		 //刷新显示
		$scope.reloadList = function(){
			//alert("into reloadList");//注意清缓存
			//当前页，当前页记录数
			$scope.query($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
		} 
		
		//jsonString = [{"id":33,"text":"电视屏幕尺寸"},{"id":34,"text":"尺寸"}]
		//jsonToString(jsonString,'text') = 电视屏幕尺寸,电视屏幕尺寸
		$scope.jsonToString = function(jsonString,key){
			var json = JSON.parse(jsonString);
			var rValue = "";
			for(var i = 0 ; i < json.length - 1; i++){
				rValue += json[i][key] + "，";
			}
			rValue += json[json.length - 1][key];
			return rValue;
		}
});