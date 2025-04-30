package com.nsn.nsnupdatesbackend.wsAuth;

import com.nsn.nsnupdatesbackend.enums.EJwtToken;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import com.nsn.nsnupdatesbackend.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class WsAuthService {

    private final AppUserService appUserService;
    private final JWTUtils jwtUtils;

    @Autowired
    public WsAuthService(AppUserService appUserService, JWTUtils jwtUtils) {
        this.appUserService = appUserService;
        this.jwtUtils = jwtUtils;
    }

    public WsAuthDto refreshWsToken(String username) {
        AppUser appUser = appUserService.getUserByUsername(username);

        String token = appUser.getWsToken();

        if (token == null || jwtUtils.isExpired(token)) {
            token = jwtUtils.createToken(username, EJwtToken.WS_TOKEN, appUser.getRole());
            appUser.setWsToken(token);
            appUserService.saveUser(appUser);
        }

        return new WsAuthDto(token);
    }
}
