package io.hydrosphere.spark_runtime

import io.hydrosphere.mist.api.ml.LocalPipelineModel

/**
  * Created by Bulat on 07.06.2017.
  */
case class ModelEntry(pipeline: LocalPipelineModel, metadata: SparkMetadata)