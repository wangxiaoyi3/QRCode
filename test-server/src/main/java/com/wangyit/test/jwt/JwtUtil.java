package com.wangyit.test.jwt;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.wangyit.test.entity.User;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    private static final String AUDIENCE = "admin";
    private static final String SECRET = "CCMetric";


    /**
     * 获取token
     * @param user
     * @return
     */
    public static String getToken(User user) {
        Calendar instance = Calendar.getInstance();
        // 默认令牌过期时间1天
        instance.add(Calendar.DATE, 1);

        JWTCreator.Builder builder = JWT.create();
        builder.withClaim("userId", user.getUserId());
        builder.withClaim("userName", user.getUserName());

        return builder.withAudience(AUDIENCE) // 签发对象
                .withIssuedAt(new Date()) // 发行时间
                .withExpiresAt(instance.getTime()) // 过期时间
                .sign(Algorithm.HMAC256(SECRET));
    }

    /**
     * 验证token合法性 成功返回token
     * @param token
     */
    public static DecodedJWT verify(String token) {
        JWTVerifier build = JWT.require(Algorithm.HMAC256(SECRET)).build();
        return build.verify(token);
    }

    /**
     * 根据token获取载荷信息
     * @param token token数据
     * @return
     */
    public static Map<String, Claim> getPayloadByToken(String token) {
        return verify(token).getClaims();
    }

}
