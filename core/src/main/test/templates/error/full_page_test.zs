This is a line
[#while i<10]
	[#if foo.bar]
		[#set abc="def"/]
	[#elseif foo.bbb && 3 > blip]
		Something here
	[#else]
		Something else here
	[/#if]
	stuff stuff stuff
[/#while]