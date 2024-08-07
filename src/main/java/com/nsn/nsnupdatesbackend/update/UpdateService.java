package com.nsn.nsnupdatesbackend.update;

import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UpdateService {

    private final UpdateRepository updateRepository;
    private final UpdateMapper updateMapper;
    private final AppUserService appUserService;

    @Autowired
    public UpdateService(UpdateRepository updateRepository, UpdateMapper updateMapper, AppUserService appUserService) {
        this.updateRepository = updateRepository;
        this.updateMapper = updateMapper;
        this.appUserService = appUserService;
    }

    public List<UpdateDto> getAllUpdates() {
        return updateRepository.findAll().stream().map(updateMapper::toUpdateDto).collect(Collectors.toList());
    }

    public Update getUpdateById(Integer id) {
        Optional<Update> update = updateRepository.findById(id);
        if (update.isPresent()) {
            return update.get();
        } else {
            throw new EntityNotFoundException("Update with id " + id + " not found");
        }
    }

    public void savePost(String username, UpdatePostReqDto updatePostReqDto) {
        AppUser appUser = appUserService.getUserByUsername(username);
        Update newUpdate = new Update(updatePostReqDto.content(), ZonedDateTime.now(ZoneId.of("UTC")), appUser);
        updateRepository.save(newUpdate);
    }
}
