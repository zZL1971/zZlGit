create or replace view sys_job_pool_cjob_v as
select DECODE(jp.job_status,'A','�ȴ�ϵͳ����','B','ϵͳ���ڹ���','C','���Զ�����','D','���ֶ�����') BILL_NAME,jp.zuonr from sys_job_pool jp where jp.create_time=(
    select max(p.create_time) from sys_job_pool p where p.zuonr=jp.zuonr and p.job_type=jp.job_type
)
and jp.job_type='CREDIT_JOB'
/*�������״̬ add Lrz 2016-04-15*/ 
;
