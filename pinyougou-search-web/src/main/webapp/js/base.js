//不带分页功能
var app = angular.module("pinyougou",[]);
//过滤器
app.filter("trustHtml",["$sce",function($sce){
	return function(data){//传入参数为过滤的内容，即按文本形式原样显示的html
		return $sce.trustAsHtml(data);//返回原样html，即解析html语义显示的html
	}
}]);