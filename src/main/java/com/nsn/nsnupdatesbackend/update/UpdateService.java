package com.nsn.nsnupdatesbackend.update;

import com.nsn.nsnupdatesbackend.user.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UpdateService {

    private final UpdateRepository updateRepository;
    private final UpdateMapper updateMapper;

    @Autowired
    public UpdateService(UpdateRepository updateRepository, UpdateMapper updateMapper) {
        this.updateRepository = updateRepository;
        this.updateMapper = updateMapper;
    }

    public List<UpdateDto> getAllUpdates() {
        return updateRepository.findAll().stream().map(updateMapper::toUpdateDto).collect(Collectors.toList());
    }

    public void savePost(Update update) {
        updateRepository.save(update);
    }
}
