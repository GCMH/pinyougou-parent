package com.pinyougou.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

/**
 * @author 依风
    *    认证类
 */
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private SellerService sellerService;
	
	public SellerService getSellerService() {
		return sellerService;
	}

	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		
		System.out.println("into UserDetailsServiceImpl");
		//构建角色列表
		List<GrantedAuthority> greAuthorities = new ArrayList<GrantedAuthority>();
		greAuthorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
		
		//根据usernae查询对应商家
		TbSeller seller = sellerService.findOne(username);
		
		//System.out.println("UserDetailsServiceImpl:" + seller);
		
		if(seller != null) {//为空禁止登陆
			if(seller.getStatus().equals("1")) {//状态为审核通过才允许登陆
				return new User(username,seller.getPassword(),greAuthorities);
			}else {
				return null;
			}
		}
		return null;
		//返回角色，匹配其密码，设置其权限
		
	}

}
