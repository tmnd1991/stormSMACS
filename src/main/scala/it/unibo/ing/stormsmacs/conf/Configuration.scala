package it.unibo.ing.stormsmacs.conf

abstract class Configuration{
  val name              : String
  val openstackNodes    : Option[Seq[OpenStackNode]]
  val cloudfoundryNodes : Option[Seq[CloudFoundryNode]]
  val genericNodes      : Option[Seq[GenericNode]]
  val fusekiNode        : FusekiNode
  val debug             : Boolean
  val pollTime          : Long
  val openstackNodeList = openstackNodes.getOrElse(List.empty)
  val cloudfoundryNodeList = cloudfoundryNodes.getOrElse(List.empty)
  val genericNodeList = genericNodes.getOrElse(List.empty)

  override def toString = "openstackNodes =" + openstackNodeList.mkString("{\n",",\n\t","}\n") +
                          "cloudfoundryNodes =" + cloudfoundryNodeList.mkString("{\n",",\n\t","}\n") +
                          "genericNodes =" + genericNodeList.mkString("{\n",",\n\t","}\n") +
                          "fusekiNode = " + fusekiNode + "\n" +
                          "debug = " + debug + "\n" +
                          "pollTime = " + pollTime
}