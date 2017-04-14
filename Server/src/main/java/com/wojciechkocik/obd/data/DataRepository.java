package com.wojciechkocik.obd.data;

import com.rethinkdb.net.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static com.rethinkdb.RethinkDB.*;

/**
 * @author Wojciech Kocik
 * @since 15.04.2017
 */
@Repository
public class DataRepository {

    private final Connection connection;
    private final static String table  = "points";

    @Autowired
    public DataRepository(Connection connection) {
        this.connection = connection;
    }

    public void store(ObdData obdData){
        r.table(table).insert(obdData).run(connection);
    }
}
