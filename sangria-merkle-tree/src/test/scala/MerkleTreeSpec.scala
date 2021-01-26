import org.scalatest.{Matchers, WordSpec}


class MerkleTreeSpec extends WordSpec with Matchers {


  "MerkleTree" should {
    "of (1,2) should be 3 nodes" in {
      val data = (1 to 2)
      val t = new MerkleTree(data).getTree 
      assert(t.size === 3)
      t shouldBe List(Sha2.hash("1"+"2"),"1","2")
    }
    "of (1,2,3,4) should be 7 nodes" in {
      val data = (1 to 4)
      val t = new MerkleTree(data).getTree 
      assert(t.size === 7)
      t shouldBe List("88cd668c2056e926cf9f6dad3acbeebf0c1e093da5ab7aceb244e65661d7e35e", "6b51d431df5d7f141cbececcf79edf3dd861c3b4069f0b11661a3eefacbba918", "86e50149658661312a9e0b35558d84f6c6d3da797f552a9657fe0558ca40cdef", "1", "2", "3", "4")
    }
    "of (1,2,3,4,5) should be 8 nodes" in {
      val data = (1 to 5)
      val t = new MerkleTree(data).getTree 
      assert(t.size === 15)
      t shouldBe List(
      "98b056932d51e88d931e1685c9e44835367ccfd1660adb3899e150072a3a90e0", 
      "88cd668c2056e926cf9f6dad3acbeebf0c1e093da5ab7aceb244e65661d7e35e", "3bcf81da18c3a06070dd115fd97e801192b100b798a397004604356a6da2995a",
      "6b51d431df5d7f141cbececcf79edf3dd861c3b4069f0b11661a3eefacbba918", "86e50149658661312a9e0b35558d84f6c6d3da797f552a9657fe0558ca40cdef", "1a6562590ef19d1045d06c4055742d38288e9e6dcd71ccde5cee80f1d5a774eb", "f1534392279bddbf9d43dde8701cb5be14b82f76ec6607bf8d6ad557f60f304e",
      "1", "2", "3", "4", "5", "0", "0", "0")
    }
    
  }
}
