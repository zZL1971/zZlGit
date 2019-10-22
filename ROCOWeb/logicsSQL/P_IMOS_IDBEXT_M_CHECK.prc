create or replace procedure P_IMOS_IDBEXT_M_CHECK(SALE_ITEM_CODE IN VARCHAR2,
                                                  FLAG           OUT VARCHAR2,
                                                  MSG            OUT VARCHAR2) AS
                                                 
V_MSG VARCHAR2(4000);
V_COUNT NUMBER;
begin
  FLAG := 'Y'; 
 /* �������,��֤������Ϣ�Ƿ����� add 2016-05-12 lrz*/
  IF SALE_ITEM_CODE IS NULL THEN
    MSG := '�����б��벻��Ϊ�գ�';
    FLAG := 'N'; 
    RETURN;
  END IF;
  SELECT COUNT(1) INTO V_COUNT  from imos_idbext ii where ii.orderid = SALE_ITEM_CODE;
  IF V_COUNT =0 THEN
    MSG := '�����б�����Ч��'||SALE_ITEM_CODE;
    FLAG := 'N'; 
    RETURN;
  END IF;
  
  FOR TEMP IN (select ii.id,
                      ii.typ,
                      ii.info1,
                      ii.cnt,
                      ii.clength,
                      ii.cwidth,
                      ii.cthickness,
                      ii.matname,
                      ii.id_serie,
                      ii.article_id,
                      ii.Surftnam,
                      ii.id_text,
                      ii.name
                 from imos_idbext ii
                where ii.typ in ('3', '8')
                  and ii.orderid = SALE_ITEM_CODE) LOOP
          /*��ʶ��
          Inf01 ��ʶ��Ϊ��ʱ����������  ��ʶ������BOM�ã���ʱ��©��
          ����
          CNT ����Ϊ��ʱ������Ϊ��ʱ��������  ����Ϊ��ʱ���ᴴ��ʧ��
          ���
          CLENGTH
          CWIDTH
          CTHICKNESS  TYP=3ʱ�������Ϊ�ջ���ʱ��������  ���Ϊ��ʱ����BOMʧ�ܼ��Ż�����
          ԭ��������
          MATNAME TYP=3ʱ��Ϊ�ջ���ʱ�������� ���ֶ������Ż�ʱ����Ͽ��ƥ��
          ����·��
          ID_SERIE  A��  TYP=3ʱ��Ϊ�ջ������·�߲����ϡ�%_%��ʱ����������
          B��  TYP=3ʱ���� ����·��=4_1��4_3ʱ��ԭ�������������ϡ�%_%����SURFTNAM�����ϡ�%_%����������
          C��  ������·��Ϊ4_* �� ��ʶ�벻����D*ʱ��ID_TEXT�����㡰%_4���߲�����
          D��  ������·��Ϊ4_* �ұ�ʶ��=D*ʱ��������ID_TEXT= 4 ��Ϊ��ʱ������  A��  Ҫ��TYP=3ʱ��һ���й���·��
          B��  Ҫ�����ܰ���İ��������ﵽƥ��
          C��  Ҫ�����������õ�����������ʱ��һ������ID_TEXT�ֶ���Ϣ������©��
          D��  Ҫ����������Ҫ�Ų��İ��Ҫ����ID_TEXT�ֶ���Ϣ������©�Ų�
          ���ϱ���
          ARTICLE_ID  TYP=8ʱ�����ϱ��볤�ȷ���11λ���ҷ��ϡ�3%�������򲻱���  ԭ���ϵ����ϱ���Ϊ11λ������3��ͷ
          ����
          NAME  Ϊ��ʱ������  ������Ʋ�����*/
           
          IF TEMP.INFO1 IS NULL THEN
            V_MSG:=TEMP.ID||'��ʾ�벻��Ϊ�գ�';
          END IF;
          
          IF TEMP.CNT IS NULL OR TEMP.CNT=0 THEN
            V_MSG:=V_MSG||'  '||TEMP.ID||'��������Ϊ�ջ�0��';
          END IF;
          IF TEMP.TYP ='3' THEN 
            IF TEMP.CLENGTH IS NULL OR TEMP.CLENGTH=0 THEN
              V_MSG:=V_MSG||'  '||TEMP.ID||'���ϳ��Ȳ���Ϊ�ջ�0��';
            END IF;
            
            IF TEMP.CWIDTH IS NULL OR TEMP.CWIDTH=0 THEN
              V_MSG:=V_MSG||'  '||TEMP.ID||'���Ͽ�Ȳ���Ϊ�ջ�0��';
            END IF;
            
            IF TEMP.CTHICKNESS IS NULL OR TEMP.CTHICKNESS=0 THEN
              V_MSG:=V_MSG||'  '||TEMP.ID||'���Ϻ�Ȳ���Ϊ�ջ�0��';
            END IF;
            
            IF TEMP.MATNAME IS NULL OR TEMP.MATNAME='0' THEN
              V_MSG:=V_MSG||'  '||TEMP.ID||'ԭ������������Ϊ�ջ�0��';
            END IF;
            
            IF TEMP.ID_SERIE IS NULL OR TEMP.ID_SERIE='0' THEN
              V_MSG:=V_MSG||'  '||TEMP.ID||'����·�߲���Ϊ�ջ�0��';
            ELSIF TEMP.ID_SERIE='4_1' OR TEMP.ID_SERIE='4_3' THEN
              
              IF INSTR(TEMP.MATNAME,'_')=0 THEN 
                 V_MSG:=V_MSG||'  '||TEMP.ID||'ԭ��������������*_*��'; 
              END IF;
              IF INSTR(TEMP.SURFTNAM,'_')=0 THEN 
                 V_MSG:=V_MSG||'  '||TEMP.ID||'SURFTNAM������*_*��'; 
              END IF;
              
            ELSIF INSTR(TEMP.ID_SERIE,'_')=0 THEN
              V_MSG:=V_MSG||'  '||TEMP.ID||'����·�߲�����*_*��'; 
            END IF;
          END IF;
          
          IF INSTR(TEMP.ID_SERIE,'4_')>0 THEN
           
            IF INSTR(TEMP.INFO1,'D')<>1 THEN
             --������·��Ϊ4_* �� ��ʶ�벻����D*ʱ
              V_MSG:=V_MSG||'  '||TEMP.ID||'��ʶ�벻����D*��'; 
            END IF;
            
            IF INSTR(TEMP.ID_TEXT,'_4')<>(LENGTH(TEMP.ID_TEXT)-1) THEN
               --ID_TEXT�����㡰%_4���߲�����
                V_MSG:=V_MSG||'  '||TEMP.ID||'ID_TEXT������*_4��'; 
            END IF;
            
            IF INSTR(TEMP.INFO1,'D')=1 THEN
               IF TEMP.ID_TEXT IS NULL THEN
                 V_MSG:=V_MSG||'  '||TEMP.ID||'ID_TEXT����Ϊ�գ�'; 
               ELSIF TEMP.ID_TEXT<>'4' THEN
                  V_MSG:=V_MSG||'  '||TEMP.ID||'������ID_TEXT= 4��'; 
               END IF;
            END IF;
          END IF;
          
          IF TEMP.TYP=8 THEN
            IF LENGTH(TEMP.ARTICLE_ID)<>11 AND INSTR(TEMP.ARTICLE_ID,'3')<>1 THEN
               --���ϱ��볤�ȷ���11λ���ҷ��ϡ�3%��
               V_MSG:=V_MSG||'  '||TEMP.ID||'���ϱ��볤�Ȳ�����11λ���ҷ���"3*"!'; 
            END IF;
          END IF;  
            
          IF TEMP.NAME IS NULL THEN
            V_MSG:=TEMP.ID||'���Ʋ���Ϊ�գ�';
          END IF;
  END LOOP;
  IF V_MSG IS NOT NULL THEN
    FLAG:='N';
    MSG:=V_MSG;
  END IF;
  EXCEPTION
     WHEN OTHERS THEN  
      MSG:='ϵͳ�쳣'||SQLERRM; 
end P_IMOS_IDBEXT_M_CHECK;
/
