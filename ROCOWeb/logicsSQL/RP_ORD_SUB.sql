create or replace view RP_SALE_ITEM_PROG as
select t.*,(CASE WHEN SAC.OVER_TIME IS NULL THEN 0  WHEN T.DURATION_total<(SAC.OVER_TIME*3600000) THEN 0 ELSE 1  END) AS is_Expired,(CASE WHEN err.id is null THEN 0 ELSE 1 END) as is_err,err.err_type,err.err_desc from (
select t1.*,to_char(t1.act_end_time,'yyyy-mm-dd') as act_end_date,
FLOOR(t1.DURATION_total / 86400000) || '天' ||
       FLOOR(MOD(t1.DURATION_total, 86400000) / 3600000) || '小时' ||
       FLOOR(MOD(t1.DURATION_total, 3600000) / 60000) || '分钟' ||
       FLOOR(MOD(t1.DURATION_total, 6000) / 1000) || '秒' AS DURATION_TIME,
t2.DQ_ACT_ID ,t2.DQ_ACT_NAME
 from (
select sh.order_code,
       si.posex,
       sh.order_date,
       sh.create_time,
       sh.create_user,
       mh.draw_type,
       mh.zzymfs,
       mh.zzymss,
       mh.zztyar,
       mh.zzzkar,
       mh.serial_number,
       mh.maktx,
       ch.name1 as kunnr_name,
       acm.procinstid,
       aha.act_id_,
       aha.act_name_,
       aha.act_start_time,
       aha.act_end_time,
       aha.duration_total,--环节处理累计时间
       aha.redo_time,--环节重做次数
       aha.assignee_
  from sale_header sh, sale_item si,material_head mh,cust_header ch,act_ct_mapping acm,(
  select aha.proc_inst_id_,aha.act_id_,aha.act_name_,aha.assignee_, min(aha.start_time_) as act_start_time,
       max(aha.end_time_) as act_end_time,count(1) as redo_time,sum(aha.duration_) as duration_total from act_hi_actinst aha where
	   --去除分配任务
aha.act_id_ not in ('auto_exclusivegateway1','exclusivegateway1','exclusivegateway2','exclusivegateway3','exclusivegateway4','exclusivegateway5','exclusivegateway6','exclusivegateway7')
 group by aha.proc_inst_id_,aha.act_id_,aha.act_name_,aha.assignee_
  ) aha
 where si.pid = sh.id
       and mh.id=si.material_head_id
       and ch.kunnr=sh.shou_da_fang
       and acm.id=si.id
       and aha.proc_inst_id_=acm.procinstid
       )t1 ,(
       SELECT PROC_INST_ID_ AS PROCINSTID,
               ACT_ID_       AS DQ_ACT_ID,
               ACT_NAME_     AS DQ_ACT_NAME,
               DRANK
          FROM (SELECT T.PROC_INST_ID_,
                       T.ACT_ID_,
                       T.ACT_NAME_ ,RANK() OVER(PARTITION BY T.PROC_INST_ID_ ORDER BY T.START_TIME_ DESC) AS DRANK
                  FROM ACT_HI_ACTINST T
                 WHERE T.ACT_TYPE_ NOT IN ('exclusiveGateway', 'startEvent')) A
         WHERE DRANK = 1
       )t2--将当前环节取出来
	   where t1.procinstid=t2.PROCINSTID)t 
	   left join sys_act_conf sac on t.act_id_=sac.act_id --与sys_act_conf 相连，取出环节超期规则
	   left join act_ct_ord_err err on (t.procinstid=err.procinstid and t.act_id_=err.target_task_definition_key)--取出环节出错信息
	   ;