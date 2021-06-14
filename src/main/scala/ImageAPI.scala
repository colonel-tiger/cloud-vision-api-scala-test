import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1._
import com.google.api.services.vision.v1.model._
import com.google.common.collect.ImmutableList

import java.util.List
import java.nio.file._

object ImageAPI {

  def createVision(appName: String): Vision = {
    val transport = GoogleNetHttpTransport.newTrustedTransport()
    val jsonFactory = JacksonFactory.getDefaultInstance
    val defaultCredential = GoogleCredential.getApplicationDefault
    val credential = defaultCredential.createScoped(VisionScopes.all)

    val builder = new Vision.Builder(transport, jsonFactory, credential)
    builder.setApplicationName(appName).build()
  }

  def createAnnotate(
      vision: Vision,
      path: String,
      maxResults: Int
  ): Vision#Images#Annotate = {
    val data = Files.readAllBytes(Paths.get(path))
    val image = new Image().encodeContent(data)
    val feature =
      new Feature().setType("DOCUMENT_TEXT_DETECTION").setMaxResults(maxResults)
    val request = new AnnotateImageRequest()
      .setImage(image)
      .setFeatures(ImmutableList.of(feature))
    val batch =
      new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request))
    val annotate = vision.images.annotate(batch)
    annotate.setDisableGZipContent(true)
    annotate
  }

  def detectTexts(annotate: Vision#Images#Annotate): List[EntityAnnotation] = {
    val batchResponse = annotate.execute()
    val response = batchResponse.getResponses.get(0)
    val texts = response.getTextAnnotations
    if (texts == null) {
      if (response.getError != null) {
        println(response.getError.getMessage)
      } else {
        println("unknown error")
      }
    }
    texts
  }
}
