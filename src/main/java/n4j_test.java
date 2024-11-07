import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.*;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

public class n4j_test {
    private enum RelTypes implements RelationshipType
    {
        KNOWS
    }
    /*private static void registerShutdownHook( final DatabaseManagementService managementService )
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                managementService.shutdown();
            }
        } );
    }*/

    public static void main(String[] args) {

        Path databaseDirectory = FileSystems.getDefault()
                .getPath("C:\\neo4j\\neo4j-community-5.25.1");
        DatabaseManagementService managementService =
                new DatabaseManagementServiceBuilder( databaseDirectory ).build();
        GraphDatabaseService graphDb = managementService.database( DEFAULT_DATABASE_NAME );


        try ( Transaction tx = graphDb.beginTx() )
        {
            // Database operations go here
            Node firstNode = tx.createNode();
            firstNode.setProperty( "message", "Hello, " );
            Node secondNode = tx.createNode();
            secondNode.setProperty( "message", "World!" );

            Relationship relationship = firstNode.createRelationshipTo( secondNode, RelTypes.KNOWS );
            relationship.setProperty( "message", "brave Neo4j " );
            System.out.print( firstNode.getProperty( "message" ) );
            System.out.print( relationship.getProperty( "message" ) );
            System.out.print( secondNode.getProperty( "message" ) );
            // let's remove the data
           /* firstNode = tx.getNodeByElementId( firstNode.getElementId() );
            secondNode = tx.getNodeByElementId( secondNode.getElementId() );
            firstNode.getSingleRelationship( RelTypes.KNOWS, Direction.OUTGOING ).delete();
            firstNode.delete();
            secondNode.delete();*/
            tx.commit();
        }
        managementService.shutdown();
    }
}
