package com.nsn.nsnupdatesbackend.update;

import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/inbox/{page}")
    public ResponseEntity<List<UpdateDto>> getAllUpdatesForUserPaginated(Principal principal, @PathVariable int page) {
        return ResponseEntity.ok().body(
            updateService.getUpdatesFromInboxByUsernamePaginated(page, principal.getName())
        );
    }

    @PutMapping("/put")
    public ResponseEntity<?> putUpdate(Principal principal, @Valid @RequestBody UpdatePostReqDto updatePostReqDto) {
        UpdateDto updateDto = updateService.putPost(principal.getName(), updatePostReqDto);
        if (updatePostReqDto.id() == null) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.ok().body(updateDto);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editUpdate(Principal principal, @Valid @RequestBody UpdatePostReqDto updatePostReqDto) {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasAuthority('ROLE_GUEST')")
    @GetMapping("/{username}/{page}")
    public ResponseEntity<List<UpdateDto>> getUpdatesFromSpecifiedUser(Principal principal, @PathVariable("username") String username, @PathVariable String page) {
        return ResponseEntity.ok().body(
                updateService.getUpdatesByUsernamePaginated(principal.getName(), username, page)
        );
    }

    @PreAuthorize("hasAuthority('ROLE_GUEST')")
    @GetMapping("/{id}")
    public ResponseEntity<UpdateDto> getUpdateFromSpecifiedUser(Principal principal, @PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(
                updateService.getUpdateDtoById(principal.getName(), id)
        );
    }

}
