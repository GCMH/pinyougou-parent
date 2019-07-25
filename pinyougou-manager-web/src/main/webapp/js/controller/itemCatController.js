 //控制层 
app.controller('itemCatController' ,function($scope,$controller,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;
				typeTemplateService.findOne(response.typeId).success(
					function(response_type){
						$scope.entity.typeId={id:response.id,text:response_type.name};
					}
				);
				
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  	
		$scope.entity.typeId = $scope.entity.typeId.id;//{}
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			//alert($scope.entity);
			serviceObject=itemCatService.add($scope.entity);//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
					if($scope.ls.length - 1 >= 0){
						$scope.findByParentId($scope.ls[$scope.ls.length - 1]);//重新加载父节点下级内容
					}else{
						$scope.findByParentId({parentId:0,id:0});
					}
				}else{
					alert(response.info);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					//重新查询 
					if($scope.ls.length - 1 >= 0){
						$scope.findByParentId($scope.ls[$scope.ls.length - 1]);//重新加载父节点下级内容
					}else{
						$scope.findByParentId({parentId:0,id:0});
					}
					
				}else{
					alert(response.info);
				}
				$scope.selectIds=[];
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//面包屑
	$scope.ls=[];
	//根据parentId查询商品
	$scope.findByParentId=function(entity){
		
		//判断是否加入面包屑数组
		if(entity.id != 0 && ($scope.ls.length ==0 || $scope.ls[$scope.ls.length - 1].id < entity.id))
			$scope.ls.push(entity);
		
		//当点击上级面包屑时，清除该级后续
		for(var i = $scope.ls.length - 1; i >= 0 && $scope.ls[i].id > entity.id ;i--){
			$scope.ls.splice(i,1);
		}
		
		if($scope.ls.length - 1 < 0){
			$scope.parentId = 0;
		}else{
			$scope.parentId = $scope.ls[$scope.ls.length - 1].id;
		}
		
		itemCatService.findByParentId(entity.id).success(
			function(response){
				$scope.list = response;
			}
		);
	}
	//父节点
	
	
	
	$scope.typeList={data:[]}
	//查询商品规格
	$scope.findTypeList=function(){
		
		typeTemplateService.selectOptionList().success(
			function(response){
				
				$scope.typeList={data:response};
			}			
		);
	}
	
	
    
});	
