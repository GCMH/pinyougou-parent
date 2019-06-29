//品牌控制
app.controller("barndController",function($scope,$http,$controller,brandService){
	
		$controller("baseController",{$scope:$scope});//类型继承baseController.js中的方法,实质是共享$scope
		//查询所有品牌
		$scope.findAll = function(){	
			//$scope.x=10;
			brandService.findAll().success(
				function(response){//执行成功，将数据填充至resopons
					$scope.brandList = response;
				}
			);
		}
		
		$scope.findPage = function(pageNum, pageSize){
			brandService.findPage(pageNum, pageSize).success(
					function(response){
						$scope.brandList=response.rows;
						$scope.paginationConf.totalItems=response.total;
					}
			);
		} 
		
		
		//添加和修改品牌信息
		$scope.save = function(){
			var saveObject = null;
			//alert("save-methodName"+methodName); //方法无法调用可能是参数名写错，可能是js代码有问题。
			if($scope.entity.id != null){ //添加时无需指定id(由数据库指定)故为空时调用添加。
				saveObject = brandService.update($scope.entity);
			}else{
				saveObject = brandService.add($scope.entity);
			}
			saveObject.success(
					function(response){
						if(response.success){
							$scope.reloadList();
						}else{
							alert(respons.info);
						}
					}
			);
		}
		
		//根据id查询单个实体
		$scope.findOne = function(id){
			//alert("findOne-id:" + id);
			brandService.findOne(id).success(
				function(response){
					$scope.entity = response;
				}
			);
		}
		
		//删除选中品牌
		$scope.delete = function(){
			//alert("into delete");
			brandService.delete($scope.selectIds).success(
				function(response){
					if(response.success){
						$scope.reloadList();
					}else{
						alert(respons.info);
					}
				}
			);
		}
		
		$scope.queryEntity = {};
		$scope.query = function(){
			//alert("into select");
			brandService.query($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage, 
				$scope.queryEntity).success(
					function(response){
						$scope.brandList=response.rows;
						$scope.paginationConf.totalItems=response.total;
					}
				);
		}
	
	});