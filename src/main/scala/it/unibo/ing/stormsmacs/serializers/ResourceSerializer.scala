package it.unibo.ing.stormsmacs.serializers

import java.net.URL
import java.sql.Timestamp

import com.esotericsoftware.kryo.io.{Output, Input}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import com.twitter.chill.ScalaKryoInstantiator
import org.openstack.api.restful.ceilometer.v2.elements.Resource
import org.openstack.api.restful.elements.Link
import scala.collection.JavaConverters._

/**
 * Created by tmnd91 on 25/03/15.
 */

class ResourceSerializer extends Serializer[Resource]{
  import spray.json._
  import org.openstack.api.restful.ceilometer.v2.elements.JsonConversions._
  val instantiator = new ScalaKryoInstantiator()
  val myKryo = instantiator.newKryo()

  override def write(kryo: Kryo, output: Output, t: Resource): Unit = {
    output.writeLong(t.first_sample_timestamp.getOrElse(new Timestamp((0))).getTime)
    output.writeLong(t.last_sample_timestamp.getOrElse(new Timestamp((0))).getTime)
    //kryo.writeObject(output,t.links.toArray)
    myKryo.writeObject(output,t.metadata)
    output.writeString(t.project_id.getOrElse("_"))
    myKryo.writeObject(output,t.resource_id)
    myKryo.writeObject(output,t.source)
    output.writeString(t.user_id.getOrElse("_"))
  }

  override def read(kryo: Kryo, input: Input, aClass: Class[Resource]): Resource = {
    val firstSample = input.readLong()
    val lastSample = input.readLong()
    Resource( first_sample_timestamp = (if (firstSample != 0) Some(new Timestamp(firstSample)) else None),
              last_sample_timestamp  = (if (lastSample != 0) Some(new Timestamp(lastSample)) else None),
              links = Seq(),//kryo.readObject(input,classOf[Array[Link]]),
              metadata = myKryo.readObject(input, classOf[Map[String,String]]),
              project_id = Some(input.readString()),
              resource_id = myKryo.readObject(input, classOf[String]),
              source = myKryo.readObject(input, classOf[String]),
              user_id = Some(input.readString())
    )
  }
}
class LinkSerializer extends Serializer[Link]{
  override def write(kryo: Kryo, output: Output, t: Link): Unit = {
    output.writeString(t.href.toString)
    output.writeString(t.rel)
    output.writeString(t.contentType.getOrElse("_"))
  }

  override def read(kryo: Kryo, input: Input, aClass: Class[Link]): Link = {
    val url = new URL(input.readString())
    val rel = input.readString()
    val ctype = input.readString()
    Link(href = url,
        contentType = if (ctype == "_") None else Some(ctype),
        rel = rel)
  }
}
