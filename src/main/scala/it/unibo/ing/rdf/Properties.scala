package it.unibo.ing.rdf

import com.hp.hpl.jena.rdf.model.ModelFactory
/**
 * Created by tmnd91 on 24/12/14.
 */
object Properties {
  private val m = ModelFactory.createDefaultModel()
  val NS = "http://ing.unibo.it/smacs/predicates#"
  val prefixes: Map[String, String] = Map("sp"->NS)
  val hasStatus = m.createProperty(NS + "hasStatus")
  val hasMonitoringStatus = m.createProperty(NS + "hasMonitoringStatus")
  val hasPid = m.createProperty(NS + "hasPid")
  val hasParentPid = m.createProperty(NS + "hasParentPid")
  val hasUptime = m.createProperty(NS + "hasUptime")
  val hasChildren = m.createProperty(NS + "hasChildren")
  val usingMemoryKb = m.createProperty(NS + "usingMemoryKb")
  val totalMemoryKb = m.createProperty(NS + "totalMemoryKb")
  val usingMemoryPerc = m.createProperty(NS + "usingMemoryPerc")
  val totalMemoryPerc = m.createProperty(NS + "totalMemoryPerc")
  val usingCPUperc = m.createProperty(NS + "usingCPUperc")
  val totalCPUperc = m.createProperty(NS + "totalCPUperc")
  val dataCollected = m.createProperty(NS + "dataCollected")
  val portResponseTime = m.createProperty(NS + "portResponseTime")
  val unixSocketResponseTime = m.createProperty(NS + "unixSocketResponseTime")
}


/*
INSERT DATA
{ GRAPH <http://example/bookStore> {
<http://192.168.1.10> <http://www.w3.org/2001/vcard-rdf/3.0#ADR> "bla" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#hasChildren> "4" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#dataCollected> "2014-12-24T10:22:30Z" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#unixSocketResponseTime> "None" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#hasParentPid> "1" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#hasMonitoringStatus> "monitored" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#portResponseTime> "None" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#totalCPUperc> "2.2" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#hasPid> "3" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#usingCPUperc> "2.1" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#hasUptime> "3 seconds" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#totalMemoryKb> "2000" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#usingMemoryPerc> "212.2" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#hasStatus> "running" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#totalMemoryPerc> "212.2" . } }
 */