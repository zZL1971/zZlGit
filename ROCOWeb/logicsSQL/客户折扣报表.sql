create or replace view cust_discount_report_view_test as
select "CI_ID","KUNNR","JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEPT","OCT","NOV","DEC","TOTAL_TOYEAR","YEAR","AREA","AREA_ID","SUB_AREA","TOTAL","SURPLUS","DISCOUNT","START_DATE","END_DATE","FD_NAME","STATUS","EXPIRED","STORE_ID","ID" from (
select id AS CI_ID,kunnr,JAN,FEB,MAR,APR,MAY,JUN,JUL,AUG,SEPT,OCT,NOV,DEC ,nvl(JAN,0)+nvl(FEB,0)+nvl(MAR,0)+nvl(APR,0)+nvl(MAY,0)+nvl(JUN,0)+nvl(AUG,0)+nvl(SEPT,0)+nvl(OCT,0)+nvl(NOV,0)+nvl(DEC,0) as total_toYear,year_ as year from (
       select
               t1.id,
               t1.kunnr,
               to_char(t2.end_time, 'mm') AS month_,
               sum(nvl(t2.minus_, 0)) AS minus_,
               to_char(t2.end_time, 'yyyy') AS year_
          from CUST_DISCOUNT_REPORT_VIEW_SUB1 t2,(select t.*,(case when status=1 then end_date else update_time end ) as really_end_time from cust_item t ) t1
         where t1.kunnr=t2.shou_da_fang and (t2.end_time between t1.start_date and t1.really_end_time)
         group by
         t1.id,
         t1.kunnr,
              to_char(t2.end_time, 'yyyy'),
                  to_char(t2.end_time, 'mm')
                  )PIVOT(
              sum(nvl(minus_,0))
              for month_
              in(
                '01' Jan,
                '02' Feb,
                '03' Mar,
                '04' Apr,
                '05' May,
                '06' Jun,
                '07' Jul,
                '08' Aug,
                '09' Sept,
                '10' Oct,
                '11' Nov,
                '12' Dec
              )
              )) t
               join CUST_DISCOUNT_REPORT_VIEW_SUB2 t2 on t.CI_ID=t2.id
;


CREATE OR REPLACE VIEW CUST_DISCOUNT_REPORT_VIEW_SUB1 AS
SELECT t3.end_time, t4.minus_, t3.sid, t3.shou_da_fang
  FROM (SELECT t2.end_time, t2.shou_da_fang, si.id AS sid
          FROM (SELECT t.end_time,
                       sh.shou_da_fang,
                       sh.order_code,
                       sh.order_status,
                       sh.id
                  FROM (select t1.*, acm.id
                          from (select proc_inst_id_,
                                       act_id_,
                                       max(ha.end_time_) as end_time
                                  from act_hi_actinst ha
                                 where act_name_ = '财务确认'
                                   and proc_inst_id_ not in
                                       (select his.proc_inst_id_
                                          from act_hi_actinst his
                                         where his.end_time_ is null
                                           and act_name_ = '财务确认'
                                           and his.proc_inst_id_ =
                                               ha.proc_inst_id_
                                           and his.act_id_ = ha.act_id_)
                                 group by proc_inst_id_, act_id_) t1
                          join act_ct_mapping acm
                            on t1.proc_inst_id_ = acm.procinstid) t
                  JOIN sale_header sh
                    ON t.id = sh.id
                 WHERE sh.order_status IS NULL) t2
          LEFT JOIN sale_item si
            ON t2.id = si.pid) t3
  LEFT JOIN (SELECT sip.sale_itemid,
                    sum(CASE
                          WHEN
                           sip.type = 'PR04' AND sip.plus_or_minus = 0 THEN
                           sip.total
                          ELSE
                           0
                        END) AS minus_
               FROM sale_item_price sip
              GROUP BY sip.sale_itemid) t4
    ON t3.sid = t4.sale_itemid
 WHERE t4.minus_ > 0;


 
 CREATE OR REPLACE VIEW CUST_DISCOUNT_REPORT_VIEW_SUB2 AS
SELECT
         PV.desc_zh_cn as area,
         PV.province_id as area_id,
         TT.sub_area,
         TT.total,
         TT.Surplus,
         TT.discount,
         TT.start_date,
         TT.end_date,
         FD.desc_zh_cn as fd_name,
         TT.status,
         TT.Expired,
         TT.store_id,
         TT.id
  FROM
  (SELECT ch.regio AS area ,
           ch.mcod3 as sub_area,
           ci.id,
           ci.total AS total,
           ci.sheng_yu AS Surplus ,
           ci.zhe_kou AS discount ,
           ci.start_date ,
           ci.end_date ,
           ci.fan_dian_name  fd_name,
           CASE WHEN ci.status=0 THEN '已注销' ELSE '正常' END AS status,
           (CASE WHEN SYSDATE>CI.END_DATE THEN '已过期' ELSE '正常' END)  AS Expired,
           ci.kunnr AS store_id
   FROM cust_header ch
   LEFT JOIN cust_item ci ON ch.id=ci.pid
   WHERE ci.total IS NOT NULL)TT left join (
    select sdd.desc_zh_cn,sdd.key_val as province_id from sys_data_dict sdd join (select id from sys_trie_tree stt where stt.key_val='KUNNR_REGION') stt_t on sdd.trie_id=stt_t.id
   ) PV on TT.area=PV.province_id left join (
    select sdd.desc_zh_cn,sdd.key_val from sys_data_dict sdd join (select id from sys_trie_tree stt where stt.key_val='FAN_DIAN_NAME') stt_t on sdd.trie_id=stt_t.id
   ) FD on TT.fd_name=FD.key_val;
