create or replace procedure P_IMOS_IDBEXT_M_CHECK(SALE_ITEM_CODE IN VARCHAR2,
                                                  FLAG           OUT VARCHAR2,
                                                  MSG            OUT VARCHAR2) AS
                                                 
V_MSG VARCHAR2(4000);
V_COUNT NUMBER;
begin
  FLAG := 'Y'; 
 /* 物料审核,验证物料信息是否完整 add 2016-05-12 lrz*/
  IF SALE_ITEM_CODE IS NULL THEN
    MSG := '订单行编码不能为空！';
    FLAG := 'N'; 
    RETURN;
  END IF;
  SELECT COUNT(1) INTO V_COUNT  from imos_idbext ii where ii.orderid = SALE_ITEM_CODE;
  IF V_COUNT =0 THEN
    MSG := '订单行编码无效！'||SALE_ITEM_CODE;
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
          /*标识码
          Inf01 标识码为空时，不允许保存  标识码作创BOM用，空时会漏板
          数量
          CNT 数量为空时，或是为零时不允许保存  数量为空时，会创建失败
          规格
          CLENGTH
          CWIDTH
          CTHICKNESS  TYP=3时，长宽厚为空或零时不允许保存  规格为零时创建BOM失败及优化出错
          原材料描述
          MATNAME TYP=3时，为空或零时不允许保存 该字段用于优化时与材料库存匹配
          工艺路线
          ID_SERIE  A、  TYP=3时，为空或零或工艺路线不符合“%_%”时，不允许保存
          B、  TYP=3时，且 工艺路线=4_1或4_3时，原材料描述不符合“%_%”或SURFTNAM不符合“%_%”，不保存
          C、  当工艺路线为4_* 且 标识码不符合D*时，ID_TEXT不满足“%_4”者不保存
          D、  当工艺路线为4_* 且标识码=D*时，不符合ID_TEXT= 4 或为空时不保存  A、  要求TYP=3时，一定有工艺路线
          B、  要求吸塑板件的板材与贴面达到匹配
          C、  要求其他产线拿到吸塑线生产时，一定给出ID_TEXT字段信息，避免漏板
          D、  要求吸塑线上要排产的板件要给出ID_TEXT字段信息，避免漏排产
          物料编码
          ARTICLE_ID  TYP=8时，物料编码长度符合11位，且符合“3%”，否则不保存  原材料的物料编码为11位，且以3开头
          名称
          NAME  为空时不保存  板件名称不保存*/
           
          IF TEMP.INFO1 IS NULL THEN
            V_MSG:=TEMP.ID||'标示码不能为空！';
          END IF;
          
          IF TEMP.CNT IS NULL OR TEMP.CNT=0 THEN
            V_MSG:=V_MSG||'  '||TEMP.ID||'数量不能为空或0！';
          END IF;
          IF TEMP.TYP ='3' THEN 
            IF TEMP.CLENGTH IS NULL OR TEMP.CLENGTH=0 THEN
              V_MSG:=V_MSG||'  '||TEMP.ID||'开料长度不能为空或0！';
            END IF;
            
            IF TEMP.CWIDTH IS NULL OR TEMP.CWIDTH=0 THEN
              V_MSG:=V_MSG||'  '||TEMP.ID||'开料宽度不能为空或0！';
            END IF;
            
            IF TEMP.CTHICKNESS IS NULL OR TEMP.CTHICKNESS=0 THEN
              V_MSG:=V_MSG||'  '||TEMP.ID||'开料厚度不能为空或0！';
            END IF;
            
            IF TEMP.MATNAME IS NULL OR TEMP.MATNAME='0' THEN
              V_MSG:=V_MSG||'  '||TEMP.ID||'原材料描述不能为空或0！';
            END IF;
            
            IF TEMP.ID_SERIE IS NULL OR TEMP.ID_SERIE='0' THEN
              V_MSG:=V_MSG||'  '||TEMP.ID||'工艺路线不能为空或0！';
            ELSIF TEMP.ID_SERIE='4_1' OR TEMP.ID_SERIE='4_3' THEN
              
              IF INSTR(TEMP.MATNAME,'_')=0 THEN 
                 V_MSG:=V_MSG||'  '||TEMP.ID||'原材料描述不符合*_*！'; 
              END IF;
              IF INSTR(TEMP.SURFTNAM,'_')=0 THEN 
                 V_MSG:=V_MSG||'  '||TEMP.ID||'SURFTNAM不符合*_*！'; 
              END IF;
              
            ELSIF INSTR(TEMP.ID_SERIE,'_')=0 THEN
              V_MSG:=V_MSG||'  '||TEMP.ID||'工艺路线不符合*_*！'; 
            END IF;
          END IF;
          
          IF INSTR(TEMP.ID_SERIE,'4_')>0 THEN
           
            IF INSTR(TEMP.INFO1,'D')<>1 THEN
             --当工艺路线为4_* 且 标识码不符合D*时
              V_MSG:=V_MSG||'  '||TEMP.ID||'标识码不符合D*！'; 
            END IF;
            
            IF INSTR(TEMP.ID_TEXT,'_4')<>(LENGTH(TEMP.ID_TEXT)-1) THEN
               --ID_TEXT不满足“%_4”者不保存
                V_MSG:=V_MSG||'  '||TEMP.ID||'ID_TEXT不符合*_4！'; 
            END IF;
            
            IF INSTR(TEMP.INFO1,'D')=1 THEN
               IF TEMP.ID_TEXT IS NULL THEN
                 V_MSG:=V_MSG||'  '||TEMP.ID||'ID_TEXT不能为空！'; 
               ELSIF TEMP.ID_TEXT<>'4' THEN
                  V_MSG:=V_MSG||'  '||TEMP.ID||'不符合ID_TEXT= 4！'; 
               END IF;
            END IF;
          END IF;
          
          IF TEMP.TYP=8 THEN
            IF LENGTH(TEMP.ARTICLE_ID)<>11 AND INSTR(TEMP.ARTICLE_ID,'3')<>1 THEN
               --物料编码长度符合11位，且符合“3%”
               V_MSG:=V_MSG||'  '||TEMP.ID||'物料编码长度不符合11位，且符合"3*"!'; 
            END IF;
          END IF;  
            
          IF TEMP.NAME IS NULL THEN
            V_MSG:=TEMP.ID||'名称不能为空！';
          END IF;
  END LOOP;
  IF V_MSG IS NOT NULL THEN
    FLAG:='N';
    MSG:=V_MSG;
  END IF;
  EXCEPTION
     WHEN OTHERS THEN  
      MSG:='系统异常'||SQLERRM; 
end P_IMOS_IDBEXT_M_CHECK;
/
