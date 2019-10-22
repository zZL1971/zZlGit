
  CREATE OR REPLACE FORCE VIEW "ROCO_BAK"."ORD_RP_VIEW_STD" ("ID", "ORDER_DATE", "ORDER_CODE", "JIAO_QI_TIAN_SHU", "PROC_INST_ID_", "ACT_ID", "START_TIME", "END_TIME", "START_ASSIGNEE", "END_ASSIGNEE") AS 
  SELECT Z.ID,Z.ORDER_DATE,Z.ORDER_CODE,Z.JIAO_QI_TIAN_SHU,Z.PROC_INST_ID_,Z.ACT_ID,Z.START_TIME,Z.END_TIME,(
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
select A.ID,A.ORDER_DATE,

       A.ORDER_CODE,
       A.JIAO_QI_TIAN_SHU,
       C.PROC_INST_ID_,
       C.ACT_ID_  act_id,
       min(C.start_time_) start_time,
       max(C.end_time_) end_time
  from sale_header a, act_ct_mapping b, ACT_HI_ACTINST C
 where a.id = b.id
   and (a.order_status <> 'QX' OR A.order_status IS NULL)
   AND A.ORDER_CODE IS not null
   AND C.PROC_INST_ID_ = B.PROCINSTID
   and C.PROC_DEF_ID_ like 'mainProductQuotation%' 
 GROUP BY A.ID,
          A.ORDER_DATE,
          A.ORDER_CODE,
          A.JIAO_QI_TIAN_SHU,
          --B.PROCINSTID,
          C.PROC_INST_ID_,
          C.ACT_ID_
          )Z;
 
