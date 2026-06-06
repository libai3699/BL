package com.gp.framework.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginBody {

    private String username;

    private String password;

    private Integer googleCode;

    private String uuid = "";
}
