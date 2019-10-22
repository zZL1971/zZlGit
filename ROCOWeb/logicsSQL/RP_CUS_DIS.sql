create or replace view cust_discount_report_view_test as
select area,area_id,sub_area,total,Surplus,fd_name,discount,start_date,end_date,status,Expired,store_id,JAN,FEB,MAR,APR,MAY,JUN,JUL,AUG,SEPT,OCT,NOV,DEC ,nvl(JAN,0)+nvl(FEB,0)+nvl(MAR,0)+nvl(APR,0)+nvl(MAY,0)+nvl(JUN,0)+nvl(AUG,0)+nvl(SEPT,0)+nvl(OCT,0)+nvl(NOV,0)+nvl(DEC,0) as total_toYear,year_ as year from (
SELECT 
       t5.area,
       t5.area_id,
       t5.sub_area,
       t5.total,
       t5.Surplus,
       t5.discount,
       t5.start_date,
       t5.end_date,
       t5.status,
       t5.Expired,
       t5.store_id,
       t5.fd_name,
       t6.month_,
       t6.minus_,
       t6.year_
FROM
  (
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
         TT.store_id
  FROM
  (SELECT ch.regio AS area ,
           ch.mcod3 as sub_area,
           ci.total AS total,
           ci.sheng_yu AS Surplus ,
           ci.zhe_kou AS discount ,
           ci.start_date ,
           ci.end_date ,
           ci.fan_dian_name  fd_name,
           CASE WHEN ci.status=0 THEN '已注销' ELSE '正常' END AS status,
           ci.guo_qi AS Expired,
           ci.kunnr AS store_id
   FROM cust_header ch
   LEFT JOIN cust_item ci ON ch.id=ci.pid
   WHERE ci.total IS NOT NULL)TT left join (
    select sdd.desc_zh_cn,sdd.key_val as province_id from sys_data_dict sdd join (select id from sys_trie_tree stt where stt.key_val='KUNNR_REGION') stt_t on sdd.trie_id=stt_t.id
   ) PV on TT.area=PV.province_id left join (
    select sdd.desc_zh_cn,sdd.key_val from sys_data_dict sdd join (select id from sys_trie_tree stt where stt.key_val='FAN_DIAN_NAME') stt_t on sdd.trie_id=stt_t.id
   ) FD on TT.fd_name=FD.key_val 
   )t5
LEFT JOIN
  ( SELECT t7.shou_da_fang,
           to_char(t7.end_time,'mm') AS month_,
           sum(nvl(t7.minus_,0)) AS minus_,
           to_char(t7.end_time,'yyyy') AS year_
   FROM
     ( SELECT t3.end_time,
              t4.minus_,
              t3.shou_da_fang
      FROM
        ( SELECT t2.end_time,
                 t2.shou_da_fang,
                 si.id AS sid
         FROM
           ( SELECT t.end_time,
                    sh.shou_da_fang,
                    sh.order_code,
                    sh.order_status,
                    sh.id
            FROM
              (SELECT aha.end_time_ AS end_time,
                      acm.id
               FROM act_hi_actinst aha
               LEFT JOIN act_ct_mapping acm ON aha.proc_inst_id_=acm.procinstid
               WHERE aha.act_id_='usertask_valuation' ) t
            JOIN sale_header sh ON t.id=sh.id
            WHERE sh.order_status!='QX'
              OR sh.order_status IS NULL ) t2
         LEFT JOIN sale_item si ON t2.id=si.pid ) t3
      LEFT JOIN
        ( SELECT sip.sale_itemid,
                 sum(CASE WHEN sip.type='PR03'
                     OR sip.type='PR04'
                     AND sip.plus_or_minus=0 THEN sip.total ELSE 0 END) AS minus_
         FROM sale_item_price sip
         GROUP BY sip.sale_itemid ) t4 ON t3.sid=t4.sale_itemid 
      WHERE t4.minus_>0 ) t7
   GROUP BY t7.shou_da_fang,to_char(t7.end_time,'mm'),
            to_char(t7.end_time,'yyyy')) t6 ON t6.shou_da_fang=t5.store_id) PIVOT(
              sum(CASE WHEN minus_ IS NULL THEN 0 ELSE minus_ END)
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
            ) ;
            drop view cust_discount_report_view_test;