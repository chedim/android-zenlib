package ru.zenmoney.library.dal;

/**
 * Created with IntelliJ IDEA.
 * User: chedim
 * Date: 16.03.13
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
public class DbInit {
    public static final String[] sql = {
            "CREATE TABLE `sms_format` (id INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT, company INTEGER, `check` TEXT, `regexp` TEXT, `columns` TEXT);",
            "create table sms_table (_id integer primary key autoincrement,sender text, text text, time_stamp integer, status integer, `transaction` integer, parsed INTEGER);",
            "create table account (_id integer primary key autoincrement,id INTEGER, archive INTEGER, balance numeric(20,4), bank INTEGER, capitalization INTEGER, card_system INTEGER, created TEXT, date_limit TEXT, date_limit_interval INTEGER, in_balance INTEGER, instrument INTEGER, payoff_period INTEGER, payoff_type INTEGER, percent REAL, static_id INTEGER, sum numeric(20,4), title TEXT collate nocase, type TEXT, user int, connection int, changed integer DEFAULT CURRENT_TIMESTAMP);",
            "create table `transaction` (_id integer primary key autoincrement,id integer,static_id integer,account_outcome integer,account_income integer,user integer,income numeric(20,4),outcome numeric(20,4),instrument_income integer,instrument_outcome integer,category integer,payee text,comment text,price real,date text,changed integer DEFAULT CURRENT_TIMESTAMP);",
            "CREATE TABLE instrument (id INTEGER PRIMARY KEY, title TEXT collate nocase, title_short TEXT, symbol TEXT, rub_rate rate REAL);",
            "create table tag (_id integer primary key autoincrement,id integer,static_id integer,title text collate nocase,user integer,changed integer DEFAULT CURRENT_TIMESTAMP);",
            "create table tag_group (_id integer primary key autoincrement,id integer,show_income integer,tag2 integer,show_outcome integer,static_id integer,tag0 integer,tag1 integer,user integer,changed integer DEFAULT CURRENT_TIMESTAMP);",
            "create table transaction_tag (_id integer primary key autoincrement,id integer,tag2 integer,`transaction` integer,tag_group integer,tag0 integer,tag1 integer,user integer, `order` integer, changed integer DEFAULT CURRENT_TIMESTAMP);",
            "create table reminderv2 (_id integer primary key autoincrement,id integer,static_id integer,user integer,account_income integeraccount_outcome integer,income real,outcome real,instrument_income integer,instrument_outcome integer,category integer,payee text,comment text,date_start text,date_end text,interval text,interval_count integer,chain_length integer,chain_bans integer,changed integer DEFAULT CURRENT_TIMESTAMP);",
            "create table reminder_marker (_id integer primary key autoincrement,id integer,user integer,date text,account_income integeraccount_outcome integer,income real,outcome real,instrument_income integer,instrument_outcome integer,category integer,state text,changed integer DEFAULT CURRENT_TIMESTAMP);",
            "create table reminder_tag (_id INTEGER PRIMARY KEY AUTOINCREMENT, id integer, user integer,reminder integer,tag_group integer,tag0 integer, tag1 integer, tag2 integer, `order` integer, changed integer DEFAULT CURRENT_TIMESTAMP);",
            "create table user (_id integer primary key autoincrement,id integer,country integer,city integer,currency integer,changed integer DEFAULT CURRENT_TIMESTAMP);",
            "CREATE TABLE connection (_id INTEGER PRIMARY KEY, id integer, user integer, company integer, account integer, state text, sync_time text, sync_interval text, sheduled integer, title text collate nocase, error_code text, changed integer DEFAULT CURRENT_TIMESTAMP);",
            "CREATE TABLE connection_data (_id INTEGER PRIMARY KEY, id integer, user integer, connection integer, name text, value text, account integer, active integer, error_text text, changed integer DEFAULT CURRENT_TIMESTAMP);",
            "INSERT INTO account (balance, sum, title, type, static_id, instrument) VALUES (0, 0, 'Наличные', 'cash', 18, 2);",
            "INSERT INTO account (balance, sum, title, type, static_id, instrument) VALUES (0, 0, 'Долги', 'debt', 3, 2);",
            "CREATE TABLE phone (_id INTEGER PRIMARY KEY, company INTEGER, number TEXT);",
            // ^^ 15th element, version 1
            "CREATE TABLE deletion (object STRING, object_id LONG, stamp LONG DEFAULT CURRENT_TIMESTAMP);",
            // ^^ 16th element, version 2
            "CREATE TABLE instrument_rate (_id INTEGER PRIMARY KEY AUTOINCREMENT, id INTEGER, date TEXT, multiplier INTEGER, rate REAL, rate_3m TEXT, source INTEGER, target INTEGER);",
            // ^^ 17th element, version 3
            "INSERT INTO instrument (id, title, title_short, symbol, rub_rate) VALUES (2, 'Российский рубль', 'RUB', 'руб.', 1);"
    };
}
