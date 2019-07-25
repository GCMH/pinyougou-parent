 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,goodsService,uploadService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//添加商品
	$scope.add=function(){				
		$scope.entity.goodsDesc.introduction = editor.html();//获取富文本编辑器内容
		
		goodsService.add($scope.entity).success(
			function(response){
				if(response.success){
					$scope.entity={};
					editor.html("");//清空富文本编辑器
					alert("添加成功！");
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	
	$scope.image_entity={url:'',color:''};
	//上传图片
	$scope.uploadFile = function(){
		alert('into uploadfile')
		uploadService.uploadFile().success(
			function(response){
				alert('into uploadfile success');
				alert(response.info);
				if(response.success){
					$scope.image_entity.url = response.info;
					alert(response.info);
				}else{
					alert(response.info);
				}
			}
		);
	}
    
	
	$scope.entity={goodsDesc:{itemImages:[]}};
	//保存图片
	$scope.add_image_entity=function(){
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}
	
});	
