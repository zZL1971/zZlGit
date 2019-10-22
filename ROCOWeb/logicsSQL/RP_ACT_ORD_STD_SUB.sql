
  CREATE OR REPLACE FORCE VIEW "ROCO_BAK"."ORD_RP_VIEW_SUB" ("SH_ID", "ORDER_CODE", "ORDER_DATE", "POSEX", "JIAO_QI_TIAN_SHU", "PROC_INST_ID_", "ACT_ID", "START_TIME", "END_TIME", "TASK_ID", "START_ASSIGNEE", "END_ASSIGNEE") AS 
  SELECT Z.SH_ID,Z.ORDER_CODE,Z.ORDER_DATE,Z.POSEX,Z.JIAO_QI_TIAN_SHU,Z.PROC_INST_ID_,Z.ACT_ID,Z.START_TIME,Z.END_TIME,Z.TASK_ID ,(
SELECT ASSIGNEE_
          FROM ACT_HI_ACTINST ST
         WHERE ST.PROC_INST_ID_ = Z.PROC_INST_ID_
           AND ST.ACT_ID_ = Z.act_id
           AND ST.start_time_ = Z.start_time
) start_ASSIGNEE,(
       SELECT ASSIGNEE_
          FROM ACT_HI_ACTINST ST
         WHERE ST.PROC_INST_ID_ = Z.PROC_INST_ID_
           AND ST.ACT_ID_ = Z.act_id
           AND ST.END_TIME_ = Z.end_time
) end_ASSIGNEE from (
SELECT D.ID AS sh_id, D.ORDER_CODE, D.ORDER_DATE,A.POSEX,
D.JIAO_QI_TIAN_SHU, 
C.PROC_INST_ID_, C.ACT_ID_ AS act_id
	, MIN(C.start_time_) AS start_time, MAX(C.end_time_) AS end_time,MAX(C.task_id_) AS task_id
FROM SALE_ITEM a, act_ct_mapping b, ACT_HI_ACTINST C,SALE_HEADER D
WHERE A.id = b.id
  AND A.PID=D.ID
	AND (D.order_status <> 'QX'
		OR D.order_status IS NULL)
	AND D.ORDER_CODE IS NOT NULL
	AND C.PROC_INST_ID_ = B.PROCINSTID
  --AND substr(C.PROC_DEF_ID_,0,20)='mainProductQuotation'
	AND C.PROC_DEF_ID_ LIKE 'subProductQuotation%'
  AND c.act_id_ NOT IN('auto_exclusivegateway1','exclusivegateway1','exclusivegateway2','exclusivegateway3','exclusivegateway4','exclusivegateway5','exclusivegateway6','exclusivegateway7')
GROUP BY D.ID, D.ORDER_CODE,D.ORDER_DATE,
D.JIAO_QI_TIAN_SHU, A.POSEX,
 C.PROC_INST_ID_, C.ACT_ID_
          )Z;
 
