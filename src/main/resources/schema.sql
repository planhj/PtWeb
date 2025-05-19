use pt;

create table categories
(
    id   bigint auto_increment
        primary key,
    icon varchar(255) not null,
    name varchar(255) not null,
    slug varchar(255) not null,
    constraint UKoul14ho7bctbefv8jywp5v3i2
        unique (slug)
);

create index IDXoul14ho7bctbefv8jywp5v3i2
    on categories (slug);

create table login_history
(
    id         bigint auto_increment
        primary key,
    ip_address varchar(255) null,
    login_time datetime(6)  not null,
    login_type varchar(20)  not null,
    user_agent varchar(255) null,
    user_id    bigint       not null
);

create index IDX3lft44makrxommxm63k7xj77d
    on login_history (login_time);

create table promotion_policies
(
    id             bigint auto_increment
        primary key,
    display_name   varchar(255) not null,
    download_ratio double       null,
    slug           varchar(255) not null,
    upload_ratio   double       null,
    constraint UKcjqpe1g15outfc0u6ajvpwxoe
        unique (slug)
);

create table qrtz_calendars
(
    SCHED_NAME    varchar(120) not null,
    CALENDAR_NAME varchar(190) not null,
    CALENDAR      blob         not null,
    primary key (SCHED_NAME, CALENDAR_NAME)
);

create table qrtz_fired_triggers
(
    SCHED_NAME        varchar(120) not null,
    ENTRY_ID          varchar(95)  not null,
    TRIGGER_NAME      varchar(190) not null,
    TRIGGER_GROUP     varchar(190) not null,
    INSTANCE_NAME     varchar(190) not null,
    FIRED_TIME        bigint       not null,
    SCHED_TIME        bigint       not null,
    PRIORITY          int          not null,
    STATE             varchar(16)  not null,
    JOB_NAME          varchar(190) null,
    JOB_GROUP         varchar(190) null,
    IS_NONCONCURRENT  varchar(1)   null,
    REQUESTS_RECOVERY varchar(1)   null,
    primary key (SCHED_NAME, ENTRY_ID)
);

create index IDX_QRTZ_FT_INST_JOB_REQ_RCVRY
    on qrtz_fired_triggers (SCHED_NAME, INSTANCE_NAME, REQUESTS_RECOVERY);

create index IDX_QRTZ_FT_JG
    on qrtz_fired_triggers (SCHED_NAME, JOB_GROUP);

create index IDX_QRTZ_FT_J_G
    on qrtz_fired_triggers (SCHED_NAME, JOB_NAME, JOB_GROUP);

create index IDX_QRTZ_FT_TG
    on qrtz_fired_triggers (SCHED_NAME, TRIGGER_GROUP);

create index IDX_QRTZ_FT_TRIG_INST_NAME
    on qrtz_fired_triggers (SCHED_NAME, INSTANCE_NAME);

create index IDX_QRTZ_FT_T_G
    on qrtz_fired_triggers (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

create table qrtz_job_details
(
    SCHED_NAME        varchar(120) not null,
    JOB_NAME          varchar(190) not null,
    JOB_GROUP         varchar(190) not null,
    DESCRIPTION       varchar(250) null,
    JOB_CLASS_NAME    varchar(250) not null,
    IS_DURABLE        varchar(1)   not null,
    IS_NONCONCURRENT  varchar(1)   not null,
    IS_UPDATE_DATA    varchar(1)   not null,
    REQUESTS_RECOVERY varchar(1)   not null,
    JOB_DATA          blob         null,
    primary key (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

create index IDX_QRTZ_J_GRP
    on qrtz_job_details (SCHED_NAME, JOB_GROUP);

create index IDX_QRTZ_J_REQ_RECOVERY
    on qrtz_job_details (SCHED_NAME, REQUESTS_RECOVERY);

create table qrtz_locks
(
    SCHED_NAME varchar(120) not null,
    LOCK_NAME  varchar(40)  not null,
    primary key (SCHED_NAME, LOCK_NAME)
);

create table qrtz_paused_trigger_grps
(
    SCHED_NAME    varchar(120) not null,
    TRIGGER_GROUP varchar(190) not null,
    primary key (SCHED_NAME, TRIGGER_GROUP)
);

create table qrtz_scheduler_state
(
    SCHED_NAME        varchar(120) not null,
    INSTANCE_NAME     varchar(190) not null,
    LAST_CHECKIN_TIME bigint       not null,
    CHECKIN_INTERVAL  bigint       not null,
    primary key (SCHED_NAME, INSTANCE_NAME)
);

create table qrtz_triggers
(
    SCHED_NAME     varchar(120) not null,
    TRIGGER_NAME   varchar(190) not null,
    TRIGGER_GROUP  varchar(190) not null,
    JOB_NAME       varchar(190) not null,
    JOB_GROUP      varchar(190) not null,
    DESCRIPTION    varchar(250) null,
    NEXT_FIRE_TIME bigint       null,
    PREV_FIRE_TIME bigint       null,
    PRIORITY       int          null,
    TRIGGER_STATE  varchar(16)  not null,
    TRIGGER_TYPE   varchar(8)   not null,
    START_TIME     bigint       not null,
    END_TIME       bigint       null,
    CALENDAR_NAME  varchar(190) null,
    MISFIRE_INSTR  smallint     null,
    JOB_DATA       blob         null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint qrtz_triggers_ibfk_1
        foreign key (SCHED_NAME, JOB_NAME, JOB_GROUP) references qrtz_job_details (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

create table qrtz_blob_triggers
(
    SCHED_NAME    varchar(120) not null,
    TRIGGER_NAME  varchar(190) not null,
    TRIGGER_GROUP varchar(190) not null,
    BLOB_DATA     blob         null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint qrtz_blob_triggers_ibfk_1
        foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references qrtz_triggers (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

create index SCHED_NAME
    on qrtz_blob_triggers (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

create table qrtz_cron_triggers
(
    SCHED_NAME      varchar(120) not null,
    TRIGGER_NAME    varchar(190) not null,
    TRIGGER_GROUP   varchar(190) not null,
    CRON_EXPRESSION varchar(120) not null,
    TIME_ZONE_ID    varchar(80)  null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint qrtz_cron_triggers_ibfk_1
        foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references qrtz_triggers (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

create table qrtz_simple_triggers
(
    SCHED_NAME      varchar(120) not null,
    TRIGGER_NAME    varchar(190) not null,
    TRIGGER_GROUP   varchar(190) not null,
    REPEAT_COUNT    bigint       not null,
    REPEAT_INTERVAL bigint       not null,
    TIMES_TRIGGERED bigint       not null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint qrtz_simple_triggers_ibfk_1
        foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references qrtz_triggers (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

create table qrtz_simprop_triggers
(
    SCHED_NAME    varchar(120)   not null,
    TRIGGER_NAME  varchar(190)   not null,
    TRIGGER_GROUP varchar(190)   not null,
    STR_PROP_1    varchar(512)   null,
    STR_PROP_2    varchar(512)   null,
    STR_PROP_3    varchar(512)   null,
    INT_PROP_1    int            null,
    INT_PROP_2    int            null,
    LONG_PROP_1   bigint         null,
    LONG_PROP_2   bigint         null,
    DEC_PROP_1    decimal(13, 4) null,
    DEC_PROP_2    decimal(13, 4) null,
    BOOL_PROP_1   varchar(1)     null,
    BOOL_PROP_2   varchar(1)     null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint qrtz_simprop_triggers_ibfk_1
        foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references qrtz_triggers (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

create index IDX_QRTZ_T_C
    on qrtz_triggers (SCHED_NAME, CALENDAR_NAME);

create index IDX_QRTZ_T_G
    on qrtz_triggers (SCHED_NAME, TRIGGER_GROUP);

create index IDX_QRTZ_T_J
    on qrtz_triggers (SCHED_NAME, JOB_NAME, JOB_GROUP);

create index IDX_QRTZ_T_JG
    on qrtz_triggers (SCHED_NAME, JOB_GROUP);

create index IDX_QRTZ_T_NEXT_FIRE_TIME
    on qrtz_triggers (SCHED_NAME, NEXT_FIRE_TIME);

create index IDX_QRTZ_T_NFT_MISFIRE
    on qrtz_triggers (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME);

create index IDX_QRTZ_T_NFT_ST
    on qrtz_triggers (SCHED_NAME, TRIGGER_STATE, NEXT_FIRE_TIME);

create index IDX_QRTZ_T_NFT_ST_MISFIRE
    on qrtz_triggers (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_STATE);

create index IDX_QRTZ_T_NFT_ST_MISFIRE_GRP
    on qrtz_triggers (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_GROUP, TRIGGER_STATE);

create index IDX_QRTZ_T_N_G_STATE
    on qrtz_triggers (SCHED_NAME, TRIGGER_GROUP, TRIGGER_STATE);

create index IDX_QRTZ_T_N_STATE
    on qrtz_triggers (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP, TRIGGER_STATE);

create index IDX_QRTZ_T_STATE
    on qrtz_triggers (SCHED_NAME, TRIGGER_STATE);

create table settings
(
    id            bigint auto_increment
        primary key,
    setting_key   varchar(255) not null,
    setting_value mediumtext   not null,
    constraint UKswd05dvj4ukvw5q135bpbbfae
        unique (setting_key)
);

create table tags
(
    id   bigint auto_increment
        primary key,
    name varchar(255) not null,
    constraint UKt48xdq560gs3gap9g7jg36kgc
        unique (name)
);

create table user
(
    id                    bigint auto_increment
        primary key,
    avatar                varchar(255)   not null,
    create_at             datetime(6)    not null,
    custom_title          varchar(255)   not null,
    download_bandwidth    varchar(255)   not null,
    downloaded            bigint         not null,
    email                 varchar(255)   not null,
    invite_slot           int            not null,
    passkey               varchar(255)   not null,
    password              varchar(255)   not null,
    personal_access_token varchar(255)   not null,
    privacy_level         varchar(10)    not null,
    real_downloaded       bigint         not null,
    real_uploaded         bigint         not null,
    score                 decimal(38, 2) not null,
    seeding_time          bigint         not null,
    signature             varchar(255)   not null,
    upload_bandwidth      varchar(255)   not null,
    uploaded              bigint         not null,
    username              varchar(255)   not null,
    constraint UK2v3v0uxl1rke2bks4g123axwq
        unique (passkey),
    constraint UKob8kqyqqgmefl0aco34akdtpe
        unique (email),
    constraint UKsb8bbouer5wak8vyiiy4pf2bx
        unique (username)
);

create table peers
(
    id             bigint auto_increment
        primary key,
    download_speed bigint       not null,
    downloaded     bigint       not null,
    info_hash      varchar(255) not null,
    ip             varchar(255) not null,
    to_go          bigint       not null,
    partial_seeder bit          not null,
    passkey        varchar(255) not null,
    peer_id        varchar(255) not null,
    port           int          not null,
    seeder         bit          not null,
    seeding_time   bigint       not null,
    update_at      datetime(6)  not null,
    upload_speed   bigint       not null,
    uploaded       bigint       not null,
    user_agent     varchar(255) not null,
    user_id        bigint       null,
    constraint UKoa8l3xqdvxr898mosks3hq3cb
        unique (ip, port, info_hash),
    constraint FK77m1r08bpaft9mvughtmnfdxi
        foreign key (user_id) references user (id)
);

create index IDXmmvk33liy7j5u9e4qhxw2d7h5
    on peers (update_at);

create table torrents
(
    id                  bigint auto_increment
        primary key,
    info_hash           varchar(255)                         not null,
    user_id             bigint                               not null,
    title               varchar(255)                         not null,
    sub_title           varchar(255)                         null,
    size                bigint                               not null,
    created_at          timestamp  default CURRENT_TIMESTAMP null,
    updated_at          timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    under_review        tinyint(1) default 0                 null,
    anonymous           tinyint(1) default 0                 null,
    category_id         bigint                               null,
    promotion_policy_id bigint                               null,
    description         text                                 null,
    tag                 json                                 null,
    constraint UKhag2ej1vo8snvirb1lv4b8r4x
        unique (info_hash),
    constraint FKb10p9e9dlvqoreml2xndoc6jd
        foreign key (category_id) references categories (id),
    constraint FKke07itypyfrgk5reojxnp6v8h
        foreign key (user_id) references user (id),
    constraint FKtdw1ru0in19ubanhdf26ln2pa
        foreign key (promotion_policy_id) references promotion_policies (id)
);

create index IDX6r5kh6i4awpdlytjmm06pk22k
    on torrents (sub_title);

create index IDXd2j3h8td7682cctkv5o77b33y
    on torrents (promotion_policy_id);

create index IDXdplkaapqslelnscuunfpm9eb6
    on torrents (title);

create table transfer_history
(
    id                    bigint auto_increment
        primary key,
    actual_downloaded     bigint      not null,
    actual_uploaded       bigint      not null,
    download_speed        bigint      not null,
    downloaded            bigint      not null,
    have_complete_history bit         not null,
    last_event            smallint    not null,
    to_go                 bigint      not null,
    started_at            datetime(6) not null,
    updated_at            datetime(6) not null,
    upload_speed          bigint      not null,
    uploaded              bigint      not null,
    torrent_id            bigint      null,
    user_id               bigint      null,
    constraint UKrm5p4xv3rb2vm6psql5je94jh
        unique (user_id, torrent_id),
    constraint FKc6oy8ob3djd6sgg47u2ak36m0
        foreign key (user_id) references user (id),
    constraint FKf7slstb93706gmwbcdd7hk1s4
        foreign key (torrent_id) references torrents (id)
);

create table item_categories
(
    category_id   int auto_increment
        primary key,
    name          varchar(50)          not null,
    description   text                 null,
    display_order int        default 0 not null,
    is_active     tinyint(1) default 1 not null,
    price         long                 not null
);

create index idx_category_active
    on item_categories (is_active);

create index idx_category_order
    on item_categories (display_order);

CREATE TABLE forum_sections (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                name VARCHAR(100) NOT NULL,
                                description TEXT,
                                display_order INT DEFAULT 0,
                                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE forum_posts (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             section_id BIGINT NOT NULL,
                             user_id BIGINT NOT NULL,
                             title VARCHAR(255) NOT NULL,
                             content TEXT NOT NULL,
                             created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                             updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             view_count INT DEFAULT 0,
                             viwe       INT DEFAULT 0,

                             FOREIGN KEY (section_id) REFERENCES forum_sections(id),
                             FOREIGN KEY (user_id) REFERENCES user(id)
);


CREATE TABLE forum_comments (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                post_id BIGINT NOT NULL,
                                user_id BIGINT NOT NULL,
                                content TEXT NOT NULL,
                                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                viwe       INT DEFAULT 0,

                                FOREIGN KEY (post_id) REFERENCES forum_posts(id),
                                FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE forum_Ccomments (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                comment_id BIGINT NOT NULL,
                                user_id BIGINT NOT NULL,
                                content TEXT NOT NULL,
                                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                viwe       INT DEFAULT 0,
                                post_id BIGINT NOT NULL,

                                FOREIGN KEY (post_id) REFERENCES  forum_posts(id),
                                FOREIGN KEY (comment_id) REFERENCES forum_comments(id),
                                FOREIGN KEY (user_id) REFERENCES user(id)
);


CREATE TABLE forum_post_likes (
                                  user_id BIGINT NOT NULL,
                                  post_id BIGINT NOT NULL,
                                  liked_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                  PRIMARY KEY (user_id, post_id),
                                  FOREIGN KEY (user_id) REFERENCES user(id),
                                  FOREIGN KEY (post_id) REFERENCES forum_posts(id)
);
