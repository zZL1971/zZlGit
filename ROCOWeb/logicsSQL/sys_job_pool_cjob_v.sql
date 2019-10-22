create or replace view sys_job_pool_cjob_v as
select DECODE(jp.job_status,'A','等待系统过账','B','系统正在过账','C','已自动过账','D','需手动过账') BILL_NAME,jp.zuonr from sys_job_pool jp where jp.create_time=(
    select max(p.create_time) from sys_job_pool p where p.zuonr=jp.zuonr and p.job_type=jp.job_type
)
and jp.job_type='CREDIT_JOB'
/*财务过账状态 add Lrz 2016-04-15*/ 
;
