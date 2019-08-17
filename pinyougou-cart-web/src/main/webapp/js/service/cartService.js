//购物车服务层
app.service('cartService',function($http){
	//查询购物车列表
	this.findCartList=function(){
		return $http.get('cart/findCartList.do');		
	}
	
	//添加商品到购物车
	this.addGoodsToCartList=function(itemId,num){
		return $http.get('cart/addGoodsToCartList.do?itemId='+itemId+'&num='+num);
	}
	
	//求合计
	this.sum=function(cartList){//cartList 购物车列表		
		var totalValue={totalNum:0, totalMoney:0.00 };//合计实体
		for(var i=0;i<cartList.length;i++){//循环商家购物车
			var cart=cartList[i];
			for(var j=0;j<cart.orderItemList.length;j++){//循环该商家所有商品
				var orderItem=cart.orderItemList[j];//购物车明细
				totalValue.totalNum+=orderItem.num;
				totalValue.totalMoney+= orderItem.totalFee;
			}				
		}
		return totalValue;
	}

});
