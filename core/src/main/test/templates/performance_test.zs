#** Here is a comment **#
[#if someList.size() < 2]
	We shouldn't get here
[#elseif someList.size() < 4]
	[#foreach text in someList]
		[#foreach char in text.toCharArray()]${i}${char}[/#foreach]
	[/#foreach] 
[#else]
	We shouldn't get here either
[/#if]

[#while i < 30]
	[@grid title="Grid ${i+1}" data=someList]
		[%header class="abc"]Header 1[/%header]
		[%header class="def"]Header 2[/%header]
		[%header class="ghi"]Header 3[/%header]
	[/@grid]
[/#while]

[#macro grid | header[] title=null data=null]
	The grid title is ${title}
	[#if header != null]
		<tr>
		[#foreach header in header]
			<th [#if header.class!=null]class="${header.class}" [/#if]>${header.body}</th>
		[/#foreach]
		</tr>
		[#foreach entry in data]
			<tr>
				${entry}
			</tr>
		[/#foreach]
	[/#if]
[/#macro]