package com.gp.framework.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

// TODO: 把 user 字段替换成你的业务用户实体（例如 SysUser），并补充 password/username 的取值来源
@Data
public class LoginUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    private String token;
    private Integer code;
    private String product;
    private Long loginTime;
    private Long expireTime;
    private String ipaddr;
    private String loginLocation;
    private String browser;
    private String os;
    private String username;
    private String password;
    private Set<String> permissions;

    // TODO: 替换 Object 为业务用户实体类型
    private Object user;

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
}
