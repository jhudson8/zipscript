<html>
	<head>
		<link rel="stylesheet" type="text/css" href="${yuiIncludePrefix}/fonts/fonts-min.css" />
[#foreach key in cssIncludes?keys]
		<link rel="stylesheet" type="text/css" href="${yuiIncludePrefix}/${key}" />
[/#foreach]

		<script type="text/javascript" src="${yuiIncludePrefix}/yahoo-dom-event/yahoo-dom-event.js"></script>
		<script type="text/javascript" src="${yuiIncludePrefix}/dragdrop/dragdrop-min.js"></script>
		<script type="text/javascript" src="${yuiIncludePrefix}/element/element-beta-min.js"></script>
[#foreach key in scriptIncludes?keys]
		<script type="text/javascript" src="${yuiIncludePrefix}/${key}"></script>
[/#foreach]
	</head>
	<body class=" yui-skin-sam">
${screen_placeholder}
	</body>
</html>