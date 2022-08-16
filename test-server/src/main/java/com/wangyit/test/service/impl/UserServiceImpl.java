package com.wangyit.test.service.impl;

import com.auth0.jwt.interfaces.Claim;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wangyit.test.base.BaseResult;
import com.wangyit.test.entity.User;
import com.wangyit.test.jwt.JwtUtil;
import com.wangyit.test.mapper.UserMapper;
import com.wangyit.test.service.QRCodeService;
import com.wangyit.test.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangyit.test.websocket.QRCodeServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2022-07-30
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public BaseResult<?> login(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", user.getUserName());
        queryWrapper.eq("pass_word", user.getPassWord());
        User _user = this.getOne(queryWrapper);
        if (null != _user) {
            String token = JwtUtil.getToken(_user);
            return BaseResult.success("验证成功").withToken(token);
        }
        return BaseResult.failed("用户名或密码错误");
    }

    @Override
    public BaseResult<?> getUserInfoByToken(String token) {
        Map<String, Claim> claimMap = JwtUtil.getPayloadByToken(token);
        Long userId = claimMap.get("userId").asLong();
        return this.getUserInfoByUserId(userId);
    }

    @Override
    public BaseResult<?> getUserInfoByUserId(Long userId) {
        User user = this.getById(userId);
        if (user != null) {
            user.setPassWord(null);
        }
        return BaseResult.success(user);
    }


}
