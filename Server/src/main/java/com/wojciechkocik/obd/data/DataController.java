package com.wojciechkocik.obd.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Wojciech Kocik
 * @since 14.04.2017
 */
@Slf4j
@RestController
@RequestMapping("/data")
public class DataController {

    private final DataService dataService;

    @Autowired
    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @PostMapping
    public void store(@RequestBody ObdData obdData) {
        dataService.storeData(obdData);
    }
}
