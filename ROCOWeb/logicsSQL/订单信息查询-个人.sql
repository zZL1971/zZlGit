create or replace view ord_info_rp_ps as
select sh.ID as id,
               sh.CREATE_TIME,
               sh.CREATE_USER,
               sh.ROW_STATUS,
               sh.UPDATE_TIME,
               sh.UPDATE_USER,
               sh.DESIGNER_TEL,
               sh.DIAN_MIAN_TEL,
               sh.FU_FUAN_COND,
               sh.FU_FUAN_MONEY,
               sh.HANDLE_TIME,
               sh.HUO_DONG_TYPE,
               sh.JIAO_QI_TIAN_SHU,
               sh.ORDER_CODE,
               sh.ORDER_DATE,
               sh.ORDER_STATUS,
               sh.ORDER_TOTAL,
               sh.ORDER_TYPE,
               sh.PAY_TYPE,
               sh.REMARKS,
               sh.SAP_ORDER_CODE,
               sh.SHI_JI_DATE,
               sh.SHI_JI_DATE2,
               sh.SHOU_DA_FANG,
               sh.SONG_DA_FANG,
               sh.YU_JI_DATE,
               sh.IS_YP,
               sh.P_ORDER_CODE,
               sh.CHECK_DRAW_USER,
               sh.CHECK_PRICE_USER,
               sh.CONFIRM_FINANCE_USER,
               --t.act_name_ as act_name,
               t1.assignee,
               (select s1.act_id_
                  from act_ord_curr_node3 s1
                 where s1.id = sh.id
                   and rownum = 1) as act_id,
               (select s1.act_name_
                  from act_ord_curr_node4 s1
                 where s1.id = sh.id
                   and rownum = 1) as jd_name,
               (select s2.name1
                  from terminal_client s2
                 where s2.sale_id = sh.id
                   and rownum = 1) as name1,
               (select s2.tel
                  from terminal_client s2
                 where s2.sale_id = sh.id
                   and rownum = 1) as tel,
               (select s3.name1
                  from cust_header s3
                 where s3.kunnr = sh.shou_da_fang
                   and rownum = 1) as kunnr_name1
          from sale_header sh
          join (
select id,assignee_ as assignee from (
            select acm.id,aha.assignee_
                 from act_ct_mapping acm
                 join act_hi_actinst aha
                   on acm.procinstid = aha.proc_inst_id_
                 join sale_header sh
                   on sh.id=acm.id
            Union All
            select acm.id,aha.assignee_
                 from act_ct_mapping acm
                 join act_hi_actinst aha
                   on acm.procinstid = aha.proc_inst_id_
                 join sale_item si
                   on si.id=acm.id
            Union All
            select acm.id ,utl_raw.cast_to_nvarchar2(utl_raw.cast_to_raw(s.create_user)) as assignee_
                 from act_ct_mapping acm
                 join sale_header s
                   on acm.id=s.id
                   ) group by id,assignee_
          )t1 on t1.id=sh.id
;
