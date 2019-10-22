create or replace view act_summary_view as
--��ͳ��
--�������̽���ͳ��
select "ACT_NAME",
       "ORDER_STATUS",
       "ORDER_YEAR",
       "ORDER_MONTH",
       "ORDER_DAY",
       "TOTAL"
  from (select act_name,
               order_status,
               to_char(max_end_time, 'yyyy') as order_year,
               to_char(max_end_time, 'mm') as order_month,
               to_char(max_end_time, 'dd') as order_day,
               count(1) as total
          from (main_act_rp_view_sub) t2 
         group by act_name,
                  order_status,
                  to_char(max_end_time, 'yyyy'),
                  to_char(max_end_time, 'mm'),
                  to_char(max_end_time, 'dd'));

				  
create or replace view main_act_rp_view_sub as
select aha.proc_inst_id_ as proc_inst_id,
       max(aha.end_time_) as max_end_time,
       max(aha.start_time_) as max_start_time,
       aha.act_id_ as act_id,
       aha.act_name_ as act_name,
       count(1) as redo_time,
       t1.order_code,
       t1.order_type,
       t1.order_total,
       (select a.order_status
          from sale_header a
         where a.order_code = t1.order_code) as order_status
  from (
  --��ȡsale_header�е���Ϣ������ȡ��procinstid
         select sh.order_code,
               sh.order_status,
               sh.order_type,
               sh.order_total,
               (select acm.procinstid
                  from act_ct_mapping acm
                 where sh.id = acm.id) as mapping_id
          from sale_header sh
          ) t1
  join act_hi_actinst aha
    on aha.proc_inst_id_ = t1.mapping_id
    --�ų���ʼ�ڵ㡢�����ڵ㡢�Լ���������ڵ�
 where aha.act_type_ not in ('startEvent', 'endEvent', 'exclusiveGateway')
 group by 
 --��Ե��������ĵ������ڽ��з��飬��һ��������һ������Ϊһ����¼
          t1.order_code,
          t1.order_type,
          t1.order_total,
          aha.proc_inst_id_,
          aha.act_id_,
          aha.act_name_;

		  
create or replace view sub_act_summary_view as
select t2.*, mh.status as order_status
  from (select t1.proc_inst_id,
               t1.act_id,
               t1.act_name,
               --���û�н����Ľڵ�ģ���ֵ9999-12-31Ϊ������ʱ��ĺ���������������ʱ��Ϊ9999-12-31������Ϊnull
               (case
                 when t1.max_end_time = date '9999-12-31' then
                  null
                 else
                  t1.max_end_time
               end) as max_end_time,
               max_start_time,
               redo_time,
               acm.id,
               (select s.order_code_posex
                  from sale_item s
                 where s.id = acm.id) as order_code,
               --��ȡ�����ļ۸�
               (select nvl(sh.order_total, 0)
                  from sale_header sh
                  join sale_item si
                    on si.pid = sh.id
                 where si.id = acm.id) as order_total,
               --��ȡ����id
               (select s.material_head_id
                  from sale_item s
                 where s.id = acm.id) as material_head_id,
               --��ȡ������
               (select a.assignee_
                  from act_hi_actinst a
                 where a.proc_inst_id_ = t1.proc_inst_id
                   and a.act_id_ = t1.act_id
                   and a.end_time_ = t1.max_end_time) as assignee
          from (
          --��ȡ�������̵����л��ڵ�����״̬��������ʱ�䣬���ʼʱ�䣬�������δ������������ʱ����Ϊnull�ģ���ô��ֵ9999-12-31���ٺ����������ų��˿�ʼ�ڵ㡢�����ڵ㡢��������
                   select aha.proc_inst_id_ as proc_inst_id,
                       aha.act_id_       as act_id,
                       aha.act_name_     as act_name,
                       max(nvl(aha.end_time_, date '9999-12-31')) as max_end_time,
                       max(aha.start_time_) as max_start_time,
                       count(1) as redo_time
                  from act_hi_actinst aha
                 where aha.proc_def_id_ like 'subProductQuotation:%'
                   and aha.act_type_ not in
                       ('startEvent', 'endEvent', 'exclusiveGateway')
                 group by aha.proc_inst_id_, aha.act_id_, aha.act_name_
                 ) t1
          left join act_ct_mapping acm
            on t1.proc_inst_id = acm.procinstid) t2
  left join material_head mh
    on t2.material_head_id = mh.id;

	
	
