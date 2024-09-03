package com.nsn.nsnupdatesbackend.like;

import com.nsn.nsnupdatesbackend.update.UpdateMapper;
import com.nsn.nsnupdatesbackend.user.AppUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeMapper {

    private final AppUserMapper appUserMapper;
    private final UpdateMapper updateMapper;

    @Autowired
    public LikeMapper(AppUserMapper appUserMapper, UpdateMapper updateMapper) {
        this.appUserMapper = appUserMapper;
        this.updateMapper = updateMapper;
    }

    public LikeDto toLikeDto(Like like) {
        return new LikeDto(
                like.getId(),
                appUserMapper.toUserDto(like.getAppUser()),
                updateMapper.toUpdateDto(like.getUpdate())
        );
    }
}
