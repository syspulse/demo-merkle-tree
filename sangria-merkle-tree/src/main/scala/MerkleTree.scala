import java.security.SecureRandom
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import scala.math._

object Sha2 {
  val random = new SecureRandom
  def getSalt: Array[Byte] = Some("salt".getBytes()).getOrElse({ val bb = new Array[Byte](16); random.nextBytes(bb); bb })
  def toHexString(b:Array[Byte]) = b.foldLeft("")((s,b)=>s + f"$b%02x")
  val digest = MessageDigest.getInstance("SHA-256");
  
  def hash(value:String):String = toHexString(digest.digest(value.getBytes(StandardCharsets.UTF_8)))
}

class MerkleTree(data:Seq[Any]) {
  def ln(v:Double) = log(v) / log(2)

  def builder(data:Seq[Any]):List[String] = {
    //println(s"data=${data.toList}")
    data.size match {
      case 0 => List()
      case 1 => List(Sha2.hash(data.head.toString))
      case 2 => List(Sha2.hash(data.head.toString + data.tail.head.toString))
      case n => {
        val gg = data.grouped(2).map( g=> {
          builder(g)
        }).flatten.toList
        builder( gg ) ++ gg
      }
    }
  }

  def build(data:Seq[Any]):List[String] = {
    if(data.size==0) return List()

    val pad = if(data.size==1) 1 else (pow(2,ceil(ln(data.size))) - data.size).toInt
    
    val dataBalanced = if(pad > 0) data ++ Range(0,pad).map(_=>0).toList else data

    val result = builder(dataBalanced) ++ dataBalanced.map(_.toString)
    //println(s"result=${result.toList}")
    result
  }

  val tree:List[String] = build(data)

  def getTree:List[String] = tree
}
