app.controller("contentController",function($scope,contentService){
	
	//存放所有广告分类，列表中一个元素对应一类广告
	//例如$scope.contentList[1] 存放的是所有首页轮播图广告
	$scope.contentList = []
	
	$scope.findByCategroyId = function(categroyId){
		contentService.findByCategroyId(categroyId).success(
			function(response){
				$scope.contentList[categroyId] = response;
			}
		);
	}
});