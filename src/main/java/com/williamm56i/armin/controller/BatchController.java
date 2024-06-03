package com.williamm56i.armin.controller;

import com.williamm56i.armin.service.BatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "批次入口")
@RestController
public class BatchController {

    @Autowired
    BatchService batchService;

    @Operation(summary = "執行批次")
    @GetMapping(value = "/executeJob")
    public long executeJob(@RequestParam(value = "beanName") String beanName) throws Exception {
        return batchService.executeJob(beanName);
    }

    @Operation(summary = "更新批次流程")
    @GetMapping(value = "/reloadJobFlow")
    public String reloadJobFlow(@RequestParam("beanName") String beanName) {
        return batchService.reloadJobFlow(beanName);
    }
}
