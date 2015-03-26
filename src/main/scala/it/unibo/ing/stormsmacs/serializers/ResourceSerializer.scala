package it.unibo.ing.stormsmacs.serializers

import java.sql.Timestamp

import com.esotericsoftware.kryo.io.{Output, Input}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import com.twitter.chill.ScalaKryoInstantiator
import org.openstack.api.restful.ceilometer.v2.elements.Resource
import org.openstack.api.restful.elements.Link

/**
 * Created by tmnd91 on 25/03/15.
 */

class ResourceSerializer extends Serializer[Resource]{
  import spray.json._
  import org.openstack.api.restful.ceilometer.v2.elements.JsonConversions._
  val instantiator = new ScalaKryoInstantiator()
  val myKryo = instantiator.newKryo()
  override def write(kryo: Kryo, output: Output, t: Resource): Unit = {
    myKryo.writeObject(output,if (t.first_sample_timestamp.isDefined) Some(t.first_sample_timestamp.get.getTime) else None)
    myKryo.writeObject(output,if (t.last_sample_timestamp.isDefined) Some(t.last_sample_timestamp.get.getTime) else None)
    myKryo.writeObject(output,t.links)
    myKryo.writeObject(output,t.metadata)
    myKryo.writeObject(output,t.project_id)
    myKryo.writeObject(output,t.resource_id)
    myKryo.writeObject(output,t.source)
    myKryo.writeObject(output,t.user_id)
  }

  override def read(kryo: Kryo, input: Input, aClass: Class[Resource]): Resource = {
    val firstSample = myKryo.readObject(input,classOf[Some[Long]])
    val lastSample = myKryo.readObject(input,classOf[Some[Long]])
    Resource( first_sample_timestamp = (if (firstSample != None) Some(new Timestamp(firstSample.get)) else None),
              last_sample_timestamp  = (if (firstSample != None) Some(new Timestamp(lastSample.get)) else None),
              links = myKryo.readObject(input,classOf[Seq[Link]]),
              metadata = myKryo.readObject(input, classOf[Map[String,String]]),
              project_id = myKryo.readObject(input, classOf[Some[String]]),
              resource_id = myKryo.readObject(input, classOf[String]),
              source = myKryo.readObject(input, classOf[String]),
              user_id = myKryo.readObject(input, classOf[Option[String]])
    )
  }
}
