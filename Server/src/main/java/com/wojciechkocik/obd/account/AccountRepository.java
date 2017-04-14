package com.wojciechkocik.obd.account;

import com.rethinkdb.net.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;

import static com.rethinkdb.RethinkDB.r;

/**
 * @author Wojciech Kocik
 * @since 15.04.2017
 */
@Repository
public class AccountRepository {


    private final Connection connection;
    private final static String table = "accounts";

    @Autowired
    public AccountRepository(Connection connection) {
        this.connection = connection;
    }

    public String store(Account account){
        HashMap run = r.table(table).insert(account).run(connection);
        ArrayList generated_keys = (ArrayList) run.get("generated_keys");
        return String.valueOf(generated_keys.get(0));
    }
}
