package com.audigo.audigo_back.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.audigo.audigo_back.dto.test.JoinDTO;
import com.audigo.audigo_back.service.implement.JoinService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@ResponseBody
public class MainController {
    // log analysis //

    private final JoinService joinService;

    public MainController(JoinService joinService) {
        this.joinService = joinService;
    }

    @PostMapping("/join")
    public String joinProcess(JoinDTO joinDTO) {
        log.debug("joinProcess: " + joinDTO);
        joinService.joinProcess(joinDTO);
        return "ok";
    }

}