package services

import java.io.File

import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.{PdfDocument, PdfWriter}
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.{Paragraph, Table}
import com.itextpdf.layout.property.UnitValue
import models.Label

object LabelWriter {
  // PostScript points per centimetre
  private final val PT_PER_CM = 28.3464566929f
  private final val LABEL_WIDTH = UnitValue.createPointValue(1.7f * PT_PER_CM)
  private final val LABEL_HEIGHT = UnitValue.createPointValue(0.7f * PT_PER_CM)
  private final val LABEL_FONT =  PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN)
  private final val LABEL_FONT_SIZE =  3f

  def createLabels(content: Seq[Label], dest: File): Unit = {
    val pdfDoc = new PdfDocument(new PdfWriter(dest))
    val doc = new Document(pdfDoc)

    val table = new Table(10)
    doc.add(table)

    content.foreach( lbl => {
      val para = new Paragraph(lbl.paragraph)
        .setFontSize(LABEL_FONT_SIZE)
        .setFont(LABEL_FONT)
        .setWidth(LABEL_WIDTH)
        .setHeight(LABEL_HEIGHT)
      table.addCell(para)
    })
    table.complete()

    doc.close()
    pdfDoc.close()
  }
}
