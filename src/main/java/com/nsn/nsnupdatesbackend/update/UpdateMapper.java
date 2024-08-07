package com.nsn.nsnupdatesbackend.update;

import com.nsn.nsnupdatesbackend.user.AppUserDto;
import org.springframework.stereotype.Service;

@Service
public class UpdateMapper {
    public UpdateDto toUpdateDto(Update update, AppUserDto appUserDto) {
        return new UpdateDto(update.getContent(), update.getCreatedAt(), appUserDto);
    }
}
