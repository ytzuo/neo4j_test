import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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

        try ( Transaction tx = graphDb.beginTx() ){ //请注意，在同一事务中不允许 schema 更改和数据更改。
            //添加索引
            /*一、索引概述

            索引是一种数据结构，它允许数据库系统更快地定位、访问和检索数据。
            在Neo4j中，索引主要用于加速节点和关系的查找，从而提高查询性能。
            通过索引，Neo4j能够减少全表扫描的次数，只查找必要的节点和关系，从而大大提高查询效率。

            二、Neo4j中的索引类型

            Neo4j支持两种类型的索引：本地索引和全文索引。

            本地索引：本地索引用于加速基于节点属性的查找。
            在Neo4j中，你可以为节点的属性创建本地索引，从而提高基于这些属性的查询性能。
            例如，如果你经常根据用户的姓名或年龄进行查询，那么为这些属性创建本地索引将非常有用。

            全文索引：全文索引用于加速基于节点标签和属性值的文本搜索。
            通过全文索引，你可以执行复杂的文本查询，如模糊匹配、短语搜索等。
            这对于处理大量文本数据非常有用，如社交媒体帖子、博客文章等。*/


            //按名称为User编制索引
            //此处代码只需执行一次, 故注释掉此处代码
           /* IndexDefinition usernamesIndex;
            Schema schema = tx.schema();
            usernamesIndex = schema.indexFor( Label.label( "User" ) )
                    .on( "username" )  //定义应属于此索引的属性。 为带有标签的所有节点编制索引
                    .withName( "usernames" )
                    .create();
            tx.commit();
            //索引在首次创建时以异步方式填充。
            schema.awaitIndexOnline( usernamesIndex, 10, TimeUnit.SECONDS );
            //查询索引填充的进度
            System.out.println( String.format( "Percent complete: %1.0f%%",
                    schema.getIndexPopulationProgress( usernamesIndex ).getCompletedPercentage() ) );
            Label label = Label.label( "User" );*/

            // Create some users
            Label label = Label.label( "User" );
            for ( int id = 0; id < 100; id++ )
            {
                Node userNode = tx.createNode( label );
                userNode.setProperty( "username", "user" + id + "@neo4j.org" );
            }
            System.out.println( "Users created" );

            //Label label = Label.label( "User" );
            int idToFind = 45;
            String nameToFind = "user" + idToFind + "@neo4j.org";

            //按ID查询user
            try ( ResourceIterator<Node> users =
                          tx.findNodes( label, "username", nameToFind ) )
            {
                ArrayList<Node> userNodes = new ArrayList<>();
                while ( users.hasNext() )
                {
                    userNodes.add( users.next() );
                }
                for ( Node node : userNodes )
                {
                    System.out.println(
                            "The username of user " + idToFind + " is " + node.getProperty( "username" ) );
                }
            }
            tx.commit();
        }
        managementService.shutdown();
    }
}
