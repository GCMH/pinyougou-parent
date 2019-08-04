 //控制层 
app.controller('goodsController' ,function($scope,$location,$controller,goodsService,uploadService,itemCatService,typeTemplateService){	
	
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
	$scope.findOne=function(){
		var id = $location.search()['id'];
		if(id == null){
			return;
		}else{
			goodsService.findOne(id).success(
				function(response){
					$scope.entity= response;
					//商品描述
					editor.html($scope.entity.goodsDesc.introduction);
					//商品图片转换与显示
					$scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
					//扩展属性转换与显示
					$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
					//规格选项转换与显示
					$scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
					//商品规格
					for(var i = 0; i < $scope.entity.itemList.length; i++){
						//alert($scope.entity.itemList[i]);
						$scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
					}
				}
			);	
		}
					
	}
	
	//添加商品
	$scope.save=function(){				
		$scope.entity.goodsDesc.introduction = editor.html();//获取富文本编辑器内容
		
		var serverObject;
		if($scope.entity.goods.id != null){
			serverObject = goodsService.update($scope.entity)//修改
		}else{
			serverObject = goodsService.add($scope.entity)//添加
		}
		
		serverObject.success(
			function(response){
				if(response.success){
					$scope.entity={};
					editor.html("");//清空富文本编辑器
					alert("添加成功！");
					location.href="goods.html";
				}else{
					alert(response.info);
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
	$scope.query=function(page,rows){			
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
    
	
	$scope.entity={goodsDesc:{itemImages:[],specificationItems:[]}};
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
				if($location.search()['id'] == null){//如果为null则是添加操作，放在执行修改中的读取操作
					$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
				}
				
			}
		);
		
		//根据规格id查询规格具体选择
		typeTemplateService.findSpecList(newValue).success(
			function(response){
				$scope.specList = response;
			}
		);
	})
	
	//保存选择的规格选择
	$scope.updateSpecAttribute = function($event,name,value){
		//alert('123456');
		var specificationItems = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,"attributeName",name);
		if(specificationItems != null){//不为空则添加，为空则初始化且添加
			if($event.target.checked){//选中添加
				specificationItems.attributeValue.push(value);
			}else{//取消删除
				specificationItems.attributeValue.splice(specificationItems.attributeValue.indexOf(value),1);
				if(specificationItems.attributeValue.length == 0){//如果attributeValue为空，移除整条元素
					$scope.entity.goodsDesc.specificationItems.splice(
							$scope.entity.goodsDesc.specificationItems.indexOf(specificationItems),1);
				}
			}
		}else{
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
		}
	}
	
	//$scope.entity={goodsDesc:{itemImages:[],specificationItems:[{attributeName:网络,attributeValue:[3G,4g]},{attributeName:内存:[32G,64G]}]}};
	//[{spec:[{网络：2g，内存：32G}],price:0,num:9999,status:'0',isDefault:'0'}，
	//[{spec:[{网络：4g，内存：32G}],price:0,num:9999,status:'0',isDefault:'0'}]]
	//创建SKU列表
	$scope.createItemList = function(){
		//alert('into createItemList');
		$scope.entity.itemList = [{spec:{},price:0,num:9999,status:'0',isDefault:'0'}];
		var items = $scope.entity.goodsDesc.specificationItems;
		for(var i = 0; i < items.length; i++){
			$scope.entity.itemList = addRows($scope.entity.itemList, items[i].attributeName, items[i].attributeValue);
		}
	}
	
	addRows = function(items,name,values){
		var itemsList = [];
		for(var i = 0; i < items.length; i++){
			var oldRow = items[i];
			
			for(var j = 0; j < values.length; j++){
				var newRow = JSON.parse(JSON.stringify(oldRow));
				//alert(values + "--" + values[j] + "--" + j + "--" + values.length);
				newRow.spec[name] = values[j];
				//alert(JSON.stringify(newRow)+"--" + name + "--" + values[j]);
				itemsList.push(newRow);
			}
		}
		return itemsList;
	}
	
	$scope.status = ['未审核','审核通过','审核未通过','已关闭'];
	
	//分类列表，记录所有分类
	$scope.itemCatList=[];
	$scope.itemCatList = function(){
		//alert('into itemCatList');
		itemCatService.findAll().success(
				
			function(response){
				for(var i = 0; i < response.length; i++){
					$scope.itemCatList[response[i].id] =  response[i].name;
				}
			}
		);
	}
	
	//根据goodsDesc.specificationItems判断是否勾选规格选项
	$scope.checkAttributeValue = function(specName,optionName){
		//alert('into checkAttributeValue' + specName + optionName);
		var optionList = [];
		//如果可以查到规格名（如网络等）且可以查到该规格的值（如联通、移动等）返回true
		if((optionList = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',specName)) != null &&
				optionList.attributeValue.indexOf(optionName) >= 0){
			return true;
		}else{
			//alert(optionList);
			return false;
		}
	}
	
});	
