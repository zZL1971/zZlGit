CREATE OR REPLACE VIEW MV_RP_SALE_ITEM_PROG AS
select sh.shou_da_fang,(SELECT CUST.NAME1 FROM CUST_HEADER CUST WHERE CUST.KUNNR=sh.SHOU_DA_FANG) as kunnr_Name,sh.order_code,sh.order_date,
--(CASE WHEN SAC.OVER_TIME IS NULL THEN 0  WHEN T2.DURATION<(SAC.OVER_TIME*3600000) THEN 0 ELSE 1  END) AS is_Expired,
(select (CASE WHEN SAC.OVER_TIME IS NULL THEN 0  WHEN T2.DURATION<(SAC.OVER_TIME*3600000) THEN 0 ELSE 1  END)as is_expired from sys_act_conf sac where sac.act_id=t2.act_id_ and sac.act_name=t2.act_name_) as is_expired,
       mh.draw_type,
       mh.serial_number,
       mh.maktx,
       mh.zzymfs,
       mh.zzymss,
       mh.zztyar,
       mh.zzzkar,
       cur.act_id_ as dq_act_id,
       cur.act_name_ as dq_act_name,
       t1.PID,
       t1.CREATE_USER,
       t1.CREATE_TIME,
       t1.POSEX,
       t1.MATERIAL_HEAD_ID,
       t1.PROCINSTID,
       t2.PROC_INST_ID_,
       t2.ACT_ID_ as act_id,
       t2.ACT_NAME_ as act_name,
       t2.ASSIGNEE_ as assignee,
       t2.MIN_START_TIME as act_start_time,
       (case when t2.MAX_END_TIME=date'9999-12-31' then null else t2.max_end_time end) as act_end_time,
       (case when t2.MAX_END_TIME=date'9999-12-31' then null else to_char(t2.max_end_time,'yyyy-mm-dd') end) as act_end_date,
       t2.REDO_TIME,
       t2.DURATION,
       FLOOR(t2.DURATION / 86400000) || '天' ||
       FLOOR(MOD(t2.DURATION, 86400000) / 3600000) || '小时' ||
       FLOOR(MOD(t2.DURATION, 3600000) / 60000) || '分钟' ||
       FLOOR(MOD(t2.DURATION, 6000) / 1000) || '秒' AS DURATION_TIME
       ,(CASE WHEN err.id is null THEN 0 ELSE 1 END) is_err,err.err_type,err.err_desc
  from (select si.pid,
               si.create_user,
               si.create_time,
               si.posex,
               si.material_head_id,
               (SELECT acm.procinstid
                  from act_ct_mapping acm
                 where acm.id = si.id) as procinstid
          from sale_item si) t1
 join (select
 aha.proc_inst_id_, --流程实例
                    aha.act_id_, --流程节点id
                    aha.act_name_, --流程节点名称
                    aha.assignee_, --流程处理人
                    min(aha.start_time_) as min_start_time,
                    max(nvl(aha.end_time_, date '9999-12-31')) as max_end_time,
                    count(1) as redo_time, --重复操作次数
                    sum(aha.duration_) as duration --重复操作累计操作时间,
               from act_hi_actinst aha
              where aha.act_type_ <> 'exclusiveGateway'
              group by
              aha.proc_inst_id_,
                       aha.act_id_,
                       aha.act_name_,
                       aha.assignee_)t2
    on t2.proc_inst_id_ = t1.procinstid
  join material_head mh
    on mh.id = t1.material_head_id
    join
         sale_header
    sh on sh.id=t1.pid
    join
       act_ord_curr_node5 cur
    on cur.procinstid=t1.PROCINSTID
    left join sys_act_conf sac
    on sac.act_id=t2.act_id_ and sac.act_name=t2.act_name_
    left join act_ct_ord_err err on (t2.PROC_INST_ID_=err.procinstid and t2.act_id_=err.target_task_definition_key)
;