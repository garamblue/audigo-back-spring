package com.audigo.audigo_back.service.implement;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.audigo.audigo_back.dto.test.JoinDTO;
import com.audigo.audigo_back.entity.UsertestEntity;
import com.audigo.audigo_back.repository.UsertestRepository;

@Service
public class JoinService {
    private final UsertestRepository usertestRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UsertestRepository utRepository, BCryptPasswordEncoder bcrt) {
        this.usertestRepository = utRepository;
        this.bCryptPasswordEncoder = bcrt;
    }

    public void joinProcess(JoinDTO joindto) {
        String username = joindto.getUsername();
        String password = joindto.getPassword();

        Boolean isExist = usertestRepository.existsByUsername(username);

        if (isExist) {
            return;
        }

        UsertestEntity data = new UsertestEntity();

        data.setUsername(username);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setRole("ROLE_ADMIN");

        usertestRepository.save(data);
    }
}
