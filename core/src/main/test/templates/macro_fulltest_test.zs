[@grid entries=people title="Interesting People"]
	[%header]
        Some header content
	[/%header]

        [##
                - template-defined parameters (only string attributes should be enclosed in quotes)
                - the "entry" context value is added by the macro definition
                        (see ...foreach entry in entries... in the macro definition below)
        ##]
        [%column title="id" hidden=true] ${entry.id} [/%column]
		[@commonColumns/]

        [## template-defined parameters can be inside other directives ##]
        [#if showAge]
                [%column title="Age"] ${entry.age} [/%column]
        [/#if]

        [##
                - template-defined parameters can be externalized for powerful functionality
                - columnList (see below) would be added to the context before merging
        ##]
        [#foreach columnInfo in columnList]
                [## properties can be access using [] (see below) when dealing with dynamic property names ##]
                [%column title=columnInfo.title size=columnInfo.size] ${entry[columnInfo.propertyName]} [/%column]
        [/#foreach]

        Any non template-defined parameter content inside the macro reference is the macro body and can
        be referenced as ${body}
        
	[%footer]
        Some footer content - can use macros inside of macros inside of macros...
        [@simpleMacro]
        	footer content
        [/@simpleMacro]
	[/%footer]
[/@grid]


[##
        the macro definition below has the name of "grid" and would be in the "data" macro library as you can see by the reference to "data.grid" above
##]

[#macro grid | column[title width=100 hidden=false cssClass=null] header[] footer[] title entries]
        The title is ${title}
        <table>
                <tr>
                        [#foreach column in column]
                                [#set colSpan=colSpan+1/]
                                <th [#if column.cssClass!=null]class="${column.cssClass}"[/#if]>${column.title}</th>
                        [/#foreach]
                </tr>
                [#if header!=null]
                        <tr><td colspan="${colSpan}">${header.body}</td></tr>
                [/#if]
                [#foreach entry in entries]
                        <tr>
                        [#foreach column in column]
                                <td style="[#if column.hidden]display: none;[/#if] width: ${column.width}px"> ${column.body} </td>
                        [/#foreach]
                        </tr>
                [/#foreach]
                [#if footer!=null]
                        <tr><td colspan="${colSpan}">${footer.body}</td></tr>
                [/#if]          
        </table>
[/#macro]

[#macro commonColumns]
        [.%column title="First Name" width=120] ${entry.firstName} [/.%column]
        [@moreCommonColumns/]
[/#macro]

[#macro moreCommonColumns]
	[.%column title="Last Name" width=140] ${entry.lastName} [/.%column]
[/#macro]

[#macro simpleMacro]
	[#if body?objectValue == "footer content"] See how you can evaluate nested content
	[#else] If we see this, something is broken
	[/#if]
[/#macro]

[#macro text | name="foo" id=null label=null required=false size=null maxLength=null value=null]
	[@label label=label]
		<input type="text" name="${name}"[#if id!=null] id="${id}"[/#if][#if size!=null] size="${size}"[/#if][#if maxLength!=null] maxLength="${maxLength}"[/#if][#if value!=null] value="${value?js}[/#if]"/>
	[/@label]
[/#macro]

[#macro label | label=null]
	[#if label!=null]
		<tr>
			<td class="label">${Resource("label.${label}")}</td>
			<td class="entry">${body}</td>
		</tr>
	[#else]
		${body}
	[/#if]
[/#macro]

[@text label="jobTitle" name="jobTitle" size=30 maxLength=80 required=true/]