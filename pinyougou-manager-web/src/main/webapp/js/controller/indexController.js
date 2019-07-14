app.controller("indexController",function($scope,loginService){
	$scope.showLoginName = function(){
		//alert("showLoginName");
		loginService.loginName().success(
			function(response){
				$scope.loginName = response.loginName;
			}
		);
	}
});