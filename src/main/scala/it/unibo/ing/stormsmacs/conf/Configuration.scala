package it.unibo.ing.stormsmacs.conf

/**
 * Example of JSON configuration file
 * {
 *     "name"              : "test",
 *     "cloudfoundryNodes" : [{
 *                               "id" : "cf_cf",
 *                               "url" : "http://10.0.10.11:9876",
 *                               "connect-timeout" : 10000,
 *                               "read-timeout" : 10000
 *                          }],
 *     "genericNodes"      : [{
 *                               "id" : "cf_generic",
 *                               "url" : "http://10.0.10.11:9875",
 *                               "connect-timeout" : 10000,
 *                               "read-timeout" : 10000
 *                           }],
 *     "fusekiNode"        : {
 *                                   "id"  : "papap",
 *                                   "url" : "http://localhost:3030"
 *                           },
 *     "remote"            : false,
 *     "debug"             : false,
 *     "pollTime"          : 4000
 * }
 */
abstract class Configuration{
  val name              : String
  val openstackNodes    : Option[Seq[OpenStackNodeConf]]
  val cloudfoundryNodes : Option[Seq[CloudFoundryNodeConf]]
  val genericNodes      : Option[Seq[GenericNodeConf]]
  val fusekiNode        : FusekiNodeConf
  val remote            : Boolean
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