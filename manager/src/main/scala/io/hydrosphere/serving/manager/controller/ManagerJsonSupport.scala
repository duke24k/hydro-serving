package io.hydrosphere.serving.manager.controller

import io.hydrosphere.serving.manager.model._
import io.hydrosphere.serving.manager.service._
import io.hydrosphere.serving.model._
import spray.json.{DeserializationException, JsString, JsValue, RootJsonFormat}
import spray.json._

/**
  *
  */
trait ManagerJsonSupport extends CommonJsonSupport {
  implicit val modelBuildStatusFormat = new EnumJsonConverter(ModelBuildStatus)
  implicit val modelServiceInstanceStatusFormat = new EnumJsonConverter(ModelServiceInstanceStatus)

  implicit val modelFormat = jsonFormat9(Model)

  implicit val buildModelRequestFormat = jsonFormat3(BuildModelRequest)
  implicit val buildModelByNameRequest = jsonFormat3(BuildModelByNameRequest)

  implicit val modelBuildFormat = jsonFormat9(ModelBuild)

  implicit val modelServiceInstanceFormat = jsonFormat8(ModelServiceInstance)

  implicit val createModelServiceRequest = jsonFormat4(CreateModelServiceRequest)

  implicit val createRuntimeTypeRequest = jsonFormat4(CreateRuntimeTypeRequest)

  implicit val createOrUpdateModelRequest = jsonFormat7(CreateOrUpdateModelRequest)

  implicit val createModelRuntime = jsonFormat12(CreateModelRuntime)

  implicit val updateModelRuntime = jsonFormat7(UpdateModelRuntime)

  implicit val applicationCreateOrUpdateRequest = jsonFormat4(ApplicationCreateOrUpdateRequest)

  implicit val awsAuthFormat = jsonFormat2(AWSAuthKeys.apply)

  implicit val localSourceParamsFormat = jsonFormat1(LocalSourceParams.apply)

  implicit val s3SourceParamsFormat = jsonFormat4(S3SourceParams.apply)

  implicit val sourceParamsFormat = new JsonFormat[SourceParams] {
    override def read(json: JsValue) = {
      json match {
        case JsObject(fields) if fields.isDefinedAt("path") =>
          LocalSourceParams(fields("path").convertTo[String])
        case JsObject(fields) if fields.isDefinedAt("queueName") && fields.isDefinedAt("bucketName") =>
          S3SourceParams(
            awsAuth = fields("awsAuth").convertTo[Option[AWSAuthKeys]],
            bucketName = fields("bucketName").convertTo[String],
            queueName = fields("queueName").convertTo[String],
            region = fields("region").convertTo[String]
          )
      }
    }

    override def write(obj: SourceParams) = {
      obj match {
        case x: LocalSourceParams => x.toJson
        case x: S3SourceParams => x.toJson
        case _ => ???
      }
    }
  }

  implicit val modelSourceConfigFormat = jsonFormat3(ModelSourceConfigAux.apply)

  implicit val createModelSourceRequestFormat = jsonFormat2(CreateModelSourceRequest.apply)

  implicit val createServingEnvironmentFormat = jsonFormat2(CreateServingEnvironment)

}
