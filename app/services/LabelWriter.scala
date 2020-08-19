package services

import java.io.File

import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.{PdfFont, PdfFontFactory}
import com.itextpdf.kernel.pdf.{PdfDocument, PdfWriter}
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.{Paragraph, Table}
import com.itextpdf.layout.property.UnitValue
import models.Label
import play.api.Logging

object LabelWriter extends Logging {
  // PostScript points per centimetre
  private final val PT_PER_CM = 28.3464566929f
  private final val LABEL_WIDTH = UnitValue.createPointValue(1.7f * PT_PER_CM)
  private final val LABEL_HEIGHT = UnitValue.createPointValue(0.7f * PT_PER_CM)
  private final val LABEL_FONT = StandardFonts.TIMES_ROMAN
  // Height of label minus some padding (1 pt), divided by four lines
  // This is also used as fixed leading in paragraphs (so lines remain horizontally aligned)
  private final val LABEL_MAX_FONT_SIZE = LABEL_HEIGHT.getValue / 4f

  private def computeOptimalFontSize(font: PdfFont, text: Label) = {
    // Get width of longest line
    // Result is in thousands of user pt
    val longestGlyphWidth = text.lines.map(font.getWidth).max / 1e3f
    val fontSize = LABEL_WIDTH.getValue / longestGlyphWidth

    logger.debug(f"fontSize: $fontSize, maxFontSize: $LABEL_MAX_FONT_SIZE")
    math.min(fontSize, LABEL_MAX_FONT_SIZE)
  }

  def createLabels(content: Seq[Label], dest: File): Unit = {
    // Created font is document specific and cannot be reused
    val font = PdfFontFactory.createFont(LABEL_FONT)
    val pdfDoc = new PdfDocument(new PdfWriter(dest))
    val doc = new Document(pdfDoc)

    val table = new Table(10)
    doc.add(table)

    content.foreach( lbl => {
      val fontSize = computeOptimalFontSize(font, lbl)
      val para = new Paragraph(lbl.paragraph)
        .setFont(font)
        .setFontSize(fontSize)
        .setWidth(LABEL_WIDTH)
        .setHeight(LABEL_HEIGHT)
        .setFixedLeading(LABEL_MAX_FONT_SIZE)
      table.addCell(para)
    })
    table.complete()

    doc.close()
    pdfDoc.close()
  }
}
