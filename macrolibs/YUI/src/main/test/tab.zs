[@tab:pane id="myTab"]
	[%page label="Some Page" active=true]
		These are the contents of some page...
		<p>
			Blah blah blah...
		</p> 
	[/%page]
	[#if true]
		[#set testVar = "ABCD"/]
		[%page label='Page "${testVar}"']
			This is the contents of page "${testVar}"
		[/%page]
	[/#if]
	[#while i<2]
		[%page label="Page ${i+1}"]
			This is the contents of page ${i+1}
		[/%page]
	[/#while]
	[#foreach item in {"A","B","C"}]
		[%page label='Page "${item}"']
			This is the contents of page "${item}"
		[/%page]
	[/#foreach]
[/@tab:pane]
