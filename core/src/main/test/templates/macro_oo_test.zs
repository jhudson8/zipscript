[@grid id="myGrid" entries=people]
	[%column title="First Name"]${entry.firstName?upperFirst}[/%column]
	[%column title="Last Name"]${entry.lastName!"Unknown Last Name"}[/%column]
	[#if doShowBirthday]
		[%column title="Birthday"]${entry.birthday|short}[/%column]
	[/#if]
[/@grid]

[@tab id="myId" title="Tab Title 1" callMacro=false]
	[%page id="page1" title="Page 1-1" selected=true]
		This is the page 1-1 contents
	[/%page]
	[%page id="page2" title="Page 1-2" selected=false]
		[@sectionHeader title=title]
			section body
		[/@sectionHeader]
		This is the page 1-2 contents
	[/%page]
[/@tab]

[@tab id="myId" title="Tab Title 1" callMacro=true]
	[%page id="page1" title="Page 2-1" selected=true]
		This is the page 2-1 contents
	[/%page]
	[%page id="page2" title="Page 2-2" selected=false]
		[@sectionHeader title=title]
			section body
		[/@sectionHeader]
		This is the page 2-2 contents
	[/%page]
[/@tab]


[#macro grid | column[title] id entries]
	The grid id is ${id}
	<table>
		<tr>
			[#foreach column in column]
			 <th>${column.title}</th>
			[/#foreach]
		</tr>
		[#foreach entry in entries]
		<tr>
			[#foreach column in column]
				<td>${column.body}</td>
			[/#foreach]
		</tr>
		[/#foreach]
	</table>
[/#macro]

[#macro tab | page[id title selected=false] id title callMacro]
--- Tab ---
	Id: ${id}
	Title: ${title}
[#if callMacro]
	[#foreach page in page]
		[#call commonPageMacro with page/]	
	[/#foreach]
[#else]
[#foreach page in page]
	--- Page ---
		Id: ${page.id}
		Title: ${page.title}
		Selected: ${page.selected}
[/#foreach]
[/#if]
[/#macro]

[#macro commonPageMacro | id title selected]
	--- Common Page ---
		Id: ${id}
		Title: ${title}
		Selected: ${selected}
[/#macro]

[#macro sectionHeader | title=null]
	This is a section - ${title}
[/#macro]