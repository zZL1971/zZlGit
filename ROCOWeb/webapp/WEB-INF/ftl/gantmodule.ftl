<#assign types={"width":"int", "filterable":"boolean", "selectOnFocus":"boolean","editable":"boolean","allowBlank":"boolean","readOnly":"boolean","showKey":"boolean"}/>
<#assign mes={"CURR_USER_LOGIN_NO","Jurisdiction"}/>
[
<@buildNode child=doc.grid.gantmodule.module parent=doc.grid.gantmodule/>
<#macro buildNode child parent> 
    <#if child?? && child?size gt 0> 
        <#list child as t> 
        	{
            <#list t.@@ as attr>
            	<#if types[attr?node_name]?exists>
					${attr?node_name}:${attr}
				<#else>
					${attr?node_name}:<#if mes[attr]?exists>${attr}<#else>'${attr}'</#if>
				</#if>
            	<#if attr_has_next>,</#if>
            </#list>
            
            <#assign items = t.module>
			<#if (items?size>0)>
			,items:[
           	<@buildNode child=items parent=t/>
           	]
           	</#if>
           	}<#if t_has_next>,</#if>
        </#list> 
    </#if> 
</#macro>]