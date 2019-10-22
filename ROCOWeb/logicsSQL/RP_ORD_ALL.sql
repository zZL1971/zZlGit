CREATE OR REPLACE VIEW ORD_RP_PRO_VIEW AS
SELECT T.*,
       (CASE
         WHEN SAC.OVER_TIME IS NULL THEN
          0
         WHEN t.DURATION < (SAC.OVER_TIME * 3600000) THEN
          0
         ELSE
          1
       END) AS is_Expired
  FROM (SELECT
        --售达方编号
         T1.KUNNR,
         --售达方名称
         T1.KUNNR_NAME,
         --店面电话
         T1.DIAN_MIAN_TEL,
         --订单是否为样品 1->是 0—>不是
         T1.IS_YP,
         --订单日期
         T1.ORDER_DATE,
         --订单编号
         T1.ORDER_CODE,
         --订单类型
         T1.ORDER_TYPE,
         --订单状态
         T1.ORDER_STATUS,
         --订单创建人
         T1.CREATE_USER,
         --订单创建日期
         T1.CREATE_TIME,
         --订单是否取消 1->取消 0->正常
         T1.IS_QX,
         T1.SAP_ORDER_CODE,
         --客户名字
         T1.CUS_NAME,
         --客户电话
         T1.CUS_TEL,
         --客户地址
         T1.CUS_ADDR,
         
         T1.PROCINSTID,
         --行项目总数
         T1.TOTAL_ITEM,
         --柜体数
         T1.GT_SUM,
         --单独移门数,
         T1.DDYM_SUM,
         --投影面积
         T1.TYMJ,
         --移门方数
         T1.YMFS,
         --移门扇数
         T1.YMSS,
         T1.zkm,
         --环节ID
         --T1.ACT_ID_,
         --环节名称
         --T1.ACT_NAME_,
         --环节开始时间
         --T1.min_start_time AS ACT_START_TIME,
         --环节结束时间
         --T1.max_end_time AS ACT_END_TIME,
         --环节结束日期
         --to_char(t1.max_end_time,'yyyy-mm-dd') AS ACT_END_DATE,
         --环节处理人
         --T1.ASSIGNEE_,
         --工作毫秒数（筛选工厂日志后的）
         --T1.DURATION,
         --t1.redo_time,
         --通过之前筛选出来的工作毫秒数，进行计算出以天，小时，分钟，秒的表达方式
         --FLOOR(T1.DURATION / 86400000) || '天' ||
         --FLOOR(MOD(T1.DURATION, 86400000) / 3600000) || '小时' ||
         --FLOOR(MOD(T1.DURATION, 3600000) / 60000) || '分钟' ||
         --FLOOR(MOD(T1.DURATION, 6000) / 1000) || '秒' AS DURATION_TIME,
         --检查工作是否超期，即工作时间是否超过了3天，超过则为超期。
         --       (CASE
         --         WHEN T1.DURATION > 259200000 THEN
         --          1
         --         ELSE
         --          0
         --       END) AS IS_EXPIRED,
         --散件数=（行项目总数-柜体总数-单独移门数）
         (T1.TOTAL_ITEM - T1.GT_SUM - T1.DDYM_SUM) AS SJ_SUM,
         T2.DQ_ACT_ID,
         T2.DQ_ACT_NAME,
         t3.act_id_,
         t3.act_name_,
         t3.assignee_,
         t3.min_start_time,
         t3.max_end_time,
         t3.redo_time,
         t3.duration,
         to_char(t3.max_end_time, 'yyyy-mm-dd') AS ACT_END_DATE,
         FLOOR(t3.DURATION / 86400000) || '天' ||
         FLOOR(MOD(t3.DURATION, 86400000) / 3600000) || '小时' ||
         FLOOR(MOD(t3.DURATION, 3600000) / 60000) || '分钟' ||
         FLOOR(MOD(t3.DURATION, 6000) / 1000) || '秒' AS DURATION_TIME
          FROM ord_rp_pro_view_sub T1,
               --取得每个订单的当前环节
               (SELECT PROC_INST_ID_ AS PROCINSTID,
                       ACT_ID_       AS DQ_ACT_ID,
                       ACT_NAME_     AS DQ_ACT_NAME,
                       DRANK
                  FROM (SELECT T.PROC_INST_ID_,
                               T.ACT_ID_,
                               T.ACT_NAME_,
                               RANK() OVER(PARTITION BY T.PROC_INST_ID_ ORDER BY T.START_TIME_ DESC) AS DRANK
                          FROM ACT_HI_ACTINST T
                        --去掉开始节点和分配任务
                         WHERE T.ACT_TYPE_ NOT IN
                               ('exclusiveGateway', 'startEvent')) A
                 WHERE DRANK = 1) T2,
               (select aha.proc_inst_id_,
                       aha.act_id_,
                       aha.act_name_,
                       aha.assignee_,
                       min(aha.start_time_) as min_start_time,
                       max(aha.end_time_) as max_end_time,
                       count(1) as redo_time,--重复操作次数
                       sum(aha.duration_) as duration--重复操作累计操作时间
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
                          aha.assignee_) t3         --以每个订单每个环节的处理人为组，取出环节处理人的相关信息
         WHERE T2.PROCINSTID = T1.PROCINSTID
           and t3.proc_inst_id_ = t1.PROCINSTID) T
  LEFT JOIN sys_act_conf sac
    ON T.ACT_ID_ = SAC.ACT_ID
;






create or replace view ord_rp_pro_view_sub as
select ch.kunnr,
       ch.name1 as kunnr_name,
       sh.dian_mian_tel,
       (CASE WHEN sh.is_yp=1 THEN 1 ELSE 0 END) AS IS_YP,
       sh.order_date,
       sh.order_code,
       sh.order_type,
       sh.CREATE_USER,
       sh.create_time,
       (CASE
         WHEN sh.order_status = 'QX' THEN
          1
         ELSE
          0
       END) as is_qx,--用于筛选
       sh.SAP_ORDER_CODE,
       (CASE
         WHEN sh.ORDER_STATUS = 'QX' THEN
          '已取消'
         ELSE
          '正常'
       END) as order_status--显示字段
      ,
       acm.procinstid,
       tc.name1 as cus_name,
       tc.tel as cus_tel,
       tc.address as cus_addr,
       count(1) as total_item,
       sum(CASE
             WHEN mh.DRAW_TYPE IN (1, 2, 3) THEN
              1
             ELSE
              0
           END) AS GT_SUM,--绘图类型为工厂2020、IMOS绘图、客户2020的则定义为柜体
       SUM(CASE
             WHEN mh.DRAW_TYPE = 5 THEN
              1
             ELSE
              0
           END) AS DDYM_SUM,--绘图类型为单独移门的，则定义为单独柜体
       sum(zztyar) tymj,--投影面积
       sum(zzymfs) ymfs,--移门方数
       sum(zzymss) ymss,--移门扇数
       sum(zzzkar) zkm
  from sale_header     sh,
       cust_header     ch,
       act_ct_mapping  acm,
       sale_item       si,
       material_head   mh,
       terminal_client tc
 where sh.shou_da_fang = CH.KUNNR
   AND sh.id = si.pid
   and tc.sale_id = sh.id
   and sh.id = acm.id
   AND SI.MATERIAL_HEAD_ID = mh.id
 group by ch.kunnr,
          ch.name1,
          sh.dian_mian_tel,
          sh.is_yp,
          sh.order_code,
          sh.order_date,
          sh.order_type,
          SH.CREATE_USER,
          sh.create_time,
          sh.order_status,
          SH.SAP_ORDER_CODE
         ,
          acm.procinstid,
          tc.sale_id,
          tc.name1,
          tc.tel,
          tc.address
;
