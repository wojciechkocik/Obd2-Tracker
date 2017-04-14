package com.wojciechkocik.obd.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Wojciech Kocik
 * @since 14.04.2017
 */
@Slf4j
@Service
public class DataService {

    private final DataRepository dataRepository;

    @Autowired
    public DataService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public void storeData(ObdData obdData) {
        log.info(obdData.toString());
        dataRepository.store(obdData);
    }
}
