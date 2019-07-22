package com.pinyougou.shop.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {
    @Reference
    private SellerService sellerService;

    /**实现动态认证：根据username到数据库中查询用户
     * 与页面中输入的用户名，密码与数据库中一致，一致成功 ，不一致失败
     *
     * @param username    在页面中输入的用户名
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //角色权限列表
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        //根据用户名(id)查询用户
        TbSeller seller = sellerService.findOne(username);
        //审核通过才能通过
        if (seller!=null&&"1".equals(seller.getStatus())){
            //用户信息
            return new User(username,seller.getPassword(),authorities);
        }

        return null;
    }
}
