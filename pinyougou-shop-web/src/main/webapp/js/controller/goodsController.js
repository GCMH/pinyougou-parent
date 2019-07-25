 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,goodsService,uploadService,itemCatService,typeTemplateService){	
	
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
		//alert('into uploadfile')
		uploadService.uploadFile().success(
			function(response){
				//alert('into uploadfile success');
				//alert(response.info);
				if(response.success){
					$scope.image_entity.url = response.info;
					//alert(response.info);
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
	
	//删除集合itemImages中图片
	$scope.remove_image_entity=function(index){
		$scope.entity.goodsDesc.itemImages.splice(index,1);
	}
	
	//查询一级分类列表
	$scope.selectItemCat1List=function(){
		
		itemCatService.findByParentId(0).success(
				
			function(response){
				//alert("into itemCatService");
				$scope.itemCat1List=response;
			}
		);
	}
	
	
	//查询二级分类
	//变量发送变化时触发，监控变量，执行函数
	$scope.$watch('entity.goods.category1Id',function(newValue,oldValue){
		$scope.itemCat3List={};//查询一级分类，三级置空，防止误显示
		itemCatService.findByParentId(newValue).success(
				function(response){
					//alert("into itemCatService");
					$scope.itemCat2List=response;
				}
			);
	});
	
	//查询三级分类
	$scope.$watch('entity.goods.category2Id',function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(
				function(response){
					//alert("into itemCatService");
					$scope.itemCat3List=response;
				}
			);
	});
	
	//查询模板ID
	$scope.$watch('entity.goods.category3Id',function(newValue,oldValue){
		itemCatService.findOne(newValue).success(
				function(response){
					//alert("into itemCatService");
					$scope.entity.goods.typeTemplateId=response.typeId;
				}
			);
	});
	
	//根据模板ID查询品牌
	$scope.$watch('entity.goods.typeTemplateId',function(newValue,oldValue){
		typeTemplateService.findOne(newValue).success(
			function(response){
				$scope.typeTemplate = response;
				$scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
			}
		);
	})
	
	
});	
