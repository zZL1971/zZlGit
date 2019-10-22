<#assign types={"width":"int","hideable":"boolean","mouseWheelEnabled","boolean","locked":"boolean", "filterable":"boolean","disabled":"boolean", "selectOnFocus":"boolean","hidden":"boolean","editable":"boolean","grow":"boolean","glyph":"int","allowBlank":"boolean"}/>
<#compress>
[
<#list doc.grid.head.column as column>
	<#if column?node_type ='element'><#--${column?node_name}-->
	{
		<#list column.@@ as attr>
			<#if types[attr?node_name]?exists>${attr?node_name}:${attr}<#elseif attr?node_name=='summaryRenderer'>${attr?node_name}: function(value){return Ext.String.format('<font style="font-weight: bold;">${attr}</font>', value);}<#else>${attr?node_name}:'${attr}'</#if><#if attr_has_next>,</#if>
		</#list>
		
		<#--解析特殊属性items-->
		<#assign items = column.item>
		<#if (items?size>0)>
			,items:[
			<#list items as item>
				{
				<#list item.@@ as attr>
					<#if attr?node_name=='handler'>
						${attr?node_name}:function(grid,rowIndex,colIndex){
							this.up('grid').fireEvent('${attr}',grid,rowIndex,colIndex);
						}
					<#elseif attr?node_name=='isDisabled'>
						${attr?node_name}:function(view, rowIdx, colIdx, item, record){
							return this.up('grid').fireEvent('${attr}',view, rowIdx, colIdx, item, record);
							//return true;
						}
					<#else>
						<#if types[attr?node_name]?exists>
							${attr?node_name}:${attr}
						<#else>
							${attr?node_name}:'${attr}'
						</#if>
					</#if>
					<#if attr_has_next>,</#if>
				</#list>
				}
				<#if item_has_next>,</#if>
			</#list>
			]
		</#if>
		
		<#--解析特殊属性editor-->
		<#assign editor = column.editor>
		<#if (editor?size>0)>
			<#if editor[0].@xtype='dictcombobox'|| editor[0].@xtype=='tablecombobox'>
				<#if editor[0].@xtype='dictcombobox'>
					,editor:Ext.create('Ext.ux.form.DictCombobox',{
				<#else>
					,editor:Ext.create('Ext.ux.form.TableComboBox',{
				</#if>
			<#else>
			,editor:{
			</#if>
			<#list editor[0].@@ as attr>
				<#if attr=='dictcombobox'||attr=='tablecombobox'>
					<#break>
				<#else>
					<#if types[attr?node_name]?exists>${attr?node_name}:${attr}<#else>${attr?node_name}:'${attr}'</#if>
					<#if attr_has_next>,</#if>
				</#if>
				
			</#list>
			<#if editor[0].@xtype=='dictcombobox' || editor[0].@xtype=='tablecombobox'>
			labelStyle:''}),renderer:Ext.ux.DataFactory.getComboboxForColumnRenderer
			<#else>
			}
			</#if>
			
		</#if>
		
		<#--解析特殊属性filter-->
		<#assign filter = column.filter>
		<#if (filter?size>0)>
			,filter:{
			<#list filter[0].@@ as attr>
				<#if types[attr?node_name]?exists>
					${attr?node_name}:${attr}
				<#else>
					${attr?node_name}:'${attr}'
				</#if>
				<#if attr_has_next>,</#if>
			</#list>
			}
		</#if>
		
		
		<#--<#assign vars = column.*>
		<#if (vars?size>0)>
			[
				<#list vars as ch>
					{
					<#list ch.@@ as attr>
						${attr?node_name}:${attr}
						<#if attr_has_next>,</#if>
					</#list>
					}
					<#if ch_has_next>,</#if>
				</#list>
			]
		</#if>-->
		
		
	}
	</#if>
	<#if column_has_next>,</#if>
</#list>
]
</#compress>


