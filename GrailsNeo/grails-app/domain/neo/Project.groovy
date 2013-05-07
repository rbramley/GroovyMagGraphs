package neo

import groovy.transform.ToString

@ToString(includes='projectName')
class Project {

	String customerName
	String projectName
	Date startDate
	Date endDate

	static hasMany = [ members : Employee, requiredSkills : Skill ]

    static constraints = {
    }
    
    static belongsTo = Employee
    
    static mapWith = "neo4j"
}
