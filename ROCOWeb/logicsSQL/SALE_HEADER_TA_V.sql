CREATE OR REPLACE VIEW SALE_HEADER_TA_V AS
SELECT H.ID,
       H.ORDER_CODE,
       H.ORDER_CODE||'   '||H.SAP_ORDER_CODE ORDER_SAP_CODE,
       H.ORDER_TYPE,
       (SELECT V.DESC_ZH_CN
          FROM SYS_DATA_DICT_VIEW V
         WHERE V.TRIE_KEY_VAL = 'ORDER_TYPE'
           AND V.KEY_VAL = H.ORDER_TYPE) ORDER_TYPE_NAME,
       H.FU_FUAN_MONEY,
       H.FU_FUAN_COND,
       (SELECT V.DESC_ZH_CN
          FROM SYS_DATA_DICT_VIEW V
         WHERE V.TRIE_KEY_VAL = 'FU_FUAN_COND'
           AND V.KEY_VAL = H.FU_FUAN_COND) FU_FUAN_COND_NAME,
       H.SHOU_DA_FANG,
       (SELECT C.NAME1 FROM CUST_HEADER C WHERE C.KUNNR = H.SHOU_DA_FANG) CUST_NAME,
       nvl((select cjb.BILL_NAME
             from sys_job_pool_cjob_v cjb
            where cjb.zuonr = h.order_code),
           '未进入过账池') BILL_NAME
  FROM SALE_HEADER H
/*财务快速确认审批 add Lrz 2016-04-14*/
;
