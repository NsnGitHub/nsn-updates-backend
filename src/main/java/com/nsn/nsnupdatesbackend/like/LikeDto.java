package com.nsn.nsnupdatesbackend.like;

import com.nsn.nsnupdatesbackend.update.UpdateDto;
import com.nsn.nsnupdatesbackend.user.AppUserDto;

public record LikeDto(
        Integer id,
        AppUserDto appUser,
        UpdateDto updateDto
) {
}
