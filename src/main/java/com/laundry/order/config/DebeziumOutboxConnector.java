//package com.laundry.order.config;
//
//import io.debezium.config.Configuration;
//import io.debezium.embedded.EmbeddedEngine;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.stereotype.Component;
//
//
//@Component
//@Log4j2
//@RequiredArgsConstructor
//public class DebeziumOutboxConnector {
//  private EmbeddedEngine engine;
//
//  @PostConstruct
//  public void start() {
//    // Cấu hình Debezium Embedded Engine
//    Configuration config = Configuration.create()
//      // Cấu hình connector
//      .with("name", "outbox-connector")
//      .with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
//      .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
//      .with("offset.storage.file.filename", "/tmp/offsets.dat")
//      .with("offset.flush.interval.ms", "60000")
//
//      // Cấu hình kết nối MySQL
//      .with("database.hostname", "localhost")
//      .with("database.port", "3306")
//      .with("database.user", "debezium")
//      .with("database.password", "dbz")
//      .with("database.server.id", "1")
//      .with("database.server.name", "mysql-server")
//      .with("database.include.list", "outboxdb")
//      .with("table.include.list", "outboxdb.outbox_events")
//      .with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
//      .with("database.history.file.filename", "/tmp/dbhistory.dat")
//
//      // Cấu hình outbox
//      .with("transforms", "outbox")
//      .with("transforms.outbox.type", "io.debezium.transforms.outbox.EventRouter")
//      .with("transforms.outbox.table.field.event.id", "id")
//      .with("transforms.outbox.table.field.event.key", "aggregateid")
//      .with("transforms.outbox.table.field.event.type", "eventtype")
//      .with("transforms.outbox.table.field.event.payload", "payload")
//      .with("transforms.outbox.route.by.field", "aggregatetype")
//      .build();
//  }
//}
