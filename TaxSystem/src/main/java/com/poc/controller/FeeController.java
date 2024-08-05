package com.poc.controller;

import com.poc.domain.Fee;
import com.poc.domain.StateEnum;
import com.poc.dto.DefaultResponse;
import com.poc.dto.FeeDto;
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
import java.util.Random;

@Api(tags = "Tax System - Fee")
@RestController
@RequestMapping(value="/fee")
@Slf4j
public class FeeController {

    @Autowired
    FeeService feeService;


    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Insert Fee")
    public ResponseEntity<DefaultResponse> insert(@RequestBody FeeDto feeDto) {

        Fee fee = null;
        String errorMessage = "";
        try{
            StateEnum stateEnum = StateEnum.toEnum(feeDto.getState());
            fee = feeService.insert(stateEnum, feeDto.getYear(), feeDto.getValue());
        }catch (Exception error){
            errorMessage = error.getMessage();
        }
        return ResponseEntity.ok(new DefaultResponse(errorMessage, fee));
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ApiOperation(value = "Remove Fee")
    public ResponseEntity<DefaultResponse> remove(@RequestBody FeeDto feeDto) {

        String errorMessage = "";
        try{
            StateEnum stateEnum = StateEnum.toEnum(feeDto.getState());
            feeService.remove(stateEnum, feeDto.getYear());
        }catch (Exception error){
            errorMessage = error.getMessage();
        }
        return ResponseEntity.ok(new DefaultResponse(errorMessage, null));
    }


    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "Get all Fee")
    public ResponseEntity<List<Fee>> getAll() {
        return ResponseEntity.ok(feeService.getAll());
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ApiOperation(value = "Populate the Fee List")
    public ResponseEntity<DefaultResponse> populateFeeList() {

        Random random = new Random();

        for(StateEnum stateEnum :StateEnum.values()){
            for(int year=2010;year<2015;year++){
                feeService.insert(stateEnum,year,Double.valueOf(String.format("%04d", random.nextInt(10000))));
            }
        }
        return ResponseEntity.ok(new DefaultResponse("list fee created",null));
    }
}