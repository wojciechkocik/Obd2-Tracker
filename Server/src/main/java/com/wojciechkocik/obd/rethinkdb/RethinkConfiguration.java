package com.wojciechkocik.obd.rethinkdb;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Wojciech Kocik
 * @since 14.04.2017
 */
@Configuration
public class RethinkConfiguration {

    public static final String DBHOST = "localhost";

    @Bean
    public Connection connectionFactory() {

        Connection connection = RethinkDB.r.connection()
                .hostname("localhost")
                .port(57787)
                .connect();

        connection.use("example_app");

        return connection;
    }


}
