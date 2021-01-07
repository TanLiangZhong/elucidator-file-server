package com.sword.elucidator.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Servlet 相关操作
 *
 * @author Tan
 * @version 1.0, 2020/11/4 15:23
 */
public class ServletUtils {

    public static HttpServletRequest getRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes()).map(it -> ((ServletRequestAttributes) it).getRequest()).orElse(null);
    }

    public static HttpServletResponse getResponse() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes()).map(it -> ((ServletRequestAttributes) it).getResponse()).orElse(null);
    }

}
