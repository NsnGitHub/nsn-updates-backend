package com.nsn.nsnupdatesbackend.update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/update")
public class UpdateController {

    private final UpdateService updateService;

    @Autowired
    public UpdateController(UpdateService updateService) {
        this.updateService = updateService;
    }

    @GetMapping
    public ResponseEntity<List<UpdateDto>> getAllUpdatesForUser(Principal principal) {
        return ResponseEntity.ok().body(updateService.getUpdatesFromInboxByUsername(principal.getName()));
    }

    @PostMapping("/create")
    public ResponseEntity<?> postUpdate(Principal principal, @RequestBody UpdatePostReqDto updatePostReqDto) {
        updateService.createPost(principal.getName(), updatePostReqDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
