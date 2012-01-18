[#if 1 in {1,2}]
	Correct!
[#else]
	Not correct!
[/#if]

[#if "abc" in {foo, bar, baz}]
	Correct!
[#else]
	Not correct!
[/#if]

[#if "abc" not in {foo, bar, baz}]
	Not correct!
[#else]
	Correct!
[/#if]

[#if foo=='abc']
Correct
[#elseif foo=='def']
Wrong 1
[#elseif foo=='ghi']
Wrong 2
[#else]
Wrong 3
[/#if]

[#if bar=='abc']
Wrong 1
[#elseif bar=='def']
Correct
[#elseif bar=='ghi']
Wrong 2
[#else]
Wrong 3
[/#if]

[#if baz=='abc']
Wrong 1
[#elseif baz=='def']
Wrong 2
[#elseif baz=='ghi']
Correct
[#else]
Wrong 3
[/#if]