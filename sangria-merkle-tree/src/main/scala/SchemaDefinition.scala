import sangria.execution.deferred.{Fetcher, HasId}
import sangria.schema._

import scala.concurrent.Future

/**
 * Defines a GraphQL schema for the current project
 */
object SchemaDefinition {
  /**
    * Resolves the lists of characters. These resolutions are batched and
    * cached for the duration of a query.
    */
  val nodes = Fetcher.caching(
    (ctx: NodeRepo, ids: Seq[String]) =>
      Future.successful(ids.flatMap(id => ctx.getRoot(id) orElse ctx.getHash(id))))(HasId(_.data))

  val NodeTypeEnum = EnumType(
    "NodeType",
    Some("node type"),
    List(
      EnumValue("ROOT",
        value = NodeType.ROOT,
        description = Some("Root")),
      EnumValue("HASH",
        value = NodeType.HASH,
        description = Some("Hash")),
      EnumValue("LEAF",
        value = NodeType.LEAF,
        description = Some("Leaf"))))

  val Node: InterfaceType[NodeRepo, Node] =
    InterfaceType(
      "Node",
      "Generic Node",
      () => fields[NodeRepo, Node](
        Field("data", StringType,
          Some("The data."),
          resolve = _.value.data),
        Field("name", OptionType(StringType),
          Some("The name."),
          resolve = _.value.name),
        Field("type", NodeTypeEnum,
          Some("The type."),
          resolve = _.value.t),
      ))

  val NodeRoot =
    ObjectType(
      "NodeRoot",
      "Root node",
      interfaces[NodeRepo, NodeRoot](Node),
      fields[NodeRepo, NodeRoot](
        Field("data", StringType,
          Some("data"),
          resolve = _.value.data),
        Field("name", OptionType(StringType),
          Some("name"),
          resolve = _.value.name),
        Field("type", NodeTypeEnum,
          Some("type."),
          resolve = _.value.t),
        
      ))

  val NodeHash = ObjectType(
    "NodeHash",
    "Hash node",
    interfaces[NodeRepo, NodeHash](Node),
    fields[NodeRepo, NodeHash](
      Field("data", StringType,
        Some("The data."),
        resolve = _.value.data),
      Field("name", OptionType(StringType),
        Some("The name "),
        resolve = ctx => Future.successful(ctx.value.name)),
      
      Field("type", NodeTypeEnum,
          Some("The type."),
          resolve = _.value.t),
    ))

  val NodeLeaf = ObjectType(
    "NodeLeaf",
    "Leaf node",
    interfaces[NodeRepo, NodeLeaf](Node),
    fields[NodeRepo, NodeLeaf](
      Field("data", StringType,
        Some("The data."),
        resolve = _.value.data),
      Field("name", OptionType(StringType),
        Some("The name "),
        resolve = ctx => Future.successful(ctx.value.name)),
      
      Field("type", NodeTypeEnum,
          Some("The type."),
          resolve = _.value.t),
    ))

  val DATA = Argument("data", StringType, description = "data")

  val NodeTypeArg = Argument("type", OptionInputType(NodeTypeEnum),
    description = "If omitted, returns the hero of the whole saga. If provided, returns the hero of that particular episode.")

  val LimitArg = Argument("limit", OptionInputType(IntType), defaultValue = 20)
  val OffsetArg = Argument("offset", OptionInputType(IntType), defaultValue = 0)

  val DataTypeArg = Argument("data", OptionInputType(StringType), defaultValue = "")

  val Query = ObjectType(
    "Query", fields[NodeRepo, Unit](

      Field("all", ListType(Node),
        arguments = Nil,
        resolve = (ctx) => ctx.ctx.getAll()),

      Field("nodes", ListType(Node),
        arguments = LimitArg :: DataTypeArg :: Nil,
        resolve = ctx => ctx.ctx.genNodes(ctx arg LimitArg, ctx arg DataTypeArg)),

      Field("findNodes", ListType(Node),
        arguments = DATA :: Nil,
        resolve = ctx => ctx.ctx.findNodes(ctx arg DATA)),

      // Field("type", Node,
      //   arguments = NodeTypeArg :: Nil,
      //   resolve = (ctx) => ctx.ctx.getNode(ctx.arg(NodeTypeArg))),

      Field("node", OptionType(Node),
        arguments = DATA :: Nil,
        resolve = (ctx) => ctx.ctx.getNode(ctx arg DATA)),

      Field("root", OptionType(NodeRoot),
        arguments = DATA :: Nil,
        resolve = ctx => ctx.ctx.getRoot(ctx arg DATA)),

      Field("hash", OptionType(NodeHash),
        arguments = DATA :: Nil,
        resolve = ctx => ctx.ctx.getHash(ctx arg DATA)),
      
      Field("leaf", OptionType(NodeLeaf),
        arguments = DATA :: Nil,
        resolve = ctx => ctx.ctx.getLeaf(ctx arg DATA)),
      

      Field("roots", ListType(NodeRoot),
        arguments = LimitArg :: OffsetArg :: Nil,
        resolve = ctx => ctx.ctx.getRoots(ctx arg LimitArg, ctx arg OffsetArg)),
      Field("hashes", ListType(NodeHash),
        arguments = LimitArg :: OffsetArg :: Nil,
        resolve = ctx => ctx.ctx.getHashes(ctx arg LimitArg, ctx arg OffsetArg)),
      Field("leaves", ListType(NodeLeaf),
        arguments = LimitArg :: OffsetArg :: Nil,
        resolve = ctx => ctx.ctx.getLeaves(ctx arg LimitArg, ctx arg OffsetArg))
    ))

  val NodesSchema = Schema(Query)
}
