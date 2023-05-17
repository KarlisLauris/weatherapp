package io.karlis.weatherapp.utils;

import jakarta.servlet.http.HttpServletRequest;

public class GetIP {
    public static String getIP(HttpServletRequest request) {
        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getRemoteAddr();
        }
        return remoteAddr;
    }
}
