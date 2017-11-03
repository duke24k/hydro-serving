package io.hydrosphere.serving.manager.service

import io.hydrosphere.serving.manager.test.CommonIntegrationSpec

import scala.concurrent.Await
import scala.concurrent.duration._

class UIManagementServiceSpecs extends CommonIntegrationSpec {

  describe("UIManagementService") {
    val uiService = managerServices.uiManagementService

    it("should gell all models") {
      val f = uiService.allModelsWithLastStatus().map{ models =>
        assert(models.size === 1)
      }
      Await.result(f, 1.minute)
    }

    it("should build model") {
      val f = uiService.buildModel(1, Some("0.0.1")).map{ info =>
        assert(info.currentServices.nonEmpty)
      }
      Await.result(f, 1.minute)
    }
  }

}
