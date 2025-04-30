package com.nsn.nsnupdatesbackend.wsAuth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping(path = "api/v1/ws")
public class WsAuthController {

    private final WsAuthService wsAuthService;

    @Autowired
    public WsAuthController(WsAuthService wsAuthService) {
        this.wsAuthService = wsAuthService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> refreshWsToken(Principal principal) {
        return ResponseEntity.ok().body(wsAuthService.refreshWsToken(principal.getName()));
    }
}
