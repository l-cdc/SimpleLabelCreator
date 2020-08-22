package services

import java.io.File

import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.{PdfFont, PdfFontFactory}
import com.itextpdf.kernel.pdf.{PdfDocument, PdfWriter}
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.{Cell, Paragraph, Table}
import com.itextpdf.layout.property.UnitValue
import models.Label
import play.api.Logging

object LabelWriter extends Logging {
  // PostScript points per centimetre
  private final val PT_PER_CM = 28.3464566929f
  private final val LABEL_WIDTH = UnitValue.createPointValue(1.7f * PT_PER_CM)
  private final val LABEL_HEIGHT = UnitValue.createPointValue(0.7f * PT_PER_CM)
  private final val LABEL_FONT = StandardFonts.TIMES_ROMAN
  /** Max font size due to height of label
   *
   * Height of label minus some padding, divided by four lines
   * This is also used as fixed leading in paragraphs (so lines of
   * different font sizes remain horizontally aligned)
   */
  private final val LABEL_MAX_FONT_SIZE = (LABEL_HEIGHT.getValue - 0.6f) / 4f
  private final val LABELS_PER_ROW = 10

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

    val table = new Table(UnitValue.createPercentArray(LABELS_PER_ROW))
      .setWidth(LABELS_PER_ROW*LABEL_WIDTH.getValue)
      .setFixedLayout()

    doc.add(table)

    content.foreach( lbl => {
      val fontSize = computeOptimalFontSize(font, lbl)
      val para = new Paragraph(lbl.paragraph)
        .setFont(font)
        .setFontSize(fontSize)
        .setFixedLeading(LABEL_MAX_FONT_SIZE)
        .setMargin(0)

      val cell = new Cell()
        .add(para)
        .setHeight(LABEL_HEIGHT)
        .setPadding(1)

      table.addCell(cell)
    })

    // Complete the last row with empty cells
    val rem = content.length % LABELS_PER_ROW
    if (rem != 0)
      (rem + 1 to LABELS_PER_ROW) foreach (_ => table.addCell(""))

    table.complete()

    doc.close()
    pdfDoc.close()
  }
}
