package org.example.psychologyback.unit;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.example.psychologyback.config.JwtConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
@Component
public class JwtTokenUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static final String ISSUER = "dashing";

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        JwtTokenUtil.applicationContext = applicationContext;
    }

    private static JwtConfig getJwtConfig() {
        return applicationContext.getBean(JwtConfig.class);
    }

    // 生成token的方法
    public static String generateToken(long userId, String username, Integer roleType) {
        try {
            // 获取jwt的配置
            JwtConfig jwtConfig = getJwtConfig();
            // 生成签名的算法
            Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
            // 生成过期时间
            Date expiration = new Date(System.currentTimeMillis() + jwtConfig.getExpiration());

            String token = JWT.create()
                    .withClaim("userId", userId)
                    .withClaim("username", username)
                    .withClaim("roleType", roleType)
                    .withExpiresAt(expiration) // 设置过期时间
                    .withIssuedAt(new Date()) // 设置签发时间
                    .withIssuer(ISSUER) // 设置签发者
                    .sign(algorithm);
            return token;
        } catch (Exception e) {
            throw new RuntimeException("生成token失败: " + e);
        }

    }

    //返回token
    public static String extractTokenFromRequest(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String token = request.getHeader("token");
        if (StringUtils.hasText(token)) {
            return token;
        }
        return null;
    }

    //token用户信息的提取
    public static TokenVerificationResult vaildateToken(String token) {
        DecodedJWT jwt = verifyToken(token);
        Long userid = jwt.getClaim("userId").asLong();
        String username = jwt.getClaim("username").asString();
        Integer roleType = null;
        try {
            roleType = jwt.getClaim("roleType").asInt();
        } catch (Exception e) {
            String roleTypeStr = jwt.getClaim("roleType").asString();
            if (StringUtils.hasText(roleTypeStr)) {
                roleType = Integer.valueOf(roleTypeStr);
            }
        }
        if (userid != null && StringUtils.hasText(username) && roleType != null) {
            return new TokenVerificationResult(userid, username, roleType, true);
        }
        return null;
    }

    //验证token有效性
    public static DecodedJWT verifyToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new JWTVerificationException("token不可以为空");
        }
        JwtConfig jwtConfig = getJwtConfig();
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
        return verifier.verify(token);
    }

    //token验证后提取用户信息封装类
    @Getter
    public static class TokenVerificationResult {
        private final long userId;
        private final String username;
        private final Integer roleType;
        private final boolean valid;

        public TokenVerificationResult(long userId, String username, Integer roleType, boolean valid) {
            this.userId = userId;
            this.username = username;
            this.roleType = roleType;
            this.valid = valid;
        }
    }

    //获取当前token
    public static String getCurrentToken() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            String token = (String) request.getAttribute("jwtToken");
            if (token != null) {
                return token;
            }
            //获取请求头里的Token
            String headerToken = extractTokenFromRequest(request);
            return headerToken;
        }
        return null;
    }

    //获取当前用户Id
    public static Long getCurrentUserId() {
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        Long userId = jwt.getClaim("userId").asLong();
        return userId;
    }

}
