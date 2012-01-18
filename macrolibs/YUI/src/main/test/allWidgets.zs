<p>
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
</p>

<p>
	[@data:table id="myTable" entries=people]
		[%column title="First Name" width=150]${entry.firstName}[/%column]
		[%column title="Last Name" width=150 selected=true]${entry.lastName}[/%column]
		[%column title="Birthday" format="date"]${entry.birthday?jsDate}[/%column]
		[%column title="# Accounts" format="number"]${entry.numAccounts}[/%column]
		[%column title="Net Worth" format="currency"]${entry.netWorth}[/%column]
		[%column title="Some Hidden Column" hidden=true]It doesn't matter what is here[/%column]
	[/@data:table]
</p>

<p>
	[@tree:view id="myTree"]
		[%node id="n1" label="Label 1" href="http://www.google.com"]
			[%node id="n1_1" label="Label 1.1"/]
			[%node id="n1_2" label="Label 1.2" tooltip="This is a tooltip"/]
			[%node id="n1_3" label="Label 1.3" tooltip="This is another tooltip"]
				[%node id="n1_3_1" label="Label 1.3.1"/]
			[/%node]
		[/%node]
		[%node id="n2" label="Label 2"]
			[%node id="n2_1" label="Label 2.1" href="http://www.google.com"/]
		[/%node]
		[%node id="n3" label="Label 3" href="http://www.google.com"/]
	[/@tree:view]
</p>