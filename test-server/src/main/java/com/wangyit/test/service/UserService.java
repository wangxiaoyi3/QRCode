package com.wangyit.test.service;

import com.wangyit.test.base.BaseResult;
import com.wangyit.test.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2022-07-30
 */
public interface UserService extends IService<User> {

    BaseResult<?> login(User user);

    BaseResult<?> getUserInfoByToken(String token);

    BaseResult<?> getUserInfoByUserId(Long userId);

}
