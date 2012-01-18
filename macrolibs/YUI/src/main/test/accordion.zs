[#import "accordion.zsm" as accordion/]

<style type="text/css">
	.yui-accordion-content div {
		padding: 6px;
	}
</style>

[@accordion:view id="mymenu5" collapsible=false width="250px"]
	[%panel title="Item 1"]
		This is the content of panel 1
	[/%panel]
	[%panel title="Item 2"]
		This is the content of panel 2
	[/%panel]
	[%panel title="Item 3" selected=true]
		This is the content of panel 3
	[/%panel]
	[%panel title="Item 4"]
		This is the content of panel 4
	[/%panel]
	[%panel title="Item 5"]
		This is the content of panel 5
	[/%panel]
[/@accordion:view]