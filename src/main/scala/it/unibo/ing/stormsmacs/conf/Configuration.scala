package it.unibo.ing.stormsmacs.conf

/**
 * @author Antonio Murgia
 * @version 10/03/2015
 * Abstract representation of stormsmacs configuration
 *
 */
abstract class Configuration{
  val name              : String
  val openstackNodes    : Option[Seq[OpenStackNodeConf]]
  val cloudfoundryNodes : Option[Seq[CloudFoundryNodeConf]]
  val genericNodes      : Option[Seq[GenericNodeConf]]
  val persisterNode     : PersisterNodeConf
  val remote            : Boolean
  val debug             : Boolean
  val pollTime          : Long
  val openstackNodeList = openstackNodes.getOrElse(List.empty)
  val cloudfoundryNodeList = cloudfoundryNodes.getOrElse(List.empty)
  val genericNodeList = genericNodes.getOrElse(List.empty)

  override def toString = "openstackNodes =" + openstackNodeList.mkString("{\n",",\n\t","}\n") +
                          "cloudfoundryNodes =" + cloudfoundryNodeList.mkString("{\n",",\n\t","}\n") +
                          "genericNodes =" + genericNodeList.mkString("{\n",",\n\t","}\n") +
                          "persisterNode = " + persisterNode + "\n" +
                          "debug = " + debug + "\n" +
                          "pollTime = " + pollTime
}