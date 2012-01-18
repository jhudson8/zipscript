[@simple title="My Page Title"]

    [@style]
        body {
            background-color: blue;
        }
    [/@style]

    [@script]
        function doSomething () {
            return true;
        }
    [/@script]

    This is the body content... blah blah blah
    <script language="java">
        doSomething();
    </script>

[/@simple]

[#macro simple | title="Default Title" onLoad=null script[src=null] style[src=null media=null]]
<html>
	<head>
		<title>${title}</title>
		[#if script != null]
			[#foreach script in script]
				[#if script.src == null]
					<script language="javascript">
						${script.body}
					</script>
				[#else]
					<script language="javascript" src="${script.src}></script>
				[/#if]
			[/#foreach]
		[/#if]
		[#if style != null]
			[#foreach style in style]
				[#if style.src == null]
					<style type="text/css">
						${style.body}
					</style>
				[#else]
					<link rel="stylesheet" type="text/css" href="${style.src}" [#if style.media != null]media="screen"[/#if]></link>				
				[/#if]
			[/#foreach]
		[/#if]
	</head>
	<body [#if onLoad!=null]onLoad="${onLoad}"[/#if]>
		<div class="header">
			Header Stuff
		</div>
		<div class="menu">
			Menu Stuff
		</div>
		<div class="screen">
			${body}
		</div>
		<div class="footer">
			Footer Stuff
		</div>
	</body>
</html>
[/#macro]