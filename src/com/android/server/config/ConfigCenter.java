package com.android.server.config;

/**
 * Created by kiddo on 17-7-14.
 */

public class ConfigCenter {
    public static String log_level = "warn";
    public static String log_dir = "./logs";
    public static int session_expired_time = 2000000;
    public static int max_heartbeat = 3600;
    public static int max_packet_size = 10000;
    public static int min_heartbeat= 3600;
    public static long compress_threshold= 10000;
    public static int max_hb_timeout_times = 2;
    public static int connect_server_port = 30000;
    public static int gateway_server_port = 30001;
    public static int admin_server_port = 30002;
    public static int gateway_client_port = 30003;
    public static String gateway_server_net;
    public static String gateway_server_multicast;
    public static String gateway_client_multicast;
    public static int conn_work;
    public static int http_work;
    public static int push_task;
    public static int push_client;
    public static int ack_timer;
    public static int gateway_server_work;
    public static int gateway_client_work;
    public static long profile_slowly_duration;
    public static int aes_key_length= 16;
    public static String private_key;
    public static boolean useNettyPoll = false;
    public static int max_content_length = 4096 ;
    public static int default_read_timeout = 10000 ;
    public static int max_conn_per_host = 10 ;
}
