package com.softalanta.batchlay.batch;

import com.softalanta.batchlay.domain.User;
import com.softalanta.batchlay.repository.UserRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserUploadWriter implements ItemWriter<User> {


    private final UserRepository userRepository;

    @Autowired
    public UserUploadWriter(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public void write(List<? extends User> users) throws Exception {
        for (User user:users) {
            userRepository.save(user);
        }
    }
}
