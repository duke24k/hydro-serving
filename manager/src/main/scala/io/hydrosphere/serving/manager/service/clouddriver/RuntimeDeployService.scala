package io.hydrosphere.serving.manager.service.clouddriver

import io.hydrosphere.serving.manager.model.ModelServiceInstance
import io.hydrosphere.serving.model.ModelService


case class ServiceInfo(
  id: Long,
  name: String,
  cloudDriveId: String,
  status: String,
  statusText: String,
  configParams: Map[String, String]
)

trait RuntimeDeployService {
  val ENV_ZIPKIN_ENABLED = "ZIPKIN_ENABLED"
  val ENV_ZIPKIN_HOST = "ZIPKIN_HOST"
  val ENV_ZIPKIN_PORT = "ZIPKIN_PORT"
  val ENV_MANAGER_HOST = "MANAGER_HOST"
  val ENV_MANAGER_PORT = "MANAGER_PORT"

  val ENV_HS_SERVICE_ID = "HS_SERVICE_ID"
  val ENV_SIDECAR_HTTP_PORT = "SIDECAR_HTTP_PORT"
  val ENV_APP_HTTP_PORT = "APP_HTTP_PORT"
  val ENV_SIDECAR_ADMIN_PORT = "SIDECAR_ADMIN_PORT"

  val DEFAULT_APP_HTTP_PORT = 9090
  val DEFAULT_SIDECAR_HTTP_PORT = 8080
  val DEFAULT_SIDECAR_ADMIN_PORT = 8082

  val LABEL_SERVICE_ID = "hydroServingServiceId"
  val LABEL_HS_SERVICE_MARKER = "HS_SERVICE_MARKER"
  val LABEL_MODEL_VERSION = "MODEL_VERSION"
  val LABEL_MODEL_NAME = "MODEL_NAME"
  val LABEL_RUNTIME_TYPE_NAME = "RUNTIME_TYPE_NAME"
  val LABEL_RUNTIME_TYPE_VERSION = "RUNTIME_TYPE_VERSION"


  def deploy(runtime: ModelService, placeholders: Seq[Any]): String

  def serviceList(): Seq[ServiceInfo]

  def service(serviceId: Long): Option[ServiceInfo]

  def deleteService(serviceId: Long)

  def serviceInstances(serviceId: Long): Seq[ModelServiceInstance]

}
