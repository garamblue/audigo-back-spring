package com.audigo.audigo_back.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.audigo.audigo_back.dto.test.JoinDTO;
import com.audigo.audigo_back.service.implement.JoinService;

@Controller
@ResponseBody
public class MainController {
    // log analysis //
    private static final Log logger = LogFactory.getLog(MainController.class);

    private final JoinService joinService;

    public MainController(JoinService joinService) {
        this.joinService = joinService;
    }

    @PostMapping("/join")
    public String joinProcess(JoinDTO joinDTO) {
        logger.debug("joinProcess: " + joinDTO);
        joinService.joinProcess(joinDTO);
        return "ok";
    }

    @GetMapping("/main")
    public String mainP() {
        logger.info("Hello this is Audigo's backend API service");
        return "Hello this is Audigo's backend API service ~ !";
    }

}