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
        --�۴﷽���
         T1.KUNNR,
         --�۴﷽����
         T1.KUNNR_NAME,
         --����绰
         T1.DIAN_MIAN_TEL,
         --�����Ƿ�Ϊ��Ʒ 1->�� 0��>����
         T1.IS_YP,
         --��������
         T1.ORDER_DATE,
         --�������
         T1.ORDER_CODE,
         --��������
         T1.ORDER_TYPE,
         --����״̬
         T1.ORDER_STATUS,
         --����������
         T1.CREATE_USER,
         --������������
         T1.CREATE_TIME,
         --�����Ƿ�ȡ�� 1->ȡ�� 0->����
         T1.IS_QX,
         T1.SAP_ORDER_CODE,
         --�ͻ�����
         T1.CUS_NAME,
         --�ͻ��绰
         T1.CUS_TEL,
         --�ͻ���ַ
         T1.CUS_ADDR,
         
         T1.PROCINSTID,
         --����Ŀ����
         T1.TOTAL_ITEM,
         --������
         T1.GT_SUM,
         --����������,
         T1.DDYM_SUM,
         --ͶӰ���
         T1.TYMJ,
         --���ŷ���
         T1.YMFS,
         --��������
         T1.YMSS,
         T1.zkm,
         --����ID
         --T1.ACT_ID_,
         --��������
         --T1.ACT_NAME_,
         --���ڿ�ʼʱ��
         --T1.min_start_time AS ACT_START_TIME,
         --���ڽ���ʱ��
         --T1.max_end_time AS ACT_END_TIME,
         --���ڽ�������
         --to_char(t1.max_end_time,'yyyy-mm-dd') AS ACT_END_DATE,
         --���ڴ�����
         --T1.ASSIGNEE_,
         --������������ɸѡ������־��ģ�
         --T1.DURATION,
         --t1.redo_time,
         --ͨ��֮ǰɸѡ�����Ĺ��������������м�������죬Сʱ�����ӣ���ı�﷽ʽ
         --FLOOR(T1.DURATION / 86400000) || '��' ||
         --FLOOR(MOD(T1.DURATION, 86400000) / 3600000) || 'Сʱ' ||
         --FLOOR(MOD(T1.DURATION, 3600000) / 60000) || '����' ||
         --FLOOR(MOD(T1.DURATION, 6000) / 1000) || '��' AS DURATION_TIME,
         --��鹤���Ƿ��ڣ�������ʱ���Ƿ񳬹���3�죬������Ϊ���ڡ�
         --       (CASE
         --         WHEN T1.DURATION > 259200000 THEN
         --          1
         --         ELSE
         --          0
         --       END) AS IS_EXPIRED,
         --ɢ����=������Ŀ����-��������-������������
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
         FLOOR(t3.DURATION / 86400000) || '��' ||
         FLOOR(MOD(t3.DURATION, 86400000) / 3600000) || 'Сʱ' ||
         FLOOR(MOD(t3.DURATION, 3600000) / 60000) || '����' ||
         FLOOR(MOD(t3.DURATION, 6000) / 1000) || '��' AS DURATION_TIME
          FROM ord_rp_pro_view_sub T1,
               --ȡ��ÿ�������ĵ�ǰ����
               (SELECT PROC_INST_ID_ AS PROCINSTID,
                       ACT_ID_       AS DQ_ACT_ID,
                       ACT_NAME_     AS DQ_ACT_NAME,
                       DRANK
                  FROM (SELECT T.PROC_INST_ID_,
                               T.ACT_ID_,
                               T.ACT_NAME_,
                               RANK() OVER(PARTITION BY T.PROC_INST_ID_ ORDER BY T.START_TIME_ DESC) AS DRANK
                          FROM ACT_HI_ACTINST T
                        --ȥ����ʼ�ڵ�ͷ�������
                         WHERE T.ACT_TYPE_ NOT IN
                               ('exclusiveGateway', 'startEvent')) A
                 WHERE DRANK = 1) T2,
               (select aha.proc_inst_id_,
                       aha.act_id_,
                       aha.act_name_,
                       aha.assignee_,
                       min(aha.start_time_) as min_start_time,
                       max(aha.end_time_) as max_end_time,
                       count(1) as redo_time,--�ظ���������
                       sum(aha.duration_) as duration--�ظ������ۼƲ���ʱ��
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
                          aha.assignee_) t3         --��ÿ������ÿ�����ڵĴ�����Ϊ�飬ȡ�����ڴ����˵������Ϣ
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
       END) as is_qx,--����ɸѡ
       sh.SAP_ORDER_CODE,
       (CASE
         WHEN sh.ORDER_STATUS = 'QX' THEN
          '��ȡ��'
         ELSE
          '����'
       END) as order_status--��ʾ�ֶ�
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
           END) AS GT_SUM,--��ͼ����Ϊ����2020��IMOS��ͼ���ͻ�2020������Ϊ����
       SUM(CASE
             WHEN mh.DRAW_TYPE = 5 THEN
              1
             ELSE
              0
           END) AS DDYM_SUM,--��ͼ����Ϊ�������ŵģ�����Ϊ��������
       sum(zztyar) tymj,--ͶӰ���
       sum(zzymfs) ymfs,--���ŷ���
       sum(zzymss) ymss,--��������
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
