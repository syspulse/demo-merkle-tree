import org.scalatest.{Matchers, WordSpec}

import sangria.ast.Document
import sangria.macros._
import sangria.execution.Executor
import sangria.execution.deferred.DeferredResolver
import sangria.marshalling.circe._

import io.circe._
import io.circe.parser._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import SchemaDefinition.NodesSchema

class SchemaSpec extends WordSpec with Matchers {

  val testNodes = List(
    NodeRoot(
      data = "1000",
      name = Some("Node-Root-1"),
      t = NodeType.ROOT    
    ),
    NodeHash(
      data = "2001",
      name = Some("Node-Hash-1"),
      t = NodeType.HASH
    ),
    NodeHash(
      data = "2002",
      name = Some("Node-Hash-2"),
      t = NodeType.HASH
    ),
    NodeLeaf(
      data = "1000",
      name = Some("Node-Lead-3"),
      t = NodeType.LEAF
    )
  )

  "Nodes Schema" should {
    "find Node-Hash-1" in {
      val query =
        graphql"""
         query {
           findNodes(data: "2001") {data,name,type} 
         }
       """

      executeQuery(query) should be (parse(
      """
      {
        "data": {
          "findNodes": [
            {
              "data": "2001",
              "name": "Node-Hash-1",
              "type": "HASH"
            }
          ]
        }
      }
      """).getOrElse(fail("Failed to parse expected JSON. Please confirm the JSON is valid.")))
    }

    "allow to fetch Node using his Data provided through variables" in {
      val query =
        graphql"""
         query FetchSomeIDQuery($$dataValue: String!) {
           node(data: $$dataValue) {
             data,name,type
           }
         }
       """

      executeQuery(query, vars = Json.obj("dataValue" -> Json.fromString("1000"))) should be (parse(
        """
        {
          "data": {
            "node": 
              {
                "data": "1000",
                "name": "Node-Root-1",
                "type": "ROOT"
              }
          }
        }
        """).getOrElse(fail("Failed to parse expected JSON. Please confirm the JSON is valid.")))
    }
  }

  def executeQuery(query: Document, vars: Json = Json.obj()) = {
    val futureResult = Executor.execute(NodesSchema, query,
      variables = vars,
      userContext = NodeRepo(testNodes),
      deferredResolver = DeferredResolver.fetchers(SchemaDefinition.nodes))

    Await.result(futureResult, 10.seconds)
  }
}
