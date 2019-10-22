<#assign valids={"presence":"/^[\\s\\S]+$/g","startAG6":"/^[A-H]{1}([0-9]{2}|[0-9]{2}[0-9]{2})$/g"}/>
<#assign validErrors={"presence":"不能为空","startAG6":"必须是A-H打头(长度为3或5)"}/>
<#compress>
[
<#list doc.grid.validations.column as column>
	<#if column?node_type ='element'>
	{
		<#list column.@@ as attr>
			<#if valids[attr]?exists>
					${attr?node_name}:${valids[attr]},error:'${validErrors[attr]}'
			<#else>
				${attr?node_name}:'${attr}'
			</#if>
        	<#if attr_has_next>,</#if>
		</#list>
	}
	</#if>
	<#if column_has_next>,</#if>
</#list>
]
</#compress>


