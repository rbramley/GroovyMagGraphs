package neo

import groovy.transform.ToString

@ToString(includes='name')
class Employee {
	String name
	String title
	Date dob

	static hasMany = [ workedOn : Project, knows : Skill ]
	
    static constraints = {
    }
    
    static mapWith = "neo4j"
}
