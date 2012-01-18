${firstName} ${lastName} ${car}

${company.name}

[#foreach employee in company.employee]
	${employee.firstName} ${employee.lastName}
[/#foreach]

${company.employee[1].firstName}

[#foreach child in children]
	${child.firstName}
[/#foreach]

${company?xpath("@name")}