package com.ruoyi.controller;

import com.ruoyi.db.service.ITgInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/job")
@Slf4j
public class JobController {

    @Autowired
    private ITgInfoService dataInfoService;



    @GetMapping("/spiderData")
    public String spiderData(String date) {
        log.info("spiderData: {}", date);
        try {

        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return "fail";
        }
        return "success";
    }
}


