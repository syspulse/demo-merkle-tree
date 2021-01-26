import scala.concurrent.duration._
import scala.util.{Failure, Success}

import sangria.execution.deferred.DeferredResolver
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.slowlog.SlowLog

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._

import io.circe.Json
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport.jsonMarshaller
import sangria.marshalling.circe._

import SangriaAkkaHttp._

object Server extends App with CorsSupport {
  implicit val system = ActorSystem("sangria-server")

  import system.dispatcher

  val route: Route =
    optionalHeaderValueByName("X-Apollo-Tracing") { tracing =>
      pathPrefix("playground") {
        pathEndOrSingleSlash {
          graphQLPlayground
      }} ~
      path("graphql") {
        prepareGraphQLRequest {
          case Success(GraphQLRequest(query, variables, operationName)) =>
            val middleware = if (tracing.isDefined) SlowLog.apolloTracing :: Nil else Nil
            val deferredResolver = DeferredResolver.fetchers(SchemaDefinition.nodes)
            val graphQLResponse = Executor.execute(
                schema = SchemaDefinition.NodesSchema,
                queryAst = query,
                userContext = new NodeRepo,
                variables = variables,
                operationName = operationName,
                middleware = middleware,
                deferredResolver = deferredResolver
              ).map(OK -> _)
               .recover {
                 case error: QueryAnalysisError => BadRequest -> error.resolveError
                 case error: ErrorWithResolver => InternalServerError -> error.resolveError
               }
            complete(graphQLResponse)
          case Failure(preparationError) => complete(BadRequest, formatError(preparationError))
        }
      }
    } ~
    (get & pathEndOrSingleSlash) {
      redirect("/graphql", PermanentRedirect)
    }

  val port = sys.props.get("http.port").fold(8081)(_.toInt)
  val host = sys.props.get("http.host").fold("0.0.0.0")(_.toString)
  println(s"Running on ${host}:${port}...")
  Http().newServerAt(host, port).bindFlow(corsHandler(route))
}
