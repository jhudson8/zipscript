[#macro tab | page[title] title]
	The title is ${title}
	[#foreach page in page]
	-- ${page.title} --
	${page.body}

	[/#foreach]
[/#macro]

[#macro commonPages]
	[.%page title="foo"]
		foo contents
	[/.%page]
	[.%page title="bar"]
		bar contents
	[/.%page]
[/#macro]

[@tab title="abc"]
	[%page title="def"]
		def contents
	[/%page]
	[@commonPages/]
[/@tab]