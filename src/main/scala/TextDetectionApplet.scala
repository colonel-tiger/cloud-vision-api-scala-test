import com.google.api.services.vision.v1.model._
import scala.collection.JavaConverters._
import processing.core._

class TextDetectionApplet extends PApplet {
  val src = "ファイルパスを書く"
  override def settings() {
    size(1400, 1000)
  }

  override def setup() {
    background(20)

    val img = loadImage(src)
    image(img, 0, 0, img.width / 2, img.height / 2)

    val vision = ImageAPI.createVision("text detection")
    val annotate = ImageAPI.createAnnotate(vision, src, 10)
    val texts = ImageAPI.detectTexts(annotate)

    textSize(20)
    fill(50, 250, 150)
    for (text <- texts.asScala) {
      drawText(text)
    }
  }

  def drawText(text: EntityAnnotation) {
    val p0 = text.getBoundingPoly.getVertices.get(0)
    val desc = text.getDescription
    println(desc)
    if (!desc.contains(" ")) {
      this.text(desc, p0.getX / 2, p0.getY / 2)
    }
  }
}
