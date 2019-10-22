create or replace view ord_info_rp4 as
select sh.*,
       t.act_name_ as act_name,
       t.assignee_ as assignee,
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
  --链接saleItem，考虑子流程
  join sale_item si
    on si.pid = sh.id
  join
  (select acm.id, aha.assignee_, aha.act_name_
          from act_ct_mapping acm
          join act_hi_actinst aha
            on acm.procinstid = aha.proc_inst_id_) t
    on t.id = sh.id
    or t.id = si.id
