[#while i < 20]
	i is ${i}
[/#while]

[#while i < 20]
	i is ${i}
[#if i > 10][#break/][/#if]
[/#while]

[#while i < 20]
[#if i == 11 || i == 12][#continue/][/#if]
	i is ${i}
[/#while]