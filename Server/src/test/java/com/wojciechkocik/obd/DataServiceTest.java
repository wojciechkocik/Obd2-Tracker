package com.wojciechkocik.obd;

import com.wojciechkocik.obd.data.DataService;
import com.wojciechkocik.obd.data.ObdData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;

/**
 * @author Wojciech Kocik
 * @since 14.04.2017
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DataServiceTest {

    @Autowired
    private DataService dataService;

    @Test
    public void dataService() {

        ObdData obdData = new ObdData();
        obdData.setEpoch(Instant.EPOCH.toEpochMilli());
        obdData.setLabel("rpm");
        obdData.setValue(1500);
        obdData.setAccountId("04e2daa6-d235-4046-8773-5e9fe3b9d91d");

        dataService.storeData(obdData);
    }

}
