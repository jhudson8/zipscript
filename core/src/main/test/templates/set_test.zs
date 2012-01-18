[#set abcList = {3,4f,5d,6l, 7b, 8s, 'some text',true,   false}/]
[#foreach entry in abcList]
	${entry}
[/#foreach]

[#set val=(3<4)/]
[#if val]
	Correct
[/#if]
