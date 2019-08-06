app.service("contentService",function($http){
	//根据广告分类id查询content表中对应广告
	this.findByCategroyId = function(categroyId){
		return $http.get("../content/findByCategroyId.do?categroyId=" + categroyId);
	}
});