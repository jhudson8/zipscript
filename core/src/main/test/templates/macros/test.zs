[#macro testFoo]
	This should not show up
[/#macro]

[@tab:pane id="foo" title="My Tab"]
	[%page title="Page 1"]
		Page 1 contents
	[/%page]
	[%page title="Page 2"]
		Page 2 contents
	[/%page]
[/@tab:pane]