@GrabResolver(name='neo4j-public-repo', root='http://m2.neo4j.org') 
@Grab('org.neo4j:neo4j-kernel:1.7.2') 
@Grab('org.neo4j:neo4j-cypher:1.7.2')
@Grab('org.neo4j:neo4j-lucene-index:1.7.2')
@Grab('org.neo4j:neo4j-testing-utils:1.7-SNAPSHOT')
@Grab('org.neo4j:neo4j-graphviz:1.7.2')

import org.neo4j.cypher.javacompat.*
import org.neo4j.graphdb.* 
import org.neo4j.index.lucene.*
import org.neo4j.kernel.impl.cache.SoftCacheProvider
import org.neo4j.test.*

// an enum helper
enum MyRelationshipTypes implements RelationshipType { featured }
 
// some optional metaclass syntactic sugar
Node.metaClass {
    propertyMissing { String name, val -> delegate.setProperty(name, val) }
    propertyMissing { String name -> delegate.getProperty(name) }
    methodMissing { String name, args -> delegate.createRelationshipTo(args[0], MyRelationshipTypes."$name") }
}

// keep the graph creation DRY
def getOrCreateNode(db, map, key, type) {
    def node

    if(map.containsKey(key)) {
        node = map.get(key)
    } else {
        node = db.createNode()
        node.name = key
        node.type = type

        map.put(key, node)
    }

    node
}

// helper method to create the graph
void createGraph(db, gd) {
    def movieMap = [:]
    def actorMap = [:]
 
    gd.each { str ->
        def movie, actor

        def parts = str.split('-')
        def movieKey = parts[0]
        def actorKey = parts[2]

        movie = getOrCreateNode(db, movieMap, movieKey, 'Movie')
        actor = getOrCreateNode(db, actorMap, actorKey, 'Actor')
        movie.featured(actor)
    } 
}

// Describe a graph as triples e.g. Movie-FEATURED-Actor
def graphDescription = [ 
    'Footloose-FEATURED-Kevin Bacon', 
    'Footloose-FEATURED-John Lithgow', 
    'Shrek-FEATURED-Mike Myers', 
    'Shrek-FEATURED-John Lithgow', 
    'Shrek-FEATURED-Cameron Diaz',
    "Charlie's Angels-FEATURED-Cameron Diaz",
    "Charlie's Angels-FEATURED-Bill Murray",
    '54-FEATURED-Mike Myers',
    '54-FEATURED-Neve Campbell',
    'Wild Things-FEATURED-Neve Campbell',
    'Wild Things-FEATURED-Bill Murray',
    'Wild Things-FEATURED-Kevin Bacon'
]


// Set up an impermanent test instance (this saves having to write disk clean up)
def db = new ImpermanentGraphDatabase([:], [new LuceneIndexProvider()], [], [new SoftCacheProvider()])

def nodeAutoIndexer = db.index().getNodeAutoIndexer();
nodeAutoIndexer.startAutoIndexingProperty( "name" );
nodeAutoIndexer.setEnabled( true );

def tx
      
try {
    tx = db.beginTx()

    db.index().forNodes("node_auto_index")

    // create the nodes for the fixture data
    createGraph(db, graphDescription)

    tx.success()
} finally {
    tx?.finish()
}

// a 2 degrees query
def query = '''
start k=node:node_auto_index(name = "Kevin Bacon") 
MATCH (k)--(m)--(a)--(n)--(b)
RETURN k.name, m.name, a.name, n.name, b.name
'''

// execute the query
ExecutionEngine engine = new ExecutionEngine(db)
ExecutionResult result = engine.execute(query)
println result
 
db.shutdown()