create or replace view sys_request_log_view as 
select t.user_name,t.role,t.url,t.request_date,avg(CASE WHEN REQ_FREQ>=100000 THEN NULL ELSE REQ_FREQ END ) AVG_REQ_FREQ from (
select srl.username as user_name,
       srl.role,
       srl.url,
       to_char(srl.request_time,'yyyy-mm-dd') as request_date,
       Floor((to_date(to_char(srl.request_Time, 'yyyy-mm-dd hh24:mi:ss'),
                          'yyyy-mm-dd hh24:mi:ss') -
                 to_date(to_char(srl.request_Last_Time,
                                  'yyyy-mm-dd hh24:mi:ss'),
                          'yyyy-mm-dd hh24:mi:ss')) * 86400000) as req_freq
  from SYS_REQUEST_LOG srl) t
  GROUP BY
          T.user_name,
          T.role,
          T.url,
          T.request_date;
