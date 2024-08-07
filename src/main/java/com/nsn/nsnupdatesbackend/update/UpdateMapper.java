package com.nsn.nsnupdatesbackend.update;

import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserDto;
import com.nsn.nsnupdatesbackend.user.AppUserMapper;
import org.springframework.stereotype.Service;

@Service
public class UpdateMapper {

    public UpdateDto toUpdateDto(Update update) {
        return new UpdateDto(update.getId(), update.getContent(), update.getCreatedAt(), update.getNumberOfLikes(), toUpdatePosterDto(update.getAppUser()));
    }

    private UpdatePosterDto toUpdatePosterDto(AppUser user) {
        return new UpdatePosterDto(user.getUsername(), user.getDisplayName());
    }
}
