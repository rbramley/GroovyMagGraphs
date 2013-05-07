package neo

import groovy.transform.ToString

@ToString(includes='name')
class Skill {

	String name

    static constraints = {
    }
    
    static mapWith = "neo4j"
}
