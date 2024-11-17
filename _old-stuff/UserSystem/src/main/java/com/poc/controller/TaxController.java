package com.poc.controller;

import com.poc.domain.Fee;
import com.poc.service.FeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "Tax System - Tax maintenance")
@RestController
@RequestMapping(value="/")
@Slf4j
public class TaxController {

    @Autowired
    FeeService feeService;

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "Get all Fee")
    public ResponseEntity<List<Fee>> getAll() {
        return ResponseEntity.ok(feeService.getAll());
    }

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Insert Fee")
    public  ResponseEntity<Fee> insert(@RequestBody Fee fee) {
        return ResponseEntity.ok(feeService.insert(fee.getStateEnum(), fee.getYear(), fee.getValue()));
    }
}
