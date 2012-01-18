[#macro foo | abc="def" baz=null bar=null]
	abc=${abc}
	baz=$!{baz}
	bar=${bar}
- ${body}
[#set abc="jibjab"/]
- ${body}
[/#macro]

[#macro test | foo[] doit=null]
[#if test1!=null]
	[#foreach foo in foo]
		${foo.body}
	[/#foreach]
[/#if]
[/#macro]

[#macro sectionHeader | title=null]
	The section header title is ${title}
	--
	${body}
	--
[/#macro]

[@test doit=true]
	[@foo]def[/@foo]
	[@foo]ghi[/@foo]
[/@test]

[@foo abc="zyx" baz="jjj" bar="kkk"]
	flip flop
[/@foo]