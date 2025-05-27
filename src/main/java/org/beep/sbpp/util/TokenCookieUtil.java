package org.beep.sbpp.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TokenCookieUtil {

    private static void setToken(Cookie TokenCookie, int age) {
        TokenCookie.setHttpOnly(true);
        TokenCookie.setSecure(true);
        TokenCookie.setPath("/");
        TokenCookie.setMaxAge(age);
    }

    public static void addAuthCookies(String accessToken, String refreshToken, HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        setToken(accessTokenCookie,60* 60); // 1시간

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        setToken(refreshTokenCookie,60 * 60 * 24 * 7); // 7일

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

    public static void clearAuthCookies(HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        setToken(accessTokenCookie,0); // 즉시 만료

        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        setToken(refreshTokenCookie,0); // 즉시 만료

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

    public static void refreshAuthCookies(String accessToken, HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        setToken(accessTokenCookie,60* 60); // 1시간

        response.addCookie(accessTokenCookie);
    }
}
