package com.gp.framework.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// TODO: 注入业务用户 Mapper / Service，按用户名查询用户、校验状态、装配权限后返回 LoginUser
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: SysUser user = userMapper.selectByUsername(username);
        //       if (user == null) throw new UsernameNotFoundException("用户不存在");
        //       LoginUser loginUser = new LoginUser();
        //       loginUser.setUsername(user.getUserName());
        //       loginUser.setPassword(user.getPassword());
        //       loginUser.setUser(user);
        //       loginUser.setPermissions(permissionService.getMenuPermission(user));
        //       return loginUser;
        throw new UsernameNotFoundException("UserDetailsServiceImpl 未实现");
    }
}
