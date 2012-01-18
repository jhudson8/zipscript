${(obj.val)+1}
${obj.val+1}

[##
== Formal Variables ==
${myObject.testMap['anotherObj'].list1[0].getText().length}
${myObject.testMap['anotherObj'].list1[0].getText()}
${myObject.testMap['anotherObj'].list1[0].Text}
${myObject.testMap['anotherObj'].list1[0].text}
[#foreach obj in myObject.testMap['anotherObj'].list1]
	${obj.text}
[/#foreach]
->$!{myObjectNoExist}<-

${myString} is ${myString.length} characters long

${obj[dynamicPath]} <-- we can access object paths dynamically


== Lazy Variables ==
$myObject.testMap['anotherObj'].list1[0].getText().length()
$myObject.testMap['anotherObj'].list1[0].getText()
$myObject.testMap['anotherObj'].list1[0].Text
$myObject.testMap['anotherObj'].list1[0].text
-> $!myObjectNoExist <-
->$!myObjectNoExist<-

$myString is $myString.length characters long

== Resources ==
$Resource("1 This is a test")
$Resource("2 This is a test", {"param 1", "param 2"})
${Resource("3 This is a test")}
${Resource("4 This is a test", {"param 1", "param 2"})}

== Map Creation ==
[#set abc=[hhh="jjj"]/]
[#set abc["foo"]="bar"/]
${abc.foo} -- ${abc['foo']} -- ${abc.hhh}
[#set Global.abc["foo"]="ddd"/]
${abc.foo} -- ${abc['foo']}

== Sequence Creation ==
[#set mySequence={}/]
${mySequence?size}
[#set mySequence={"abc"}/]
${mySequence[0]}

== Iterate through context keys ==
[#foreach key in Vars?keys]	
	[#if key.substring(0,1)?isLowerCase]
		Context Key: ${key} - ${Vars[key]}
	[/#if]
[/#foreach]
##]