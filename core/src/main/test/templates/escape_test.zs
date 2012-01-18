[#set foo = "abc"/]
\[#set foo = "def"/]
\${foo}
\$foo
\\${foo}
\\$foo
\\\${foo}
\\\$foo


[#set var="abc"/]
[#escape upperCase]
    ${var} def ${"ghi"}
    [#noescape]
        jlk ${"mno"}
    [/#noescape]
[/#escape]


[#set var = "http://www.google.com?abc=def&ghi=jfk"/]
${var}
[#escape html]
	${var}
	[#noescape]
		${var}
	[/#noescape]
[/#escape]

[@foo param1="ab'cd"]
	ef'gh
	[#escape js]
		ij'kl
		${"mn'op"}
	[/#escape]
[/@foo]

[#macro foo | param1]
	[#escape js]
		${param1}
		${body}
	[/#escape]
[/#macro]