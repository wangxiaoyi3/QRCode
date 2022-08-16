package com.wangyit.test.controller;


import com.wangyit.test.base.BaseResult;
import com.wangyit.test.entity.User;
import com.wangyit.test.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 
 * @since 2022-07-30
 */
@RestController
@RequestMapping("/api/${api.version}/user")
@Api(tags = "用户")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    @ApiOperation(value = "登录")
    public BaseResult<?> login(@RequestBody User user) {
        return userService.login(user);
    }

    @RequestMapping("/profile")
    @ApiOperation(value = "获取当前登录用户信息")
    public BaseResult<?> getUserInfoByToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        return userService.getUserInfoByToken(token);
    }

    @RequestMapping("/userInfo/{userId}")
    @ApiOperation(value = "获取用户信息")
    public BaseResult<?> getUserInfoByUserId(@PathVariable("userId") Long userId) {
        return userService.getUserInfoByUserId(userId);
    }


}
