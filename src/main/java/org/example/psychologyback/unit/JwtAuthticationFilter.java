package org.example.psychologyback.unit;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.example.psychologyback.DTO.response.UserLoginResponseDTO;
import org.example.psychologyback.common.ResultCode;
import org.example.psychologyback.config.SecurityConfig;
import org.example.psychologyback.enumClass.UserStatus;
import org.example.psychologyback.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JwtAuthticationFilter extends OncePerRequestFilter {
    @Resource
    UserService userService;
    @Override
protected boolean shouldNotFilter(HttpServletRequest request){
      String requestUrl = request.getRequestURI().toString();
      return SecurityConfig.isPublicPath(requestUrl);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        String requestUrl = request.getRequestURL().toString();
        String requestMethod = request.getMethod();
        System.out.println("requestUrl: " + requestUrl);
        System.out.println("requestMethod: " + requestMethod);
       String token= JwtTokenUtil.extractTokenFromRequest(request);
       if(StringUtils.hasText(token)){
          JwtTokenUtil.TokenVerificationResult vaildateresult= JwtTokenUtil.vaildateToken(token);
           if(vaildateresult != null && vaildateresult.isValid()){
            UserLoginResponseDTO.UserDetail userInfo= userService.getUserInfo(vaildateresult.getUserId());
            System.out.println("userInfo: " + userInfo);
            if(userInfo!=null && UserStatus.NORMAL.getCode().equals(userInfo.getStatus())){
                // 1. 封装权限列表
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + vaildateresult.getRoleType())
                );
                // 2. 构造认证令牌对象
                UsernamePasswordAuthenticationToken authcation = new UsernamePasswordAuthenticationToken(
                        vaildateresult.getUsername(), // 主体principal：用户名
                        null,                   // 凭证credentials：密码，JWT场景不需要，填null
                        authorities             // 权限集合
                );
                // 设置认证信息到Spring Security上下文
                SecurityContextHolder.getContext().setAuthentication(authcation);

                // 将token存储到请求属性中
                request.setAttribute("jwtToken", token);

            }
            else{
                clearSecurityContext();
                ResponseUtil.writeError(response, ResultCode.TOKEN_ACCESS_FORBIDDEN);
                return;
            }
          }
          else{
              clearSecurityContext();
              ResponseUtil.writeError(response, ResultCode.TOKEN_INVALID);
              return;
          }
       }
       else{
           clearSecurityContext();
           ResponseUtil.writeError(response, ResultCode.ACCESS_UNAUTHORIZED);
           return;
       }
       chain.doFilter(request,response);
    }

private void clearSecurityContext(){
    SecurityContextHolder.clearContext();
}

}
