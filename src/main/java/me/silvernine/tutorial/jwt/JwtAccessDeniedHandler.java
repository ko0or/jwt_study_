package me.silvernine.tutorial.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*

    #. 403 에러 리턴하기 위한 클래스 파일 작성
     -> 필요한 권한없이 접근하려 할때 ( 로그인은했지만, 필요한 권한이 없는경우 )


*/

@Slf4j
@Service
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        log.debug("403에러 발생 !! -> 로그인은했지만, 필요한 권한이 없는경우");
    }


}