[#macro test | foo[flipflop bar[title=null]] callCommonMacro]
Suff at top
[#if callCommonMacro]
	Here's the list
	[#foreach foo in foo]
		${foo.body} - ${foo.flipflop}
	[/#foreach]
[#else]
	[#foreach foo in foo]
		[#call someCommonFooMacro with foo/]
	[/#foreach]
[/#if]
stuff at bottom
[/#macro]

[#macro someCommonFooMacro | flipflop=null bar=null]
	common macro foo attribute: ${flipflop}
	The number of bar parameters are ${bar?size}
	[#foreach entry in bar]
		The title is: $!{entry.title}
	[/#foreach]
[/#macro]

[@test callCommonMacro=true]
	[%foo flipflop="bar"]def[/%foo]
[/@test]
[@test callCommonMacro=false]
	[%foo flipflop="bar"]
		[%bar title="Bar 1"]
			this is bar 1 nested content
		[/%bar]
		[%bar title="Bar 2"]
			this is bar 2 nested content
		[/%bar]
	[/%foo]
[/@test]