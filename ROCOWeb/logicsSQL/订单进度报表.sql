CREATE OR REPLACE VIEW MV_ORD_RP_PRO_VIEW AS
SELECT V."SH_ID",
       V."ORDER_CODE",
       V."SAP_ORDER_CODE",
       V."ORDER_TYPE",
       V."ORDER_DATE",
       V."CREATE_USER",
       V."CREATE_TIME",
       V."ORDER_STATUS",
       V."IS_QX",
       V."DIAN_MIAN_TEL",
       V."KUNNR",
       V."IS_YP",
       V."KUNNR_NAME",
       V."CUS_NAME",
       V."CUS_TEL",
       V."CUS_ADDR",
       V."DQ_ACT_NAME",
       V."DQ_ACT_ID",
       V."ACT_NAME",
       V."ACT_ID",
       V."ASSIGNEE",
       V."REDO_TIME",
       V."MIN_START_TIME",
       V."MAX_END_TIME",
       V."ACT_END_DATE",
       V."DURATION",
       V."DURATION_TIME",
       V."TOTAL_ITEM",
       V."GT_SUM",
       V."DDYM_SUM",
       V."TYMJ",
       V."YMSS",
       V."YMFS",
       V."IS_CHAOQI",
       (V.TOTAL_ITEM - V.gt_sum - V.ddym_sum) as sj_sum --散件数量 =总行数-柜体数-单独移门数量
       ,
       decode(v.is_chaoqi, 1, '是', '否') as is_Expired, --是否超期的文字描述
              decode(v.is_yp, 1, '是', '否') as is_yb --是否样品的文字描述
  FROM (SELECT H.ID as sh_id,
               H.ORDER_CODE,
               H.SAP_ORDER_CODE,
               H.ORDER_TYPE,
               H.ORDER_DATE,
               H.CREATE_USER,
               H.CREATE_TIME,
               DECODE(H.ORDER_STATUS, 'QX', '取消', '正常') ORDER_STATUS,
               DECODE(H.ORDER_STATUS, 'QX', 1, 0) as is_qx,
               H.DIAN_MIAN_TEL, --店面电话
               H.SHOU_DA_FANG as kunnr, --售达方
               H.IS_YP, --是否样板
               (SELECT CUST.NAME1
                  FROM CUST_HEADER CUST
                 WHERE CUST.KUNNR = H.SHOU_DA_FANG) as kunnr_name, --售达方名称
               TC.name1 as cus_name, --客户姓名
               tc.tel as cus_tel, --联系电话
               tc.address as cus_addr, --客户地址
               cur.act_id_ as dq_act_id,
               cur.act_name_ as dq_act_name,
               /*(SELECT HA.ACT_NAME_
                    FROM ACT_HI_ACTINST HA
                   WHERE HA.ACT_TYPE_ NOT IN ('exclusiveGateway', 'startEvent')
                     AND HA.START_TIME_ =
                         (SELECT MAX(AHA.START_TIME_)
                            FROM ACT_HI_ACTINST AHA
                           WHERE AHA.PROC_INST_ID_ = HA.PROC_INST_ID_)
                     AND HA.PROC_INST_ID_ = ACM.PROCINSTID) dq_Act_Name, --当前环节名称
               (SELECT HA.act_id_
                    FROM ACT_HI_ACTINST HA
                   WHERE HA.ACT_TYPE_ NOT IN ('exclusiveGateway', 'startEvent')
                     AND HA.START_TIME_ =
                         (SELECT MAX(AHA.START_TIME_)
                            FROM ACT_HI_ACTINST AHA
                           WHERE AHA.PROC_INST_ID_ = HA.PROC_INST_ID_)
                     AND HA.PROC_INST_ID_ = ACM.PROCINSTID) as dq_act_id, --当前环节名称*/
               ACT.act_name_ as act_name, --订单环节
               ACT.act_id_ as act_id, --订单环节
               ACT.assignee_ as assignee, --环节处理人
               ACT.redo_time, --处理次数
               ACT.min_start_time, --环节开始时间
               ACT.max_end_time, --环节结束时间
               to_char(ACT.max_end_time, 'yyyy-mm-dd') as act_end_date, --环节结束日期
               ACT.duration, --环节处理时间
               FLOOR(ACT.DURATION / 86400000) || '天' ||
               FLOOR(MOD(ACT.DURATION, 86400000) / 3600000) || '小时' ||
               FLOOR(MOD(ACT.DURATION, 3600000) / 60000) || '分钟' ||
               FLOOR(MOD(ACT.DURATION, 6000) / 1000) || '秒' AS DURATION_TIME, --处理时间，换算出来 XX天 XX小时 XX分钟XX秒
               (SELECT COUNT(1) FROM sale_item SL WHERE SL.PID = H.ID) total_Item, --总行数

               MM.GT_SUM, --柜体数量
               --(MM.TOTAL_ITEM - t4.gt_sum - t4.ddym_sum) as sj_sum--散件数量 =总行数-柜体数-单独移门数量
               MM.DDYM_SUM, --单独移门数量
               MM.tymj, --投影面积
               MM.ymss, --移门扇数
               MM.ymfs, --移门方数

               (SELECT CASE
                         WHEN AC.OVER_TIME IS NULL THEN
                          0
                         WHEN ACT.DURATION < (AC.OVER_TIME * 3600000) THEN
                          0
                         ELSE
                          1
                       END
                  FROM sys_act_conf AC
                 WHERE AC.ACT_ID = ACT.act_id_) is_chaoqi --是否超期

          FROM SALE_HEADER H,
               terminal_client TC,
               (select aha.proc_inst_id_, --流程实例
                       aha.act_id_, --流程节点id
                       aha.act_name_, --流程节点名称
                       aha.assignee_, --流程处理人
                       min(aha.start_time_) as min_start_time,
                       max(aha.end_time_) as max_end_time,
                       count(1) as redo_time, --重复操作次数
                       sum(aha.duration_) as duration --重复操作累计操作时间
                  from act_hi_actinst aha
                 where aha.act_id_ not in
                       ('auto_exclusivegateway1',
                        'exclusivegateway1',
                        'exclusivegateway2',
                        'exclusivegateway3',
                        'exclusivegateway4',
                        'exclusivegateway5',
                        'exclusivegateway6',
                        'exclusivegateway7')
                 group by aha.proc_inst_id_,
                          aha.act_id_,
                          aha.act_name_,
                          aha.assignee_) ACT,
               (SELECT S.PID,
                       sum(nvl(MH.zztyar,0)) tymj, --投影面积
                       sum(nvl(MH.zzymfs,0)) ymfs, --移门方数
                       sum(nvl(MH.zzymss,0)) ymss, --移门扇数
                       sum(nvl(MH.zzzkar,0)) zkm,
                       sum(CASE
                             WHEN mh.DRAW_TYPE IN (1, 2, 3) THEN
                              1
                             ELSE
                              0
                           END) AS GT_SUM, --绘图类型为工厂2020、IMOS绘图、客户2020的则定义为柜体
                       SUM(CASE
                             WHEN mh.DRAW_TYPE = 5 THEN
                              1
                             ELSE
                              0
                           END) AS DDYM_SUM
                  FROM SALE_ITEM S, MATERIAL_HEAD MH
                 WHERE S.MATERIAL_HEAD_ID = MH.ID(+)
                 GROUP BY S.PID) MM,
               act_ct_mapping ACM,
               act_ord_curr_node4 cur
         WHERE H.ID = TC.SALE_ID
           AND ACM.PROCINSTID = ACT.proc_inst_id_
           AND MM.PID = H.ID
           AND ACM.ID = H.ID
           and cur.id = acm.id) V /* WHERE V.ORDER_CODE='LJ31104160012'*/
;
