object NodeType extends Enumeration {
  val ROOT, HASH, LEAF = Value
}

trait Node {
  def data: String
  def name: Option[String]
  def t: NodeType.Value
}

case class NodeRoot(
  data: String,
  name: Option[String],
  t: NodeType.Value = NodeType.ROOT
  ) extends Node

case class NodeHash(
  data: String,
  name: Option[String],
  t: NodeType.Value = NodeType.HASH
  ) extends Node

case class NodeLeaf(
  data: String,
  name: Option[String],
  t: NodeType.Value = NodeType.LEAF
  ) extends Node


class NodeRepo {
  import NodeRepo._

  def getFromData(data:Seq[Any]): List[Node] = {
    if(data.size==0) return List()

    val mt = new MerkleTree(data).getTree
      List(NodeRoot(mt.head,Some("root"))) ++ 
      mt.tail.take(mt.size / 2).map(data => NodeHash(data,Some("hash"))) ++
      mt.tail.drop(mt.size / 2).map(v => NodeLeaf(v.toString,Some("leaf")))
  }

  def getAll():List[Node] =  nodes

  def genNodes(limit: Int, dataType:String=""): List[Node] = getFromData(
    dataType match {
      case "random" => (1 to limit).map(v => scala.util.Random.nextInt())
      case "" => (1 to limit)
    }  
  )

  def findNodes(data:String): List[Node] = nodes.filter(_.data == data).asInstanceOf[List[Node]]

  def getNode(data: String): Option[Node] = nodes.find(c => c.data == data)

  def getRoot(data: String): Option[NodeRoot] = nodes.find(c => c.data == data && c.t == NodeType.ROOT).map(_.asInstanceOf[NodeRoot])

  def getHash(data: String): Option[NodeHash] = nodes.find(c => c.data == data && c.t == NodeType.HASH).map(_.asInstanceOf[NodeHash])

  def getLeaf(data: String): Option[NodeLeaf] = nodes.find(c => c.data == data).filter(_.t == NodeType.LEAF).map(_.asInstanceOf[NodeLeaf])
  
  def getRoots(limit: Int, offset: Int): List[NodeRoot] = nodes.drop(offset).take(limit).filter(_.t == NodeType.ROOT).asInstanceOf[List[NodeRoot]]
  
  def getHashes(limit: Int, offset: Int): List[NodeHash] = nodes.drop(offset).take(limit).filter(_.t == NodeType.HASH).asInstanceOf[List[NodeHash]]

  def getLeaves(limit: Int, offset: Int): List[NodeLeaf] = nodes.drop(offset).take(limit).filter(_.t == NodeType.LEAF).asInstanceOf[List[NodeLeaf]]
}

object NodeRepo {

  val defaultNodes:List[Node] = List(
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

  var nodes = defaultNodes
  
  def apply(nodes:List[Node] = defaultNodes):NodeRepo = {
    this.nodes = nodes
    new NodeRepo
  }
  
}
