package com.softalanta.batchlay.batch;

import com.softalanta.batchlay.domain.User;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserUploadProcessor implements ItemProcessor<User, User> {

    private static final Map<String, String> DEPT = new HashMap<>();

    public UserUploadProcessor(){
        DEPT.put("001", "Father");
        DEPT.put("002", "Mother");
        DEPT.put("003", "Son");
    }

    @Override
    public User process(User user) throws Exception {
        user.setDept(DEPT.get(user.getDept()));
        return user;
    }
}
