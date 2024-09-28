package com.nsn.nsnupdatesbackend.update;

import com.nsn.nsnupdatesbackend.user.AppUser;
import org.springframework.stereotype.Service;

@Service
public class UpdateMapper {

    public UpdateDto toUpdateDto(Update update, boolean hasUserLiked) {
        return new UpdateDto(update.getId(), update.getContent(), update.getCreatedAt(), update.getNumberOfLikes(),
                toUpdatePosterDto(update.getAppUser()), update.getIsEdited(), update.getEditedAt(), hasUserLiked);
    }

    private UpdatePosterDto toUpdatePosterDto(AppUser user) {
        return new UpdatePosterDto(user.getUsername(), user.getDisplayName());
    }
}
