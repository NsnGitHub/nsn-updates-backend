package com.nsn.nsnupdatesbackend.update;

import com.nsn.nsnupdatesbackend.follow.FollowService;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
    private final FollowService followService;

    @Autowired
    public UpdateService(UpdateRepository updateRepository, UpdateMapper updateMapper, AppUserService appUserService,
                         FollowService followService) {
            this.updateRepository = updateRepository;
            this.updateMapper = updateMapper;
            this.appUserService = appUserService;
            this.followService = followService;
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

    public void createPost(String username, UpdatePostReqDto updatePostReqDto) {
        AppUser appUser = appUserService.getUserByUsername(username);
        Update newUpdate = new Update(updatePostReqDto.content(), ZonedDateTime.now(ZoneId.of("UTC")), appUser);

        updateRepository.save(newUpdate);

        System.out.println("DEBUG: ABOUT TO EXECUTE ASYNC FUNCTION");
        asyncAddUpdateToAllFollowersInbox(appUser, newUpdate);
    }

    @Async
    protected void asyncAddUpdateToAllFollowersInbox(AppUser user, Update update) {
        List<AppUser> followers = followService.getFollowersForUser(user);

        for (AppUser follower : followers) {
            List<Update> inbox = follower.getInbox();
            inbox.add(update);
            appUserService.saveUser(follower);
        }
    }

    public List<UpdateDto> getUpdatesFromInboxByUsername(String username) {
        AppUser user = appUserService.getUserByUsername(username);
        List<Update> inbox = user.getInbox();

        return inbox.stream().map(updateMapper::toUpdateDto).toList();
    }
}
